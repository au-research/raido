package raido.apisvc.endpoint.raidv2;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.model.MintRaidRequestV1;
import raido.idl.raidv2.model.RaidListItemV1;
import raido.idl.raidv2.model.RaidListRequest;
import raido.idl.raidv2.model.ReadRaidV1Request;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.Constant.MAX_EXPERIMENTAL_RECORDS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
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
  
  public BasicRaidExperimental(
    DSLContext db,
    ApidsService apidsSvc
  ) {
    this.db = db;
    this.apidsSvc = apidsSvc;
  }

  @Override
  public List<RaidListItemV1> listRaid(RaidListRequest req) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());

    return db.select(RAID.HANDLE, RAID.NAME, RAID.START_DATE).
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

  @Override
  public RaidListItemV1 mintRaidV1(MintRaidRequestV1 req) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());

    JSONB jsonbMetadata = JSONB.valueOf(
      req.getMetadata() == null ? "{}" : req.getMetadata().toString()
    );

    var response = apidsSvc.mintApidsHandleContentPrefix(
      "https://demo.raido-infra.com/%s"::formatted);

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
      execute();

    return db.fetchSingle(RAID, RAID.HANDLE.eq(response.identifier.handle)).
      into(RaidListItemV1.class); 
  }

  @Override
  public MintRaidRequestV1 readRaidV1(ReadRaidV1Request req) {
    var user = getAuthzPayload();

    var raid = db.fetchSingle(RAID, RAID.HANDLE.eq(req.getHandle())).
      into(RaidRecord.class);

    guardOperatorOrAssociated(user, raid.getServicePointId());
    
    return new MintRaidRequestV1().
      handle(raid.getHandle()).
      name(raid.getName()).
      startDate(raid.getStartDate().atOffset(ZoneOffset.UTC)).
      metadataEnvelopeSchema("unknown").
      metadata(raid.getMetadata().data());
  }

  @Override
  public MintRaidRequestV1 updateRaidV1(MintRaidRequestV1 req) {
    db.update(RAID).
      set(RAID.NAME, req.getName()).
      set(RAID.START_DATE, req.getStartDate() == null ?
        LocalDateTime.now() : offset2Local(req.getStartDate()) ).
      where( RAID.HANDLE.eq(req.getHandle())).
      execute();
    
    return readRaidV1(new ReadRaidV1Request().handle(req.getHandle()));
  }
}
