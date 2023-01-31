package raido.apisvc.service.raid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.exception.NoDataFoundException;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.raid.validation.RaidoSchemaV1ValidationService;
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
import static org.springframework.transaction.annotation.Propagation.NEVER;
import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.service.raid.MetadataService.mapApi2Db;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.getPrimaryTitle;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.getPrimaryTitles;
import static raido.apisvc.util.DateUtil.local2Offset;
import static raido.apisvc.util.DateUtil.offset2Local;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.enums.Metaschema.legacy_metadata_schema_v1;
import static raido.db.jooq.api_svc.enums.Metaschema.raido_metadata_schema_v1;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;

@Component
public class RaidService {
  private static final Log log = to(RaidService.class);
  
  private final DSLContext db;
  private final ApidsService apidsSvc;
  private final MetadataService metaSvc;
  private final RaidoSchemaV1ValidationService validSvc;
  private  final TransactionTemplate tx;
  private final RaidRepository raidRepository;

  public RaidService(
    DSLContext db,
    ApidsService apidsSvc,
    MetadataService metaSvc,
    RaidoSchemaV1ValidationService validSvc,
    TransactionTemplate tx,
    RaidRepository raidRepository) {
    this.db = db;
    this.apidsSvc = apidsSvc;
    this.metaSvc = metaSvc;
    this.validSvc = validSvc;
    this.tx = tx;
    this.raidRepository = raidRepository;
  }


  record DenormalisedRaidData(
    String primaryTitle,
    LocalDate startDate,
    boolean confidential
  ) { }

  public DenormalisedRaidData getDenormalisedRaidData(
    CreateMetadataSchemaV1 metadata
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

  /**
   Note propagation=NEVER causes this to fail if called when a TX is 
   currently active. The method does manual TX handling internally, 
   to avoid holding an open TX while talking to the APIDS service.
   */
  @Transactional(propagation = NEVER)
  public String mintRaidoSchemaV1(
    long servicePointId,
    RaidoMetadataSchemaV1 metadata
  ) throws ValidationFailureException {
    /* this is the part where we want to make sure no TX is help open.
    * Maybe *this* should be marked tx.prop=never? */
    var response = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);
    String handle = response.identifier.handle;
    String raidUrl = response.identifier.property.value;

    metadata.setId(metaSvc.createIdBlock(handle, raidUrl));

    // validation failure possible
    String metadataAsJson = metaSvc.mapToJson(metadata);
    var raidData = getDenormalisedRaidData(metadata);

    tx.executeWithoutResult((status)-> db.insertInto(RAID).
      set(RAID.HANDLE, handle).
      set(RAID.SERVICE_POINT_ID, servicePointId).
      set(RAID.URL, raidUrl).
      set(RAID.URL_INDEX, response.identifier.property.index).
      set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
      set(RAID.METADATA, JSONB.valueOf(metadataAsJson)).
      set(RAID.METADATA_SCHEMA, mapApi2Db(metadata.getMetadataSchema())).
      set(RAID.START_DATE, raidData.startDate()).
      set(RAID.DATE_CREATED, LocalDateTime.now()).
      set(RAID.CONFIDENTIAL, raidData.confidential()).
      execute());
    
    return handle;
  }

  @Transactional(propagation = NEVER)
  public String mintRaidSchemaV1(
    final CreateRaidSchemaV1 raidSchemaV1
  ) {
    /* this is the part where we want to make sure no TX is help open.
     * Maybe *this* should be marked tx.prop=never? */
    var response = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);
    String handle = response.identifier.handle;
    String raidUrl = response.identifier.property.value;

    raidSchemaV1.getMetadata().setId(metaSvc.createIdBlock(handle, raidUrl));

    // validation failure possible
    var raidData = getDenormalisedRaidData(raidSchemaV1.getMetadata());

