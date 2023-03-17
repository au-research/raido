package raido.apisvc.service.raid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;
import raido.apisvc.service.raid.RaidService.ReadRaidV2Data;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.IdBlock;
import raido.idl.raidv2.model.LegacyMetadataSchemaV1;
import raido.idl.raidv2.model.PublicClosedMetadataSchemaV1;
import raido.idl.raidv2.model.PublicRaidMetadataSchemaV1;
import raido.idl.raidv2.model.PublicReadRaidResponseV3;
import raido.idl.raidv2.model.RaidoMetadataSchemaV1;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;
import static raido.apisvc.endpoint.message.ValidationMessage.METADATA_TOO_LARGE;
import static raido.apisvc.endpoint.message.ValidationMessage.metadataJsonParseFailure;
import static raido.apisvc.util.DateUtil.local2Offset;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.ExceptionUtil.ise;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.areEqual;
import static raido.db.jooq.api_svc.enums.Metaschema.legacy_metadata_schema_v1;
import static raido.db.jooq.api_svc.enums.Metaschema.raido_metadata_schema_v1;
import static raido.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;

@Component
public class MetadataService {
  /* I think this should point top the metadata schema definition 
  in the readthedocs, or our github OpenApi definition, or the "formal 
  metadata schema" think MBA is working on? */
  public static final String RAID_ID_TYPE_URI = "https://raid.org";
  
  private static final Log log = to(MetadataService.class);

  private final ObjectMapper defaultMapper = defaultMapper();
  
  private final MetadataProps metaProps;

  public MetadataService(MetadataProps metaProps) {
    this.metaProps = metaProps;
  }

