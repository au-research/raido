package raido.apisvc.service.raid;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.exception.InvalidVersionException;
import raido.apisvc.exception.UnknownServicePointException;
import raido.apisvc.factory.RaidRecordFactory;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.repository.ServicePointRepository;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.service.raid.id.IdentifierHandle;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.service.raid.id.IdentifierParser.ParseProblems;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.apisvc.service.raid.validation.RaidoSchemaV1ValidationService;
import raido.apisvc.spring.security.raidv2.ApiToken;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.service.raid.MetadataService.mapApi2Db;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.getPrimaryTitle;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.getPrimaryTitles;
import static raido.apisvc.util.DateUtil.local2Offset;
import static raido.apisvc.util.DateUtil.offset2Local;
import static raido.apisvc.util.ExceptionUtil.runtimeException;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.enums.Metaschema.*;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;

/* Be careful with usage of @Transactional, see db-transaction-guideline.md */
@Component
public class RaidService {
  private static final Log log = to(RaidService.class);
  
  private final DSLContext db;
  private final ApidsService apidsSvc;
  private final MetadataService metaSvc;
  private final RaidoSchemaV1ValidationService validSvc;
  private final TransactionTemplate tx;

  private final ServicePointRepository servicePointRepository;
  private final IdentifierParser idParser;

  public RaidService(
    final DSLContext db,
    final ApidsService apidsSvc,
    final MetadataService metaSvc,
    final RaidoSchemaV1ValidationService validSvc,
    final TransactionTemplate tx,
    final RaidRepository raidRepository,
    final ServicePointRepository servicePointRepository,
    final RaidRecordFactory raidRecordFactory,
    IdentifierParser idParser
  ) {
    this.db = db;
    this.apidsSvc = apidsSvc;
    this.metaSvc = metaSvc;
    this.validSvc = validSvc;
    this.tx = tx;
    this.servicePointRepository = servicePointRepository;
    this.idParser = idParser;
  }

  record DenormalisedRaidData(
    String primaryTitle,
    LocalDate startDate,
    boolean confidential
  ) { }

  /** Expects the passed metadata is valid. */
  public DenormalisedRaidData getDenormalisedRaidData(
          RaidoMetadataSchemaV2 metadata
  ){
    return new DenormalisedRaidData(
            getPrimaryTitle(metadata.getTitles()).getTitle(),
            metadata.getDates().getStartDate(),
            metadata.getAccess().getType() != AccessType.OPEN
    );
  }

  /** Expects the passed metadata is valid. */
  public DenormalisedRaidData getDenormalisedRaidData(
    RaidoMetadataSchemaV1 metadata
  ){
    return new DenormalisedRaidData(
      getPrimaryTitle(metadata.getTitles()).getTitle(),
      metadata.getDates().getStartDate(),
      metadata.getAccess().getType() != AccessType.OPEN
    );
  }

  public DenormalisedRaidData getDenormalisedRaidData(
    LegacyMetadataSchemaV1 metadata
  ){
    return new DenormalisedRaidData(
      getPrimaryTitle(metadata.getTitles()).getTitle(),
      metadata.getDates().getStartDate(),
      metadata.getAccess().getType() != AccessType.OPEN
    );
  }

  public IdentifierUrl mintRaidoSchemaV1(
    long servicePointId,
    RaidoMetadataSchemaV1 metadata
  ) throws ValidationFailureException {
    /* this is the part where we want to make sure no TX is held open.
    * Maybe *this* call should be marked tx.prop=never? */
    var apidsResponse = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);
    IdentifierHandle handle = parseHandleFromApids(apidsResponse);

    String raidUrl = apidsResponse.identifier.property.value;

    final var servicePointRecord =
      servicePointRepository.findById(servicePointId)
        .orElseThrow(() -> new UnknownServicePointException(servicePointId));

    var id = new IdentifierUrl(metaSvc.getMetaProps().handleUrlPrefix, handle);
    metadata.setId(metaSvc.createIdBlock(id, servicePointRecord));

    // validation failure possible
    String metadataAsJson = metaSvc.mapToJson(metadata);
    var raidData = getDenormalisedRaidData(metadata);

