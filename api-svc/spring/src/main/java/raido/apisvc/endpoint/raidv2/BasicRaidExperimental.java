package raido.apisvc.endpoint.raidv2;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.service.raid.ValidationFailureException;
import raido.apisvc.service.raid.validation.RaidoSchemaV1ValidationService;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.model.MintRaidRequestV1;
import raido.idl.raidv2.model.MintRaidoSchemaV1Request;
import raido.idl.raidv2.model.MintResponse;
import raido.idl.raidv2.model.RaidListItemV1;
import raido.idl.raidv2.model.RaidListItemV2;
import raido.idl.raidv2.model.RaidListRequest;
import raido.idl.raidv2.model.RaidListRequestV2;
import raido.idl.raidv2.model.ReadRaidResponseV1;
import raido.idl.raidv2.model.ReadRaidResponseV2;
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
import static raido.db.jooq.api_svc.tables.RaidV2.RAID_V2;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class BasicRaidExperimental implements BasicRaidExperimentalApi {
  private static final Log log = to(BasicRaidExperimental.class);

  private DSLContext db;
  private RaidService raidSvc;
  private RaidoSchemaV1ValidationService validSvc;

  public BasicRaidExperimental(
    DSLContext db,
    RaidService raidSvc,
    RaidoSchemaV1ValidationService validSvc
  ) {
    this.db = db;
    this.raidSvc = raidSvc;
    this.validSvc = validSvc;
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

  @Override
  public RaidListItemV1 mintRaidV1(MintRaidRequestV1 req) {
    Guard.notNull("confidential flag must be set", req.getConfidential());
    Guard.hasValue("name field must be set", req.getName());
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());

    ApidsMintResponse response = raidSvc.mintSchemalessRaid(req);

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

  @Override
  public ReadRaidResponseV2 readRaidV2(ReadRaidV1Request req) {
    Guard.hasValue("must pass a handle", req.getHandle());
    var user = getAuthzPayload();
    var data = raidSvc.readRaidResponseV2(req.getHandle());
    guardOperatorOrAssociated(user, data.getServicePointId());
    return data;
  }
  
  /* Performance:
  - client sends json
  - Spring parses it out into all these different objects 
  - we validate it then immediately and turn the metadata (which is most of 
    the payload) back into json
  - save it to the DB
  - then turn the whole thing back into json, but now with a handle!
  And we're not even doing any validation or orcids/rors, etc. yet.
  That is a *lot* of heap garbage. Large raids are gonna wreck our memory usage.
  Especially if every update to single field is done by re-sending the full
  raid. 
  Also, sending the full 200K raid data, just to update the startDate - the 
  bandwidth bills will be monstrous.  Mmmmm, but only on the way in - if we 
  don't return the full raid from mint/update - that won't cost us.  But we'll
  need more read calls - which will cost. I'm thinking stable API should not
  return the raid, just the handle (or maybe IdBlock).  
  A lot of API clients may just be dumping
  raids into the system (i.e. RDM) they don't care to display anything.
  */
  @Override
  public MintResponse mintRaidoSchemaV1(
    MintRaidoSchemaV1Request req
  ) {
    var mint = req.getMintRequest();
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, mint.getServicePointId());

    var failures = validSvc.validateRaidoSchemaV1(req.getMetadata());
    
    if( !failures.isEmpty() ){
      return new MintResponse().success(false).failures(failures);
    }

    String handle = null;
    try {
      handle = raidSvc.mintRaidoSchemaV1(
        req.getMintRequest().getServicePointId(),
        req.getMetadata() );
    }
    catch( ValidationFailureException e ){
      return new MintResponse().success(false).failures(e.getFailures());
    }

    return new MintResponse().success(true). 
      raid( readRaidV2(new ReadRaidV1Request().handle(handle)) );
  }

  @Override
  public List<RaidListItemV2> listRaidV2(RaidListRequestV2 req) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());

    return db.select(RAID_V2.HANDLE, RAID_V2.PRIMARY_TITLE, RAID_V2.START_DATE,
        RAID_V2.CONFIDENTIAL, RAID_V2.DATE_CREATED.as("createDate")).
      from(RAID_V2).
      where(
        RAID_V2.SERVICE_POINT_ID.eq(req.getServicePointId()).
          and(createV2SearchCondition(req))
      ).
      orderBy(RAID_V2.DATE_CREATED.desc()).
      limit(MAX_EXPERIMENTAL_RECORDS).
      fetchInto(RaidListItemV2.class);
  }

  private static Condition createV2SearchCondition(RaidListRequestV2 req) {
    Condition searchCondition = DSL.trueCondition();
    if( hasValue(req.getPrimaryTitle()) ){
      String primaryTitle = req.getPrimaryTitle().trim();
      /* client side should prevent this, don't need to worry about 
       user-friendly error. */
      if( primaryTitle.length() < 5 ){
        var iae = iae("primaryTitle is too short to list raids");
        log.with("primaryTitle", primaryTitle).error(iae.getMessage());
        throw iae;
      }
      primaryTitle = "%"+primaryTitle+"%";
      searchCondition = DSL.condition(RAID_V2.PRIMARY_TITLE.like(primaryTitle));
    }
    return searchCondition;
  }

}
