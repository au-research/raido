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
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.UpdateRaidV1Request;

import java.time.LocalDateTime;

//TODO: write a test for this
@Component
public class RaidRecordFactory {
  private static final String ACCESS_TYPE_CLOSED =
    "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json";

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
      .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
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
      .setVersion(raid.getId().getVersion())
      .setHandle(apidsMintResponse.identifier.handle)
      .setServicePointId(servicePointRecord.getId())
      .setUrl(apidsMintResponse.identifier.property.value)
      .setUrlIndex(apidsMintResponse.identifier.property.index)
      .setPrimaryTitle(primaryTitle)
      .setMetadata(JSONB.valueOf(raidJson))
      .setMetadataSchema(Metaschema.raido_metadata_schema_v1)
      .setStartDate(raid.getDates().getStartDate())
      .setDateCreated(LocalDateTime.now())
      .setConfidential(raid.getAccess().getType().equals(ACCESS_TYPE_CLOSED));
  }
  public RaidRecord merge(final UpdateRaidV1Request raid, final RaidRecord existing) {

    final var primaryTitle = raid.getTitles().stream()
      .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
      .findFirst()
      .orElseThrow(() -> new InvalidTitleException("One title with a titleType of 'Primary' should be specified."))
      .getTitle();

    final var newVersion = raid.getId().getVersion() + 1;
    raid.getId().version(newVersion);

    final String raidJson;
    try {
      raidJson = objectMapper.writeValueAsString(raid);
    } catch (JsonProcessingException e) {
      throw new InvalidJsonException();
    }

    return new RaidRecord()
      .setVersion(existing.getVersion())
      .setHandle(existing.getHandle())
      .setServicePointId(existing.getServicePointId())
      .setUrl(existing.getUrl())
      .setUrlIndex(existing.getUrlIndex())
      .setPrimaryTitle(primaryTitle)
      .setMetadata(JSONB.valueOf(raidJson))
      .setMetadataSchema(Metaschema.raido_metadata_schema_v1)
      .setStartDate(raid.getDates().getStartDate())
      .setConfidential(raid.getAccess().getType().equals(ACCESS_TYPE_CLOSED));
  }
}