    raidRepository.save(handle,
      raidSchemaV1.getMintRequest().getServicePointId(),
      raidUrl,
      response.identifier.property.index,
      raidData.primaryTitle(),
      raidSchemaV1.getMetadata(),
      mapApi2Db(raidSchemaV1.getMetadata().getMetadataSchema()),
      raidData.startDate(),
      raidData.confidential);

    return handle;
  }

  @Transactional(propagation = NEVER)
  public String mintLegacySchemaV1(
    long servicePointId,
    LegacyMetadataSchemaV1 metadata
  ) throws ValidationFailureException {
    /* this is the part where we want to make sure no TX is help open.
     * Maybe *this* should be marked tx.prop=never? */
    var response = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);
    String handle = response.identifier.handle;
    String raidUrl = response.identifier.property.value;

    metadata.setId(metaSvc.createIdBlock(handle, raidUrl));

    // validation failure possible
    String metadataAsJson = metaSvc.mapToJson(metadata);
    var raidData = getDenormalisedRaidData(metadata);

    tx.executeWithoutResult((status)-> db.insertInto(RAID).
      set(RAID.HANDLE, handle).
      set(RAID.SERVICE_POINT_ID, servicePointId).
      set(RAID.URL, raidUrl).
      set(RAID.URL_INDEX, response.identifier.property.index).
      set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
      set(RAID.METADATA, JSONB.valueOf(metadataAsJson)).
      set(RAID.METADATA_SCHEMA, mapApi2Db(metadata.getMetadataSchema())).
      set(RAID.START_DATE, raidData.startDate()).
      set(RAID.DATE_CREATED, LocalDateTime.now()).
      set(RAID.CONFIDENTIAL, raidData.confidential()).
      execute());

    return handle;
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

  public RaidSchemaV1 readRaidV1(String handle){
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
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    try {
      return new RaidSchemaV1().
        mintRequest(
          new MintRequestSchemaV1().servicePointId(data.servicePoint.getId())).
        metadata(
          objectMapper.readValue(data.raid().getMetadata().data(), MetadataSchemaV1.class)
        );
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
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
    String handle = metadata.getId().getIdentifier();
    String raidUrl = metaSvc.formatRaidoLandingPageUrl(handle);

    metadata.getId().setRaidAgencyUrl(raidUrl);
    metadata.getId().setRaidAgencyIdentifier(
      metaSvc.getMetaProps().raidAgencyIdentifier );
    
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
      set(RAID.URL, raidUrl).
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

  /* improve: after it's working, try to factor this so that validation can be
   separated out and called from the endpoint and this is just "do work".
   Might not be possible though, think validation is too intertwined with the
   work that this method actually does. */
  public List<ValidationFailure> updateRaidoSchemaV1(
    RaidoMetadataSchemaV1 newData,
    RaidRecord oldRaid
  ) {
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
    
    if( !failures.isEmpty() ){
      return failures;
    }

    var raidData = getDenormalisedRaidData(newData);

    db.update(RAID).
      set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
      set(RAID.METADATA, JSONB.valueOf(metadataAsJson)).
      set(RAID.START_DATE, raidData.startDate()).
      set(RAID.CONFIDENTIAL, raidData.confidential()).
      where(RAID.HANDLE.eq(oldRaid.getHandle())).
      execute();
    
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

    var raidData = getDenormalisedRaidData(newData);

    db.update(RAID).
      set(RAID.METADATA_SCHEMA, raido_metadata_schema_v1).
      set(RAID.PRIMARY_TITLE, raidData.primaryTitle()).
      set(RAID.METADATA, JSONB.valueOf(metadataAsJson)).
      set(RAID.START_DATE, raidData.startDate()).
      set(RAID.CONFIDENTIAL, raidData.confidential()).
      where(RAID.HANDLE.eq(oldRaid.getHandle())).
      execute();

    return emptyList();
  }
  
  public ServicePointRecord findServicePoint(String name){
    return db.select().from(SERVICE_POINT).
      where(SERVICE_POINT.NAME.eq(name)).
      fetchSingleInto(SERVICE_POINT);
  }
}