    tx.executeWithoutResult((status)-> db.insertInto(RAID).
      set(RAID.HANDLE, handle.format()).
      set(RAID.SERVICE_POINT_ID, servicePointId).
      set(RAID.URL, raidUrl).
      set(RAID.URL_INDEX, apidsResponse.identifier.property.index).
      set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
      set(RAID.METADATA, JSONB.valueOf(metadataAsJson)).
      set(RAID.METADATA_SCHEMA, mapApi2Db(metadata.getMetadataSchema())).
      set(RAID.START_DATE, raidData.startDate()).
      set(RAID.DATE_CREATED, LocalDateTime.now()).
      set(RAID.CONFIDENTIAL, raidData.confidential()).
      execute());
    
    return id;
  }
  
  private IdentifierHandle parseHandleFromApids(
    ApidsMintResponse apidsResponse
  ) {
    var parseResult = idParser.parseHandle(apidsResponse.identifier.handle);
    
    if( parseResult instanceof ParseProblems problems ){
      log.with("handle", apidsResponse.identifier.handle).
        with("problems", problems.getProblems()).
        error("APIDS service returned malformed handle");
      throw runtimeException("APIDS service returned malformed handle: %s",
        apidsResponse.identifier.handle);
    }
   
    return (IdentifierHandle) parseResult; 
  }

  public IdentifierUrl mintLegacySchemaV1(
    long servicePointId,
    LegacyMetadataSchemaV1 metadata
  ) throws ValidationFailureException {
    /* this is the part where we want to make sure no TX is help open.
     * Maybe *this* should be marked tx.prop=never? */
    var apidsResponse = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);
    IdentifierHandle handle = parseHandleFromApids(apidsResponse);
    String raidUrl = apidsResponse.identifier.property.value;

    final var servicePointRecord =
      servicePointRepository.findById(servicePointId)
        .orElseThrow(() -> new UnknownServicePointException(servicePointId));

    var id = new IdentifierUrl(metaSvc.getMetaProps().handleUrlPrefix, handle);
    metadata.setId(metaSvc.createIdBlock(id, servicePointRecord));

    // validation failure possible
    String metadataAsJson = metaSvc.mapToJson(metadata);
    var raidData = getDenormalisedRaidData(metadata);

    tx.executeWithoutResult((status)-> db.insertInto(RAID).
      set(RAID.HANDLE, handle.format()).
      set(RAID.SERVICE_POINT_ID, servicePointId).
      set(RAID.URL, raidUrl).
      set(RAID.URL_INDEX, apidsResponse.identifier.property.index).
      set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
      set(RAID.METADATA, JSONB.valueOf(metadataAsJson)).
      set(RAID.METADATA_SCHEMA, mapApi2Db(metadata.getMetadataSchema())).
      set(RAID.START_DATE, raidData.startDate()).
      set(RAID.DATE_CREATED, LocalDateTime.now()).
      set(RAID.CONFIDENTIAL, raidData.confidential()).
      execute());

    return id;
  }
  
  public record ReadRaidV2Data(
    RaidRecord raid,
    ServicePointRecord servicePoint
  ){}

  public ReadRaidV2Data readRaidV2Data(String handle){
    return db.select(RAID.fields()).
      select(SERVICE_POINT.fields()).
      from(RAID).join(SERVICE_POINT).onKey().
      where(RAID.HANDLE.eq(handle)).
      fetchSingle(r -> new ReadRaidV2Data(
        r.into(RaidRecord.class), 
        r.into(ServicePointRecord.class)) );
  }
  
  public ReadRaidResponseV2 readRaidResponseV2(String handle){

    ReadRaidV2Data data;
    try {
      data = readRaidV2Data(handle);
    }
    catch( NoDataFoundException e ){
      /* want to easily see what handles are failing, without having to 
      turn on param logging for all endpoints. When we implement selective 
      enablement of param logging at filter level, can get rid of this. */
      log.with("handle", handle).warn(e.getMessage());
      throw new RuntimeException(e);
    }
    return new ReadRaidResponseV2().
      handle(data.raid().getHandle()).
      servicePointId(data.servicePoint().getId()).
      servicePointName(data.servicePoint().getName()).
      primaryTitle(data.raid().getPrimaryTitle()).
      startDate(data.raid().getStartDate()).
      createDate(local2Offset(data.raid().getDateCreated())).
      url(data.raid().getUrl()).
      metadata(data.raid().getMetadata().data());
  }

  public void migrateRaidoSchemaV1(
    long servicePointId,
    int urlContentIndex,
    OffsetDateTime createDate, 
    LegacyMetadataSchemaV1 metadata
  ) throws ValidationFailureException {
    String primaryTitle = getPrimaryTitles(metadata.getTitles()).
      get(0).getTitle();
    LocalDate startDate = metadata.getDates().getStartDate();
    boolean confidential = metadata.getAccess().getType() != AccessType.OPEN;

    // migrated raids already have handles
    String identifier = metadata.getId().getIdentifier();
    IdentifierUrl id = idParser.parseUrlWithException(identifier);
    String handle = id.handle().format();
    
    final var servicePointRecord =
      servicePointRepository.findById(servicePointId)
        .orElseThrow(() -> new UnknownServicePointException(servicePointId));

    metadata.setId(metaSvc.createIdBlock(id, servicePointRecord));

    // validation failure possible
    String metadataAsJson = metaSvc.mapToJson(metadata);

    JSONB jsonbMetadata = JSONB.valueOf(metadataAsJson);

    /* planning to implement a "migrate legacy to raid v1" endpoint, what
    happens if someone "re-migrates" a raid that has already been upgraded?
    Without testing, pretty sure it will just stomp/reset - losing any data
    that was added during upgrade or added after upgrade. 
    This is where a raid metadata version would implement optimistic locking*/
    
    db.insertInto(RAID).
      set(RAID.HANDLE, handle).
      set(RAID.SERVICE_POINT_ID, servicePointId).
      set(RAID.URL, id.formatUrl()).
      set(RAID.URL_INDEX, urlContentIndex).
      set(RAID.PRIMARY_TITLE, primaryTitle).
      set(RAID.METADATA, jsonbMetadata).
      set(RAID.METADATA_SCHEMA, mapApi2Db(metadata.getMetadataSchema())).
      set(RAID.START_DATE, startDate).
      set(RAID.DATE_CREATED, offset2Local(createDate)).
      set(RAID.CONFIDENTIAL, confidential).
      onConflict(RAID.HANDLE).doUpdate().
        set(stream(RAID.fields()).collect(toMap(f -> f, DSL::excluded))).
        where(RAID.HANDLE.eq(handle)).
      execute();
  }

  @Transactional
  public List<ValidationFailure> updateRaidoSchemaV2(
          RaidoMetadataSchemaV2 newData,
          RaidRecord oldRaid
  ) {
    var oldData = metaSvc.mapV2SchemaMetadata(oldRaid);

    List<ValidationFailure> failures = new ArrayList<>();

    failures.addAll(
            validSvc.validateIdBlockNotChanged(newData.getId(), oldData.getId()) );
    failures.addAll(validSvc.validateRaidoSchemaV2(newData));


    var version = newData.getId().getVersion();
    newData.getId().version(version + 1);

    // validation failure possible (conversion error or maxSize of json)
    String metadataAsJson = null;
    try {
      metadataAsJson = metaSvc.mapToJson(newData);
    }
    catch( ValidationFailureException e ){
      failures.addAll(e.getFailures());
    }

    if( !failures.isEmpty() ){
      return failures;
    }

    var raidData = getDenormalisedRaidData(newData);

    final var metadata = metadataAsJson;

    var rowsUpdated =  tx.execute(status -> db.update(RAID).
            set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
            set(RAID.METADATA, JSONB.valueOf(metadata)).
            set(RAID.START_DATE, raidData.startDate()).
            set(RAID.CONFIDENTIAL, raidData.confidential()).
            set(RAID.METADATA_SCHEMA, raido_metadata_schema_v2).
            set(RAID.VERSION, version + 1).
            where(RAID.HANDLE.eq(oldRaid.getHandle())).
            and(RAID.VERSION.eq(version)).
            execute());

    if (rowsUpdated != 1) {
      throw new InvalidVersionException(version);
    }

    return emptyList();
  }


  /* improve: after it's working, try to factor this so that validation can be
   separated out and called from the endpoint and this is just "do work".
   Might not be possible though, think validation is too intertwined with the
   work that this method actually does. */
  public List<ValidationFailure> updateRaidoSchemaV1(
    RaidoMetadataSchemaV1 newData, 
    RaidRecord oldRaid
  ){
    Metaschema newMetadataSchema = mapApi2Db(newData.getMetadataSchema());
    if( newMetadataSchema != oldRaid.getMetadataSchema() ){
      return singletonList(SCHEMA_CHANGED);
    }
    
    var oldData = metaSvc.mapV1SchemaMetadata(oldRaid);

    List<ValidationFailure> failures = new ArrayList<>();
    
    failures.addAll(
      validSvc.validateIdBlockNotChanged(newData.getId(), oldData.getId()) );
    failures.addAll(validSvc.validateRaidoSchemaV1(newData));

    // validation failure possible (conversion error or maxSize of json)
    String metadataAsJson = null;
    try {
      metadataAsJson = metaSvc.mapToJson(newData);
    }
    catch( ValidationFailureException e ){
      failures.addAll(e.getFailures());
    }
    JSONB json = JSONB.valueOf(metadataAsJson);

    if( !failures.isEmpty() ){
      return failures;
    }

    var raidData = getDenormalisedRaidData(newData);
    tx.executeWithoutResult((status)->{
      db.update(RAID).
        set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
        set(RAID.METADATA, json).
        set(RAID.START_DATE, raidData.startDate()).
        set(RAID.CONFIDENTIAL, raidData.confidential()).
        where(RAID.HANDLE.eq(oldRaid.getHandle())).
        execute();
    });
    
    return emptyList();
  }

  public List<ValidationFailure> upgradeRaidoSchemaV1(
    RaidoMetadataSchemaV1 newData,
    RaidRecord oldRaid
  ) {

    if( newData.getMetadataSchema() != RAIDOMETADATASCHEMAV1 ){
      return List.of(CANNOT_UPGRADE_TO_OTHER_SCHEMA);
    }
    if( oldRaid.getMetadataSchema() != legacy_metadata_schema_v1 ){
      return List.of(UPGRADE_LEGACY_SCHEMA_ONLY);
    }
    
    var legacyData = metaSvc.mapLegacyMetadata(oldRaid);

    List<ValidationFailure> failures = new ArrayList<>();
    failures.addAll(
      validSvc.validateIdBlockNotChanged(newData.getId(), legacyData.getId()) );
    failures.addAll(validSvc.validateRaidoSchemaV1(newData));

    // validation failure possible (conversion error or maxSize of json)
    String metadataAsJson = null;
    try {
      metadataAsJson = metaSvc.mapToJson(newData);
    }
    catch( ValidationFailureException e ){
      failures.addAll(e.getFailures());
    }

    if( !failures.isEmpty() ){
      return failures;
    }

    JSONB json = JSONB.valueOf(metadataAsJson);
    var raidData = getDenormalisedRaidData(newData);

    tx.executeWithoutResult((status)->{
      db.update(RAID).
        set(RAID.METADATA_SCHEMA, raido_metadata_schema_v1).
        set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
        set(RAID.METADATA, json).
        set(RAID.START_DATE, raidData.startDate()).
        set(RAID.CONFIDENTIAL, raidData.confidential()).
        where(RAID.HANDLE.eq(oldRaid.getHandle())).
        execute();
    });

    return emptyList();
  }

  public ServicePointRecord findServicePoint(String name){
    return db.select().from(SERVICE_POINT).
      where(SERVICE_POINT.NAME.eq(name)).
      fetchSingleInto(SERVICE_POINT);
  }

  public boolean isEditable(final ApiToken user, final long servicePointId) {
    final var servicePoint = servicePointRepository.findById(servicePointId)
      .orElseThrow(() -> new UnknownServicePointException(servicePointId));

    return user.getClientId().equals("RAIDO_API") || servicePoint.getAppWritesEnabled();
  }
}