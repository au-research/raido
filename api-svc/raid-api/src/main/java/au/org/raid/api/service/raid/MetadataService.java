package au.org.raid.api.service.raid;

import au.org.raid.api.service.raid.RaidService.ReadRaidV2Data;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.api.util.Log;
import au.org.raid.db.jooq.enums.Metaschema;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.METADATA_TOO_LARGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.metadataJsonParseFailure;
import static au.org.raid.api.util.DateUtil.local2Offset;
import static au.org.raid.api.util.ExceptionUtil.*;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.areEqual;
import static au.org.raid.api.util.StringUtil.isBlank;
import static au.org.raid.db.jooq.enums.Metaschema.*;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.RaidoMetaschemaV2.RAIDOMETADATASCHEMAV2;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;

@Component
public class MetadataService {
    /* I think this should point top the metadata schema definition
    in the readthedocs, or our github OpenApi definition, or the "formal
    metadata schema" think MBA is working on? */
    public static final String RAID_ID_TYPE_URI = "https://raid.org/";
    /* driven by the openapi spec, but I don't think there's any way to access
    the property name programmatically, the open-api generator for spring is all
    about generating annotations, not this kind of usage (same problem as
    encountered when trying to reference the urls for load-testing from gatling */
    public static final String METADATA_SCHEMA_PROP = "metadataSchema";
    private static final Log log = to(MetadataService.class);
    private final ObjectMapper defaultMapper = defaultMapper();

    private final MetadataProps metaProps;

    public MetadataService(MetadataProps metaProps) {
        this.metaProps = metaProps;
    }

    public static ObjectMapper defaultMapper() {
        return new ObjectMapper().
                /* copied from the default mapper in ApiConfig,
                I don't want number timestamps in the DB either.
                Not sure if this should be bean instead of just a field. */
                        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).
                /* without this, converting the metadata to json string gave:
                "Java 8 date/time type `java.time.LocalDate` not supported by default" */
                        registerModule(new JavaTimeModule()).
                enable(ALLOW_UNQUOTED_FIELD_NAMES).
                // fields where value is null or Optional will not be written
                        setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    public static Metaschema mapApi2Db(
            au.org.raid.idl.raidv2.model.RaidoMetaschema schema
    ) {
        if (areEqual(schema.getValue(), RAIDOMETADATASCHEMAV1.getValue())) {
            return raido_metadata_schema_v1;
        }

        if (areEqual(schema.getValue(), LEGACYMETADATASCHEMAV1.getValue())) {
            return legacy_metadata_schema_v1;
        }

        var ex = iae("unknown json metaschema value");
        log.with("schema", schema).error(ex.getMessage());
        throw ex;
    }

    public static Metaschema mapApi2Db(
            au.org.raid.idl.raidv2.model.RaidoMetaschemaV2 schema
    ) {
        if (areEqual(schema.getValue(), RAIDOMETADATASCHEMAV2.getValue())) {
            return raido_metadata_schema_v2;
        }

        if (areEqual(schema.getValue(), RAIDOMETADATASCHEMAV1.getValue())) {
            return raido_metadata_schema_v1;
        }

        if (areEqual(schema.getValue(), LEGACYMETADATASCHEMAV1.getValue())) {
            return legacy_metadata_schema_v1;
        }

        var ex = iae("unknown json metaschema value");
        log.with("schema", schema).error(ex.getMessage());
        throw ex;
    }

    public static au.org.raid.idl.raidv2.model.RaidoMetaschema mapDb2Api(
            Metaschema schema
    ) {
        if (areEqual(schema.getLiteral(), raido_metadata_schema_v1.getLiteral())) {
            return RAIDOMETADATASCHEMAV1;
        }

        if (areEqual(schema.getLiteral(), legacy_metadata_schema_v1.getLiteral())) {
            return LEGACYMETADATASCHEMAV1;
        }

        var ex = iae("unknown json metaschema value");
        log.with("schema", schema).error(ex.getMessage());
        throw ex;
    }

