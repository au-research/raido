package raido.apisvc.service.raid;

import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.RaidV2Record;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.IdBlock;
import raido.idl.raidv2.model.MintRaidRequestV1;
import raido.idl.raidv2.model.MintRaidoSchemaV1Request;
import raido.idl.raidv2.model.RaidoMetadataSchemaV1;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static raido.apisvc.util.DateUtil.offset2Local;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.RaidV2.RAID_V2;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Component
public class RaidService {
  
  private DSLContext db;
  private ApidsService apidsSvc;
  private MetadataService metaSvc;
  private EnvironmentProps envProps;

  public RaidService(
    DSLContext db,
    ApidsService apidsSvc,
    MetadataService metaSvc,
    EnvironmentProps envProps
  ) {
    this.db = db;
    this.apidsSvc = apidsSvc;
    this.metaSvc = metaSvc;
    this.envProps = envProps;
  }


  public ApidsMintResponse mintSchemalessRaid(MintRaidRequestV1 req) {
    JSONB jsonbMetadata = JSONB.valueOf(
      req.getMetadata() == null ? "{}" : req.getMetadata().toString()
    );

    var response = apidsSvc.mintApidsHandleContentPrefix(
      this::formatRaidoLandingPageUrl);

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


  public String mintRaidoV2(MintRaidoSchemaV1Request req) {
    RaidoMetadataSchemaV1 metadata = req.getMetadata();
    metaSvc.validateRaidoSchemaV1(req.getMetadataSchema(), metadata);
    String primaryTitle = metaSvc.validateHasPrimaryTitle(metadata.getTitles());
    LocalDate startDate = metadata.getDates().getStartDate();
    boolean confidential = metadata.getAccess().getType() != AccessType.OPEN;

    var response = apidsSvc.mintApidsHandleContentPrefix(
      this::formatRaidoLandingPageUrl);
    String handle = response.identifier.handle;
    String raidUrl = response.identifier.property.value;

    metadata.setId(new IdBlock().
      identifier(handle).
      identifierTypeUri("https://raid.org").
      globalUrl(formatGlobalUrl(handle)).
      raidoUrl(raidUrl) );

    JSONB jsonbMetadata = JSONB.valueOf( metaSvc.mapToJson(metadata) );

    db.insertInto(RAID_V2).
      set(RAID_V2.HANDLE, handle).
      set(RAID_V2.SERVICE_POINT_ID, req.getMintRequest().getServicePointId()).
      set(RAID_V2.URL, raidUrl).
      set(RAID_V2.URL_INDEX, response.identifier.property.index).
      set(RAID_V2.PRIMARY_TITLE, primaryTitle).
      set(RAID_V2.METADATA, jsonbMetadata).
      set(RAID_V2.METADATA_SCHEMA, req.getMetadataSchema()).
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

  public String formatRaidoLandingPageUrl(String handle){
    return "%s/%s".formatted(envProps.raidoLandingPrefix, handle);
  }

  public String formatGlobalUrl(String handle){
    return "%s/%s".formatted(envProps.globalUrlPrefix, handle);
  }


}
