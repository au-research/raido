package raido.apisvc.endpoint.raidv2;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.ValidationFailureException;
import raido.apisvc.service.raid.validation.RaidoSchemaV1ValidationService;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.model.MintRaidoSchemaV1Request;
import raido.idl.raidv2.model.MintResponse;
import raido.idl.raidv2.model.RaidListItemV2;
import raido.idl.raidv2.model.RaidListRequestV2;
import raido.idl.raidv2.model.ReadRaidResponseV2;
import raido.idl.raidv2.model.ReadRaidV2Request;
import raido.idl.raidv2.model.UpdateRaidoSchemaV1Request;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.transaction.annotation.Propagation.NEVER;
import static raido.apisvc.endpoint.Constant.MAX_EXPERIMENTAL_RECORDS;
import static raido.apisvc.endpoint.message.ValidationMessage.CANNOT_UPDATE_LEGACY_SCHEMA;
import static raido.apisvc.endpoint.message.ValidationMessage.IDENTIFIER_NOT_SET;
import static raido.apisvc.endpoint.message.ValidationMessage.ID_BLOCK_NOT_SET;
import static raido.apisvc.endpoint.message.ValidationMessage.METADATA_NOT_SET;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static raido.apisvc.service.raid.MetadataService.mapDb2Api;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.mintFailed;
import static raido.apisvc.util.DateUtil.local2Offset;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;
import static raido.apisvc.util.StringUtil.isBlank;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;

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
  public ReadRaidResponseV2 readRaidV2(ReadRaidV2Request req) {
    Guard.hasValue("must pass a handle", req.getHandle());
    var user = getAuthzPayload();
    var data = raidSvc.readRaidResponseV2(req.getHandle());
    guardOperatorOrAssociated(user, data.getServicePointId());
    return data;
  }

  /* Performance notes:
  - client sends json
  - Spring parses it out into all these different objects 
  - we validate it then immediately and turn the metadata (which is most of 
    the payload) back into json
  - save it to the DB
  - then turn the whole thing back into json, but now with a handle!
  And we're not even doing any validation of ORCID/ROR, etc. yet.
  That is a *lot* of heap garbage. Large raids are gonna wreck our memory usage.
  Especially if every update to single field is done by re-sending the full
  raid. 
  Also, sending the full 200K raid data, just to update the startDate - the 
  bandwidth bills will be monstrous.  BUT only on the way in - if we 
  don't return the full raid from mint/update - that won't cost us.  But we'll
  need more read calls - which will cost. I'm thinking stable API should not
  return the raid, just the handle (or maybe IdBlock).  
  A lot of API clients may just be dumping
  raids into the system (i.e. RDM) they don't care to display anything.
  */
  @Override
  // See the RaidSvc.mint() method for an explanation of Transaction stuff
  @Transactional(propagation = NEVER)
  public MintResponse mintRaidoSchemaV1(
    MintRaidoSchemaV1Request req
  ) {
    var mint = req.getMintRequest();

    /* when I did a "curl" with just an "empty object ", i.e. 
    `{}`, we got an NPE at mint.getServicePointId() */
    Guard.notNull("request param may not be null", req);
    Guard.notNull("mintRequest field may not be null", mint);
    
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, mint.getServicePointId());

    var failures = validSvc.validateRaidoSchemaV1(req.getMetadata());
    
    if( !failures.isEmpty() ){
      return mintFailed(failures);
    }

    String handle;
    try {
      handle = raidSvc.mintRaidoSchemaV1(
        req.getMintRequest().getServicePointId(),
        req.getMetadata() );
    }
    catch( ValidationFailureException e ){
      return mintFailed(failures);
    }

    return new MintResponse().success(true). 
      raid( readRaidV2(new ReadRaidV2Request().handle(handle)) );
  }

  @Override
  public List<RaidListItemV2> listRaidV2(RaidListRequestV2 req) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());

    return db.select(RAID.HANDLE, RAID.PRIMARY_TITLE, RAID.START_DATE,
        RAID.CONFIDENTIAL, RAID.METADATA_SCHEMA, RAID.DATE_CREATED).
      from(RAID).
      where(
        RAID.SERVICE_POINT_ID.eq(req.getServicePointId()).
          and(createV2SearchCondition(req))
      ).
      orderBy(RAID.DATE_CREATED.desc()).
      limit(MAX_EXPERIMENTAL_RECORDS).
      fetch(r-> new RaidListItemV2().
        handle(r.get(RAID.HANDLE)).
        primaryTitle(r.get(RAID.PRIMARY_TITLE)).
        startDate(r.get(RAID.START_DATE)).
        createDate(local2Offset(r.get(RAID.DATE_CREATED))).
        metadataSchema(mapDb2Api(r.get(RAID.METADATA_SCHEMA))) );
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
      searchCondition = DSL.condition(RAID.PRIMARY_TITLE.like(primaryTitle));
    }
    return searchCondition;
  }

  @Override
  public MintResponse updateRaidoSchemaV1(UpdateRaidoSchemaV1Request req) {
    var user = getAuthzPayload();
    var newData = req.getMetadata(); 
    if( newData == null ){
      return mintFailed(METADATA_NOT_SET);
    }
    
    var id = newData.getId();
    if( id == null ){
      return mintFailed(ID_BLOCK_NOT_SET);
    }

    if( isBlank(id.getIdentifier()) ){
      return mintFailed(IDENTIFIER_NOT_SET);
    }
    
    if( req.getMetadata().getMetadataSchema() == LEGACYMETADATASCHEMAV1 ){
      return mintFailed(CANNOT_UPDATE_LEGACY_SCHEMA);
    }

    // improve: don't need the svcPoint, wasteful to read it here
    var oldRaid = raidSvc.readRaidV2Data(id.getIdentifier()).raid();
    guardOperatorOrAssociated(user, oldRaid.getServicePointId());

    var failures = raidSvc.updateRaidoSchemaV1(req.getMetadata(), oldRaid);
    if( failures.isEmpty() ){
      return new MintResponse().success(true).raid( 
        readRaidV2(new ReadRaidV2Request().handle(id.getIdentifier())) );
    }
    else {
      return mintFailed(failures);
    }
  }

  @Override
  public MintResponse upgradeLegacyToRaidoSchema(
    UpdateRaidoSchemaV1Request req
  ) {
    var user = getAuthzPayload();
    var newData = req.getMetadata();
    if( newData == null ){
      return mintFailed(METADATA_NOT_SET);
    }

    var id = newData.getId();
    if( id == null ){
      return mintFailed(ID_BLOCK_NOT_SET);
    }

    if( isBlank(id.getIdentifier()) ){
      return mintFailed(IDENTIFIER_NOT_SET);
    }

    var oldRaid = raidSvc.readRaidV2Data(id.getIdentifier()).raid();
    guardOperatorOrAssociated(user, oldRaid.getServicePointId());
    
    /* re-using updateRaidoSchemaV1() allows the upgrade process to make any 
    change, not just add the necessary data to make it compatible with the 
    upgrade schema.  
    But as long as the new structure obeys the schema rules, who cares? */
    var failures = raidSvc.upgradeRaidoSchemaV1(req.getMetadata(), oldRaid);
    if( failures.isEmpty() ){
      return new MintResponse().success(true).raid(
        readRaidV2(new ReadRaidV2Request().handle(id.getIdentifier())) );
    }
    else {
      return mintFailed(failures);
    }
  }


}