    public static au.org.raid.idl.raidv2.model.RaidoMetaschemaV2 mapDb2ApiV2(
            Metaschema schema
    ) {
        if (areEqual(schema.getLiteral(), raido_metadata_schema_v2.getLiteral())) {
            return RaidoMetaschemaV2.RAIDOMETADATASCHEMAV2;
        }

        if (areEqual(schema.getLiteral(), raido_metadata_schema_v1.getLiteral())) {
            return RaidoMetaschemaV2.RAIDOMETADATASCHEMAV1;
        }

        if (areEqual(schema.getLiteral(), legacy_metadata_schema_v1.getLiteral())) {
            return RaidoMetaschemaV2.LEGACYMETADATASCHEMAV1;
        }

        var ex = iae("unknown json metaschema value");
        log.with("schema", schema).error(ex.getMessage());
        throw ex;
    }

    public String mapToJson(Object metadataInstance)
            throws ValidationFailureException {
        String jsonValue;
        try {
            jsonValue = defaultMapper.writeValueAsString(metadataInstance);
        } catch (JsonProcessingException e) {
            var failure = metadataJsonParseFailure();
            log.with("metadata", metadataInstance).
                    with("message", e.getMessage()).
                    warn(failure.getMessage());
            throw new ValidationFailureException(failure);
        }

        var failures = validateMetadataSize(jsonValue);
        if (!failures.isEmpty()) {
            throw new ValidationFailureException(failures);
        }

        return jsonValue;
    }

    public <T> T mapObject(JSONB value, Class<T> type) {
        return mapObject(value.data(), type);
    }

