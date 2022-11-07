package raido.apisvc.service.raid;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.jooq.exception.NoDataFoundException;
import org.springframework.stereotype.Component;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.RaidV2Record;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.MetadataSchemaV1;
import raido.idl.raidv2.model.MintRaidRequestV1;
import raido.idl.raidv2.model.ReadRaidResponseV2;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static raido.apisvc.service.raid.MetadataService.mapJs2Jq;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.getPrimaryTitles;
import static raido.apisvc.util.DateUtil.local2Offset;
import static raido.apisvc.util.DateUtil.offset2Local;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.RaidV2.RAID_V2;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Component
public class RaidService {
  private static final Log log = to(RaidService.class);
  
  private DSLContext db;
  private ApidsService apidsSvc;
  private MetadataService metaSvc;

  public RaidService(
    DSLContext db,
    ApidsService apidsSvc,
    MetadataService metaSvc
  ) {
    this.db = db;
    this.apidsSvc = apidsSvc;
    this.metaSvc = metaSvc;
  }


  public ApidsMintResponse mintSchemalessRaid(MintRaidRequestV1 req) {
    JSONB jsonbMetadata = JSONB.valueOf(
      req.getMetadata() == null ? "{}" : req.getMetadata().toString()
    );

    var response = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);

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
    
    return response;
  }


  public String mintRaidoSchemaV1(
    long servicePointId, 
    MetadataSchemaV1 metadata
  ) throws ValidationFailureException {
    String primaryTitle = getPrimaryTitles(metadata.getTitles()).
      get(0).getTitle();
    LocalDate startDate = metadata.getDates().getStartDate();
    boolean confidential = metadata.getAccess().getType() != AccessType.OPEN;

    var response = apidsSvc.mintApidsHandleContentPrefix(
      metaSvc::formatRaidoLandingPageUrl);
    String handle = response.identifier.handle;
    String raidUrl = response.identifier.property.value;

    metadata.setId(metaSvc.createIdBlock(handle, raidUrl));

    // validation failure possible
    String metadataAsJson = metaSvc.mapToJson(metadata);

    JSONB jsonbMetadata = JSONB.valueOf(metadataAsJson);

    db.insertInto(RAID_V2).
      set(RAID_V2.HANDLE, handle).
      set(RAID_V2.SERVICE_POINT_ID, servicePointId).
      set(RAID_V2.URL, raidUrl).
      set(RAID_V2.URL_INDEX, response.identifier.property.index).
      set(RAID_V2.PRIMARY_TITLE, primaryTitle).
      set(RAID_V2.METADATA, jsonbMetadata).
      set(RAID_V2.METADATA_SCHEMA, mapJs2Jq(metadata.getMetadataSchema())).
      set(RAID_V2.START_DATE, startDate).
      set(RAID_V2.DATE_CREATED, LocalDateTime.now()).
      set(RAID_V2.CONFIDENTIAL, confidential).
      execute();
    return handle;
  }

  public record ReadRaidData(
    RaidRecord raid, 
    ServicePointRecord servicePoint
  ){}

  public record ReadRaidV2Data(
    RaidV2Record raid, 
    ServicePointRecord servicePoint
  ){}

  public ReadRaidData readRaidData(String handle){
    return db.select(RAID.fields()).
      select(SERVICE_POINT.fields()).
      from(RAID).join(SERVICE_POINT).onKey().
      where(RAID.HANDLE.eq(handle)).
      fetchSingle(r -> new ReadRaidData(
        r.into(RaidRecord.class), 
        r.into(ServicePointRecord.class)) );
  }
  
  public ReadRaidV2Data readRaidV2Data(String handle){
    return db.select(RAID_V2.fields()).
      select(SERVICE_POINT.fields()).
      from(RAID_V2).join(SERVICE_POINT).onKey().
      where(RAID_V2.HANDLE.eq(handle)).
      fetchSingle(r -> new ReadRaidV2Data(
        r.into(RaidV2Record.class), 
        r.into(ServicePointRecord.class)) );
  }
  
  public ReadRaidResponseV2 readRaidResponseV2(String handle){
    ReadRaidV2Data data = null;
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
    MetadataSchemaV1 metadata
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

    db.insertInto(RAID_V2).
      set(RAID_V2.HANDLE, handle).
      set(RAID_V2.SERVICE_POINT_ID, servicePointId).
      set(RAID_V2.URL, raidUrl).
      set(RAID_V2.URL_INDEX, urlContentIndex).
      set(RAID_V2.PRIMARY_TITLE, primaryTitle).
      set(RAID_V2.METADATA, jsonbMetadata).
      set(RAID_V2.METADATA_SCHEMA, mapJs2Jq(metadata.getMetadataSchema())).
      set(RAID_V2.START_DATE, startDate).
      set(RAID_V2.DATE_CREATED, LocalDateTime.now()).
      set(RAID_V2.CONFIDENTIAL, confidential).
      onConflict(RAID_V2.HANDLE).doUpdate().
        set(RAID_V2.SERVICE_POINT_ID, servicePointId).
        set(RAID_V2.URL, raidUrl).
        set(RAID_V2.URL_INDEX, urlContentIndex).
        set(RAID_V2.PRIMARY_TITLE, primaryTitle).
        set(RAID_V2.METADATA, jsonbMetadata).
        set(RAID_V2.METADATA_SCHEMA, mapJs2Jq(metadata.getMetadataSchema())).
        set(RAID_V2.START_DATE, startDate).
        set(RAID_V2.DATE_CREATED, LocalDateTime.now()).
        set(RAID_V2.CONFIDENTIAL, confidential).
        where(RAID_V2.HANDLE.eq(handle)).
      execute();
  }
  
}