  public String mapToJson(Object metadataInstance) 
  throws ValidationFailureException {
    String jsonValue;
    try {
      jsonValue = defaultMapper.writeValueAsString(metadataInstance);
    }
    catch( JsonProcessingException e ){
      var failure = metadataJsonParseFailure();
      log.with("metadata", metadataInstance).
        with("message", e.getMessage()).
        warn(failure.getMessage());
      throw new ValidationFailureException(failure);
    }
    
    var failures = validateMetadataSize(jsonValue);
    if( !failures.isEmpty() ){
      throw new ValidationFailureException(failures);
    }
    
    return jsonValue;
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

  public <T> T mapObject(JSONB value, Class<T> type){
    try {
      return this.defaultMapper.readValue(value.data(), type);
    }
    catch( JsonProcessingException e ){
      throw new RuntimeException(e);
    }
  }
  
  public RaidoMetadataSchemaV1 mapV1SchemaMetadata(RaidRecord raid){
    var result = mapObject(raid.getMetadata(), RaidoMetadataSchemaV1.class);
    if( !(
      areEqual(
        result.getMetadataSchema().getValue(),
        RAIDOMETADATASCHEMAV1.getValue()
      ) &&
      raid.getMetadataSchema() == raido_metadata_schema_v1
    )){
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

  public LegacyMetadataSchemaV1 mapLegacyMetadata(RaidRecord raid){
    var result = mapObject(raid.getMetadata(), LegacyMetadataSchemaV1.class);
    if( !(
      areEqual(
        result.getMetadataSchema().getValue(),
        LEGACYMETADATASCHEMAV1.getValue()
      ) &&
      raid.getMetadataSchema() == legacy_metadata_schema_v1
    )){
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

  public static Metaschema mapApi2Db(
    raido.idl.raidv2.model.RaidoMetaschema schema
  ){
    if( areEqual(schema.getValue(), RAIDOMETADATASCHEMAV1.getValue()) ){
      return raido_metadata_schema_v1;
    }

    if( areEqual(schema.getValue(), LEGACYMETADATASCHEMAV1.getValue()) ){
      return legacy_metadata_schema_v1;
    }

    var ex = iae("unknown json metaschema value");
    log.with("schema", schema).error(ex.getMessage());
    throw ex;
  }

  public static raido.idl.raidv2.model.RaidoMetaschema mapDb2Api(
    Metaschema schema
  ){
    if( areEqual(schema.getLiteral(), raido_metadata_schema_v1.getLiteral()) ){
      return RAIDOMETADATASCHEMAV1;
    }

    if( areEqual(schema.getLiteral(), legacy_metadata_schema_v1.getLiteral()) ){
      return LEGACYMETADATASCHEMAV1;
    }

    var ex = iae("unknown json metaschema value");
    log.with("schema", schema).error(ex.getMessage());
    throw ex;
  }

  public String formatRaidoLandingPageUrl(String handle){
    return "%s/%s".formatted(metaProps.raidoLandingPrefix, handle);
  }

  public IdBlock createIdBlock(final IdentifierUrl id,
                               final ServicePointRecord servicePointRecord
  ) {
    return new IdBlock().
      identifier(id.formatUrl()).
      identifierSchemeURI(RAID_ID_TYPE_URI).
      identifierRegistrationAgency(metaProps.identifierRegistrationAgency).
      identifierOwner(servicePointRecord.getIdentifierOwner()).
      identifierServicePoint(servicePointRecord.getId()).
      globalUrl(id.handle().format(metaProps.globalUrlPrefix)).
      raidAgencyUrl(id.handle().format(metaProps.handleUrlPrefix));
  }

  /**
   This isn't so much for DoS prevention - too late for that to be effective. 
   Real DoS logic needs to be way out in front of the API, at the 
   cloudfront/gateway level.  
   This is about giving the customer usable feedback that the raid is too big.  
   The DoS logic will have a higher (byte-oriented) threshold,
   legit customers should rarely see the DoS failure, either they or we'll 
   see this message and figure things out from there.
   */
  public List<ValidationFailure> validateMetadataSize(String metadataAsJson) {
    if( metadataAsJson.length() > metaProps.maxMetadataChars ){
      log.with("jsonLength", metadataAsJson.length()).
        with("maxSize", metaProps.maxMetadataChars).
        warn(METADATA_TOO_LARGE.getMessage());
      return List.of(METADATA_TOO_LARGE);
    }
    return Collections.emptyList();
  }

  public MetadataProps getMetaProps() {
    return metaProps;
  }
  
  public PublicReadRaidResponseV3 mapRaidoV1SchemaToPublic(ReadRaidV2Data data){
    var metadata = mapV1SchemaMetadata(data.raid());
    if( metadata.getAccess().getType() != AccessType.OPEN ){
      return mapToPublicClosed(
        data.raid(), metadata.getId(), metadata.getAccess());
    }

    return new PublicReadRaidResponseV3().
      handle(data.raid().getHandle()).
      createDate(local2Offset(data.raid().getDateCreated())).
      servicePointId(data.servicePoint().getId()).
      servicePointName(data.servicePoint().getName()).
      metadata(new PublicRaidMetadataSchemaV1().
        // metadataSchema set by OpenAPI from the class type
        id(metadata.getId()).
        titles(metadata.getTitles()).
        dates(metadata.getDates()).
        descriptions(metadata.getDescriptions()).
        access(metadata.getAccess()).
        alternateUrls(metadata.getAlternateUrls()).
        contributors(metadata.getContributors()).
        organisations((metadata.getOrganisations()))
      );
  }
  
  public PublicReadRaidResponseV3 mapToPublicClosed(
    RaidRecord raid,
    IdBlock id, 
    AccessBlock access
  ){
    if( access.getType() == AccessType.OPEN ){
      var ex = ise("attempted to map open raid to ClosedSchema");
      log.with("handle", raid.getHandle()).
        with("columnSchema", raid.getMetadataSchema()).
        with("jsonAccessType", access.getType()).
        error(ex.getMessage());
      throw ise("access type mismatch between column and json");
    }
    
    return new PublicReadRaidResponseV3().
      handle(raid.getHandle()).
      createDate(local2Offset(raid.getDateCreated())).
      metadata(new PublicClosedMetadataSchemaV1().
        /* metadataSchema ignored because of the `@JsonIgnoreProperties` on 
        `PublicReadRaidMetadataResponseV1`.  The value sent down the wire 
        comes from the `@JsonSubTypes` depending on the class type. */
        id(id).
        access(access) 
      );

  }
  
  public PublicReadRaidResponseV3 mapLegacySchemaToPublic(ReadRaidV2Data data){
    var metadata = mapLegacyMetadata(data.raid());

    if( metadata.getAccess().getType() != AccessType.OPEN ){
      return mapToPublicClosed(
        data.raid(), metadata.getId(), metadata.getAccess());
    }

    return new PublicReadRaidResponseV3().
      handle(data.raid().getHandle()).
      createDate(local2Offset(data.raid().getDateCreated())).
      servicePointId(data.servicePoint().getId()).
      servicePointName(data.servicePoint().getName()).
      metadata(new PublicRaidMetadataSchemaV1().
        // metadataSchema set by OpenAPI from the class type
        id(metadata.getId()).
        titles(metadata.getTitles()).
        dates(metadata.getDates()).
        descriptions(metadata.getDescriptions()).
        access(metadata.getAccess()).
        alternateUrls(metadata.getAlternateUrls())
        // invalid because no contrib
      );
  }
}