    public <T> T mapObject(String value, Class<T> type) {
        try {
            return this.defaultMapper.readValue(value, type);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public RaidoMetadataSchemaV2 mapV2SchemaMetadata(RaidRecord raid) {
        var raidoMetadataSchemaV2 =
                mapObject(raid.getMetadata(), RaidoMetadataSchemaV2.class);
        raidoMetadataSchemaV2.metadataSchema(RAIDOMETADATASCHEMAV2);
        return raidoMetadataSchemaV2;

    }

    public RaidoMetadataSchemaV1 mapV1SchemaMetadata(RaidRecord raid) {
        var result = mapObject(raid.getMetadata(), RaidoMetadataSchemaV1.class);
        if (!(
                areEqual(
                        result.getMetadataSchema().getValue(),
                        RAIDOMETADATASCHEMAV1.getValue()
                ) &&
                        raid.getMetadataSchema() == raido_metadata_schema_v1
        )) {
            var ex = ise("DB column / JSON field mismatched schema");
            log.with("handle", raid.getHandle()).
                    with("columnSchema", raid.getMetadataSchema()).
                    with("jsonSchema", result.getMetadataSchema()).
                    error(ex.getMessage());
            throw ise("mismatch between column and json: %s",
                    raid.getMetadataSchema());
        }
        return result;
    }

    public LegacyMetadataSchemaV1 mapLegacyMetadata(RaidRecord raid) {
        var result = mapObject(raid.getMetadata(), LegacyMetadataSchemaV1.class);
        if (!(
                areEqual(
                        result.getMetadataSchema().getValue(),
                        LEGACYMETADATASCHEMAV1.getValue()
                ) &&
                        raid.getMetadataSchema() == legacy_metadata_schema_v1
        )) {
            var ex = ise("DB column / JSON field mismatched schema");
            log.with("handle", raid.getHandle()).
                    with("columnSchema", raid.getMetadataSchema()).
                    with("jsonSchema", result.getMetadataSchema()).
                    error(ex.getMessage());
            throw ise("mismatch between column and json: %s",
                    raid.getMetadataSchema());
        }
        return result;
    }

    public String formatRaidoLandingPageUrl(String handle) {
        return "%s/%s".formatted(metaProps.getRaidoLandingPrefix(), handle);
    }

    public IdBlock createIdBlock(final IdentifierUrl id,
                                 final ServicePointRecord servicePointRecord
    ) {
        return new IdBlock().
                identifier(id.formatUrl()).
                identifierSchemeURI(RAID_ID_TYPE_URI).
                identifierRegistrationAgency(metaProps.getIdentifierRegistrationAgency()).
                identifierOwner(servicePointRecord.getIdentifierOwner()).
                identifierServicePoint(servicePointRecord.getId()).
                globalUrl(id.handle().format(metaProps.getGlobalUrlPrefix())).
                raidAgencyUrl(id.handle().format(metaProps.getHandleUrlPrefix())).
                version(1);
    }

    /**
     * This isn't so much for DoS prevention - too late for that to be effective.
     * Real DoS logic needs to be way out in front of the API, at the
     * cloudfront/gateway level.
     * This is about giving the customer usable feedback that the raid is too big.
     * The DoS logic will have a higher (byte-oriented) threshold,
     * legit customers should rarely see the DoS failure, either they or we'll
     * see this message and figure things out from there.
     */
    public List<ValidationFailure> validateMetadataSize(String metadataAsJson) {
        if (metadataAsJson.length() > metaProps.getMaxMetadataChars()) {
            log.with("jsonLength", metadataAsJson.length()).
                    with("maxSize", metaProps.getMaxMetadataChars()).
                    warn(METADATA_TOO_LARGE.getMessage());
            return List.of(METADATA_TOO_LARGE);
        }
        return Collections.emptyList();
    }

    public MetadataProps getMetaProps() {
        return metaProps;
    }

    public PublicReadRaidMetadataResponseV1 mapRaidoV1SchemaToPublicMetadata(
            RaidRecord raid
    ) {
        var metadata = mapV1SchemaMetadata(raid);
        if (metadata.getAccess().getType() != AccessType.OPEN) {
            return mapToPublicClosedMetadata(
                    raid, metadata.getId(), metadata.getAccess());
        }

        return new PublicRaidMetadataSchemaV1().
                // metadataSchema set by OpenAPI from the class type
                        id(metadata.getId()).
                titles(metadata.getTitles()).
                dates(metadata.getDates()).
                descriptions(metadata.getDescriptions()).
                access(metadata.getAccess()).
                alternateUrls(metadata.getAlternateUrls()).
                contributors(metadata.getContributors()).
                organisations((metadata.getOrganisations())).
                subjects(metadata.getSubjects()).
                relatedRaids(metadata.getRelatedRaids()).
                relatedObjects(metadata.getRelatedObjects()).
                alternateIdentifiers(metadata.getAlternateIdentifiers()).
                spatialCoverages(metadata.getSpatialCoverages()).
                traditionalKnowledgeLabels(metadata.getTraditionalKnowledgeLabels())
                ;
    }

    public PublicReadRaidMetadataResponseV1 mapRaidoV2SchemaToPublicMetadata(
            RaidRecord raid
    ) {
        var metadata = mapV2SchemaMetadata(raid);
        if (metadata.getAccess().getType() != AccessType.OPEN) {
            return mapToPublicClosedMetadata(
                    raid, metadata.getId(), metadata.getAccess());
        }

        return new PublicRaidMetadataSchemaV1().
                // metadataSchema set by OpenAPI from the class type
                        id(metadata.getId()).
                titles(metadata.getTitles()).
                dates(metadata.getDates()).
                descriptions(metadata.getDescriptions()).
                access(metadata.getAccess()).
                alternateUrls(metadata.getAlternateUrls()).
                contributors(metadata.getContributors()).
                organisations((metadata.getOrganisations()))
                ;
    }

    public PublicReadRaidMetadataResponseV1 mapToPublicClosedMetadata(
            RaidRecord raid,
            IdBlock id,
            AccessBlock access
    ) {
        if (access.getType() == AccessType.OPEN) {
            var ex = ise("attempted to map open raid to ClosedSchema");
            log.with("handle", raid.getHandle()).
                    with("columnSchema", raid.getMetadataSchema()).
                    with("jsonAccessType", access.getType()).
                    error(ex.getMessage());
            throw ise("access type mismatch between column and json");
        }

        return new PublicClosedMetadataSchemaV1().
                /* metadataSchema ignored because of the `@JsonIgnoreProperties` on
                `PublicReadRaidMetadataResponseV1`.  The value sent down the wire
                comes from the `@JsonSubTypes` depending on the class type. */
                        id(id).
                access(access);
    }

    public PublicReadRaidMetadataResponseV1 mapLegacySchemaToPublicMetadata(
            RaidRecord raid
    ) {
        var metadata = mapLegacyMetadata(raid);

        if (metadata.getAccess().getType() != AccessType.OPEN) {
            return mapToPublicClosedMetadata(
                    raid, metadata.getId(), metadata.getAccess());
        }

        return new PublicRaidMetadataSchemaV1().
                // metadataSchema set by OpenAPI from the class type
                        id(metadata.getId()).
                titles(metadata.getTitles()).
                dates(metadata.getDates()).
                descriptions(metadata.getDescriptions()).
                access(metadata.getAccess()).
                alternateUrls(metadata.getAlternateUrls())
                // invalid because no contrib
                ;
    }

    public PublicReadRaidResponseV3 mapPublicReadResponse(
            ReadRaidV2Data data
    ) {
        var metadata = mapPublicReadMetadataResponse(data.raid());

        if (data.raid().getConfidential()) {
            return new PublicReadRaidResponseV3().
                    handle(data.raid().getHandle()).
                    createDate(local2Offset(data.raid().getDateCreated())).
                    // we don't populate the service-point data for closed
                            metadata(metadata);
        }

        return new PublicReadRaidResponseV3().
                handle(data.raid().getHandle()).
                createDate(local2Offset(data.raid().getDateCreated())).
                servicePointId(data.servicePoint().getId()).
                servicePointName(data.servicePoint().getName()).
                metadata(metadata);
    }

    public PublicReadRaidMetadataResponseV1 mapPublicReadMetadataResponse(
            RaidRecord raid
    ) {
        Metaschema schema = raid.getMetadataSchema();

        if (schema == legacy_metadata_schema_v1) {
            return mapLegacySchemaToPublicMetadata(raid);
        }

        if (schema == raido_metadata_schema_v1) {
            return mapRaidoV1SchemaToPublicMetadata(raid);
        }

        if (schema == raido_metadata_schema_v2) {
            return mapRaidoV2SchemaToPublicMetadata(raid);
        }

        var ex = ise("unknown raid schema");
        log.with("schema", schema).
                with("handle", raid.getHandle()).
                error(ex.getMessage());
        throw ex;
    }

    public PublicReadRaidMetadataResponseV1 parsePublicRaidMetadata(
            String line
    ) {
        try (var parser = defaultMapper.getFactory().createParser(line)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                throw runtimeException("Expected START_OBJECT: %s", line);
            }

            String metadataSchema = null;
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                String name = parser.getCurrentName();
                parser.nextToken();

                if (METADATA_SCHEMA_PROP.equals(name)) {
                    metadataSchema = parser.getText();
                    break;
                }
            }

            if (isBlank(metadataSchema)) {
                throw runtimeException("Expected a value for `%s`: %s",
                        METADATA_SCHEMA_PROP, line);
            }

            if (areEqual(metadataSchema,
                    PublicClosedMetadataSchemaV1.class.getSimpleName())
            ) {
                return defaultMapper.readValue(line,
                        PublicClosedMetadataSchemaV1.class);
            } else if (areEqual(metadataSchema,
                    PublicRaidMetadataSchemaV1.class.getSimpleName())
            ) {
                return defaultMapper.readValue(line,
                        PublicRaidMetadataSchemaV1.class);
            } else {
                throw runtimeException("unknown raid metadataSchema `%s`: %s",
                        metadataSchema, line);
            }

        } catch (IOException e) {
            throw wrapIoException(e, "while parsing line: ", line);
        }
    }
}

