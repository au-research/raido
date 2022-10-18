package raido.apisvc.endpoint.raidv2;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.RaidService;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.model.MintRaidRequestV1;
import raido.idl.raidv2.model.RaidListItemV1;
import raido.idl.raidv2.model.RaidListRequest;
import raido.idl.raidv2.model.ReadRaidResponseV1;
import raido.idl.raidv2.model.ReadRaidV1Request;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.Constant.MAX_EXPERIMENTAL_RECORDS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static raido.apisvc.util.DateUtil.local2Offset;
import static raido.apisvc.util.DateUtil.offset2Local;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;
import static raido.db.jooq.api_svc.tables.Raid.RAID;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class BasicRaidExperimental implements BasicRaidExperimentalApi {
  private static final Log log = to(BasicRaidExperimental.class);
  /* Hardcoded, we know this statically because we hardcoded the sequence to
   20M and raido is the first SP inserted */
  public static final long RAIDO_SP_ID = 20_000_000;

  private DSLContext db;
  private ApidsService apidsSvc;
  private EnvironmentProps envProps;
  private RaidService raidSvc;
  
  public BasicRaidExperimental(
    DSLContext db,
    ApidsService apidsSvc,
    EnvironmentProps envProps,
    RaidService raidSvc
  ) {
    this.db = db;
    this.apidsSvc = apidsSvc;
    this.envProps = envProps;
    this.raidSvc = raidSvc;
  }

  @Override
  public List<RaidListItemV1> listRaid(RaidListRequest req) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());

    return db.select(RAID.HANDLE, RAID.NAME, RAID.START_DATE, 
        RAID.CONFIDENTIAL, RAID.DATE_CREATED.as("createDate")).
      from(RAID).
      where(
        RAID.SERVICE_POINT_ID.eq(req.getServicePointId()).
          and(createSearchCondition(req))
      ).
      orderBy(RAID.DATE_CREATED.desc()).
      limit(MAX_EXPERIMENTAL_RECORDS).
      fetchInto(RaidListItemV1.class);
  }

  private static Condition createSearchCondition(RaidListRequest req) {
    Condition searchCondition = DSL.trueCondition();
    if( hasValue(req.getName()) ){
      String searchName = req.getName().trim();
      /* client side should prevent this, don't need to worry about 
       user-friendly error. */
      if( searchName.length() < 5 ){
        var iae = iae("searchName is too short to list raids");
        log.with("searchName", searchName).error(iae.getMessage());
        throw iae;
      }
      searchName = "%"+searchName+"%";
      searchCondition = DSL.condition(RAID.NAME.like(searchName));
    }
    return searchCondition;
  }

  public String formatLandingPageUrl(String handle){
    return "%s/%s".formatted(envProps.raidLandingPrefix, handle);
  }
  
  @Override
  public RaidListItemV1 mintRaidV1(MintRaidRequestV1 req) {
    Guard.notNull("confidential flag must be set", req.getConfidential());
    Guard.hasValue("name field must be set", req.getName());
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());

    JSONB jsonbMetadata = JSONB.valueOf(
      req.getMetadata() == null ? "{}" : req.getMetadata().toString()
    );

    
    var response = apidsSvc.mintApidsHandleContentPrefix(
      this::formatLandingPageUrl );

    db.insertInto(RAID).
      set(RAID.HANDLE, response.identifier.handle).
      set(RAID.SERVICE_POINT_ID, req.getServicePointId()).
      set(RAID.CONTENT_PATH, response.identifier.property.value).
      set(RAID.CONTENT_INDEX, response.identifier.property.index).
      set(RAID.NAME, req.getName()).
      set(RAID.DESCRIPTION, "").
      set(RAID.METADATA, jsonbMetadata).
      set(RAID.START_DATE, req.getStartDate() == null ? 
        LocalDateTime.now() : offset2Local(req.getStartDate()) ).
      set(RAID.DATE_CREATED, LocalDateTime.now()).
      set(RAID.CONFIDENTIAL, req.getConfidential()).
      execute();

    return db.fetchSingle(RAID, RAID.HANDLE.eq(response.identifier.handle)).
      into(RaidListItemV1.class); 
  }

  
  /**
   export RAID_API_TOKEN=xxx.yyy.zzz
   curl -s -X POST https://demo.raido-infra.com/v2/experimental/read-raid/v1 \
     -H 'Content-Type: application/json' \
     -H "Authorization: Bearer $RAID_API_TOKEN" \
     -d '{"handle":"123.456/789"}'   
   */
  @Override
  public ReadRaidResponseV1 readRaidV1(ReadRaidV1Request req) {
    Guard.hasValue("must pass a handle", req.getHandle());
    var user = getAuthzPayload();
    var data = raidSvc.readRaidData(req.getHandle());
    
    guardOperatorOrAssociated(user, data.servicePoint().getId());

    return new ReadRaidResponseV1().
      handle(data.raid().getHandle()).
      servicePointId(data.servicePoint().getId()).
      servicePointName(data.servicePoint().getName()).
      name(data.raid().getName()).
      startDate(local2Offset(data.raid().getStartDate())).
      createDate(local2Offset(data.raid().getDateCreated())).
      url(data.raid().getContentPath()).
      metadataEnvelopeSchema("unknown").
      metadata(data.raid().getMetadata().data()).
      confidential(data.raid().getConfidential());
  }

  @Override
  public ReadRaidResponseV1 updateRaidV1(MintRaidRequestV1 req) {
    Guard.hasValue("must pass a name", req.getName());
    Guard.hasValue("must pass a handle", req.getHandle());
    Guard.notNull("confidential flag must be set", req.getConfidential());
    
    db.update(RAID).
      set(RAID.NAME, req.getName()).
      set(RAID.START_DATE, req.getStartDate() == null ?
        LocalDateTime.now() : offset2Local(req.getStartDate()) ).
      set(RAID.CONFIDENTIAL, req.getConfidential()).
      where( RAID.HANDLE.eq(req.getHandle())).
      execute();
    
    return readRaidV1(new ReadRaidV1Request().handle(req.getHandle()));
  }
}
