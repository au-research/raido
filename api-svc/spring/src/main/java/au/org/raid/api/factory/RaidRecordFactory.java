package au.org.raid.api.factory;

import au.org.raid.api.exception.InvalidJsonException;
import au.org.raid.api.exception.InvalidTitleException;
import au.org.raid.api.service.apids.model.ApidsMintResponse;
import au.org.raid.api.util.DateUtil;
import au.org.raid.db.jooq.api_svc.enums.Metaschema;
import au.org.raid.db.jooq.api_svc.tables.records.RaidRecord;
import au.org.raid.db.jooq.api_svc.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;

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
            final RaidCreateRequest raid,
            final ApidsMintResponse apidsMintResponse,
            final ServicePointRecord servicePointRecord) {

        final var primaryTitle = raid.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .orElseThrow(() -> new InvalidTitleException("One title with a titleType of 'Primary' should be specified."))
                .getText();

        final String raidJson;
        try {
            raidJson = objectMapper.writeValueAsString(raid);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException();
        }

        return new RaidRecord()
                .setVersion(raid.getIdentifier().getVersion())
                .setHandle(apidsMintResponse.identifier.handle)
                .setServicePointId(servicePointRecord.getId())
                .setUrl(apidsMintResponse.identifier.property.value)
                .setUrlIndex(apidsMintResponse.identifier.property.index)
                .setPrimaryTitle(primaryTitle)
                .setMetadata(JSONB.valueOf(raidJson))
                .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
                .setStartDate(DateUtil.parseDate(raid.getDate().getStartDate()))
                .setDateCreated(LocalDateTime.now())
                .setConfidential(raid.getAccess().getType().equals(ACCESS_TYPE_CLOSED));
    }

    public RaidRecord merge(final RaidUpdateRequest raid, final RaidRecord existing) {

        final var primaryTitle = raid.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .orElseThrow(() -> new InvalidTitleException("One title with a titleType of 'Primary' should be specified."))
                .getText();

        final var newVersion = raid.getIdentifier().getVersion() + 1;
        raid.getIdentifier().version(newVersion);

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
                .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
                .setStartDate(DateUtil.parseDate(raid.getDate().getStartDate()))
                .setConfidential(raid.getAccess().getType().equals(ACCESS_TYPE_CLOSED));
    }
}