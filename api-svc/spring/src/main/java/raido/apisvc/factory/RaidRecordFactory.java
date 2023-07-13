package raido.apisvc.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;
import raido.apisvc.exception.InvalidJsonException;
import raido.apisvc.exception.InvalidTitleException;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.*;

import java.time.LocalDateTime;

@Component
public class RaidRecordFactory {

  private static final String PRIMARY_TITLE_TYPE =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";
  private final ObjectMapper objectMapper;

  public RaidRecordFactory(final ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public RaidRecord create(
    final CreateRaidV1Request raid,
    final ApidsMintResponse apidsMintResponse,
    final ServicePointRecord servicePointRecord) {

    final var primaryTitle = raid.getTitles().stream()
      .filter(title -> title.getType().equals(PRIMARY_TITLE_TYPE))
      .findFirst()
      .orElseThrow(() -> new InvalidTitleException("One title with a titleType of 'Primary' should be specified."))
      .getTitle();

    final String raidJson;
    try {
      raidJson = objectMapper.writeValueAsString(raid);
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException();
    }

    return new RaidRecord()
      .setHandle(apidsMintResponse.identifier.handle)
      .setServicePointId(servicePointRecord.getId())
      .setUrl(apidsMintResponse.identifier.property.value)
      .setUrlIndex(apidsMintResponse.identifier.property.index)
      .setPrimaryTitle(primaryTitle)
      .setMetadata(JSONB.valueOf(raidJson))
      .setMetadataSchema(Metaschema.raido_metadata_schema_v1)
      .setStartDate(raid.getDates().getStartDate())
      .setDateCreated(LocalDateTime.now())
      .setConfidential(raid.getAccess().getType() == AccessType.CLOSED);
  }

  public RaidRecord merge(final UpdateRaidV1Request raid, final RaidRecord existing) {

    final var primaryTitle = raid.getTitles().stream()
      .filter(title -> title.getType().equals(PRIMARY_TITLE_TYPE))
      .findFirst()
      .orElseThrow(() -> new InvalidTitleException("One title with a titleType of 'Primary' should be specified."))
      .getTitle();

    final String raidJson;
    try {
      raidJson = objectMapper.writeValueAsString(raid);
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException();
    }

    return new RaidRecord()
      .setHandle(existing.getHandle())
      .setServicePointId(existing.getServicePointId())
      .setUrl(existing.getUrl())
      .setUrlIndex(existing.getUrlIndex())
      .setPrimaryTitle(primaryTitle)
      .setMetadata(JSONB.valueOf(raidJson))
      .setMetadataSchema(Metaschema.raido_metadata_schema_v1)
      .setStartDate(raid.getDates().getStartDate())
      .setConfidential(raid.getAccess().getType() == AccessType.CLOSED);
  }

  public RaidRecord merge(final UpdateRaidStableV2Request raid, final RaidRecord existing) {

    final var primaryTitle = raid.getTitles().stream()
      .filter(title -> title.getType() == TitleType.PRIMARY_TITLE)
      .findFirst()
      .orElseThrow(() -> new InvalidTitleException("One title with a titleType of 'Primary' should be specified."))
      .getTitle();

    raid.getId().setVersion(raid.getId().getVersion() + 1);

    final String raidJson;
    try {
      raidJson = objectMapper.writeValueAsString(raid);
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException();
    }

    return new RaidRecord()
      .setVersion(raid.getId().getVersion())
      .setHandle(existing.getHandle())
      .setServicePointId(existing.getServicePointId())
      .setUrl(existing.getUrl())
      .setUrlIndex(existing.getUrlIndex())
      .setPrimaryTitle(primaryTitle)
      .setMetadata(JSONB.valueOf(raidJson))
      .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
      .setStartDate(raid.getDates().getStartDate())
      .setConfidential(raid.getAccess().getType() == AccessType.CLOSED);
  }
}
