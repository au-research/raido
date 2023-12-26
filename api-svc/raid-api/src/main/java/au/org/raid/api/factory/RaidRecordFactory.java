package au.org.raid.api.factory;

import au.org.raid.api.exception.InvalidJsonException;
import au.org.raid.api.exception.InvalidTitleException;
import au.org.raid.api.service.Handle;
import au.org.raid.api.service.apids.model.ApidsMintResponse;
import au.org.raid.api.util.DateUtil;
import au.org.raid.db.jooq.enums.Metaschema;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import au.org.raid.idl.raidv2.model.Title;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//TODO: write a test for this
@Component
public class RaidRecordFactory {
    private static final String ACCESS_TYPE_OPEN =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json";

    private static final String PRIMARY_TITLE_TYPE =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";
    private final ObjectMapper objectMapper;

    public RaidRecordFactory(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public RaidRecord create(final RaidDto raid,
                             final Integer accessTypeId,
                             final Integer accessStatementLanguageId,
                             final Integer registrationAgencyOrganisationId,
                             final Integer ownerOrganisationId
                             ) {


        final var handle = new Handle(raid.getIdentifier().getId());

        final var primaryTitle = raid.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .orElseThrow(() -> new InvalidTitleException(null));

        String accessStatement = null;
        if (raid.getAccess().getStatement() != null) {
            accessStatement = raid.getAccess().getStatement().getText();
        }

        return new RaidRecord()
                .setHandle(handle.toString())
                .setServicePointId(raid.getIdentifier().getOwner().getServicePoint())
                .setUrl(raid.getIdentifier().getGlobalUrl())
                .setPrimaryTitle(primaryTitle.getText())
                .setConfidential(!raid.getAccess().getType().getId().equals(ACCESS_TYPE_OPEN))
                .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
                .setMetadata(JSONB.valueOf(objectMapper.writeValueAsString(raid)))
                .setStartDate(DateUtil.parseDate(raid.getDate().getStartDate()))
                .setDateCreated(LocalDateTime.now())
                .setVersion(raid.getIdentifier().getVersion())
                .setStartDateString(raid.getDate().getStartDate())
                .setEndDate(raid.getDate().getEndDate())
                .setLicense(raid.getIdentifier().getLicense())
                .setAccessTypeId(accessTypeId)
                .setEmbargoExpiry(raid.getAccess().getEmbargoExpiry())
                .setAccessStatement(accessStatement)
                .setAccessStatementLanguageId(accessStatementLanguageId)
                .setSchemaUri(raid.getIdentifier().getSchemaUri())
                .setRegistrationAgencyOrganisationId(registrationAgencyOrganisationId)
                .setOwnerOrganisationId(ownerOrganisationId);
    }

    @SneakyThrows
    public RaidRecord create(final RaidDto raid) {
        final var handle = new Handle(raid.getIdentifier().getGlobalUrl());

        final var primaryTitle = raid.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .orElseThrow(() -> new InvalidTitleException(null));

        return new RaidRecord()
                .setVersion(raid.getIdentifier().getVersion())
                .setHandle(handle.toString())
                .setPrimaryTitle(primaryTitle.getText())
                .setServicePointId(raid.getIdentifier().getOwner().getServicePoint())
                .setUrl(raid.getIdentifier().getGlobalUrl())
                .setMetadata(JSONB.valueOf(objectMapper.writeValueAsString(raid)))
                .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
                .setStartDate(DateUtil.parseDate(raid.getDate().getStartDate()))
                .setDateCreated(LocalDateTime.now())
                .setConfidential(!raid.getAccess().getType().getId().equals(ACCESS_TYPE_OPEN));
    }

    public RaidRecord create(
            final RaidCreateRequest raid,
            final ApidsMintResponse apidsMintResponse,
            final ServicePointRecord servicePointRecord) {

        final var primaryTitle = raid.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .map(Title::getText)
                .orElse("");

        final String raidJson;
        try {
            raidJson = objectMapper.writeValueAsString(raid);
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException();
        }

        return new RaidRecord()
                .setVersion(raid.getIdentifier().getVersion())
                .setPrimaryTitle(primaryTitle)
                .setHandle(apidsMintResponse.identifier.handle)
                .setServicePointId(servicePointRecord.getId())
                .setUrl(apidsMintResponse.identifier.property.value)
                .setMetadata(JSONB.valueOf(raidJson))
                .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
                .setStartDate(DateUtil.parseDate(raid.getDate().getStartDate()))
                .setDateCreated(LocalDateTime.now())
                .setConfidential(!raid.getAccess().getType().getId().equals(ACCESS_TYPE_OPEN));
    }

    public RaidRecord merge(final RaidUpdateRequest raid, final RaidRecord existing) {

        final var primaryTitle = raid.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .orElseThrow(() -> new InvalidTitleException("One title with a titleType of 'Primary' should be specified."))
                .getText();

//        final var newVersion = raid.getIdentifier().getVersion() + 1;
//        raid.getIdentifier().version(newVersion);

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
                .setMetadata(JSONB.valueOf(raidJson))
                .setMetadataSchema(Metaschema.raido_metadata_schema_v2)
                .setStartDate(DateUtil.parseDate(raid.getDate().getStartDate()))
                .setConfidential(!raid.getAccess().getType().getId().equals(ACCESS_TYPE_OPEN));
    }
}