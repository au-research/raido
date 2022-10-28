package raido.apisvc.service.raid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.db.jooq.api_svc.tables.records.RaidV2Record;
import raido.idl.raidv2.model.IdBlock;
import raido.idl.raidv2.model.MetadataSchemaV1;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;
import static raido.apisvc.endpoint.message.ValidationMessage.METADATA_TOO_LARGE;
import static raido.apisvc.endpoint.message.ValidationMessage.metadataJsonParseFailure;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.ExceptionUtil.ise;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.areEqual;
import static raido.db.jooq.api_svc.enums.Metaschema.raido_metadata_schema_v1;
import static raido.idl.raidv2.model.Metaschema.RAIDO_METADATA_SCHEMA_V1;

@Component
public class MetadataService {
  private static final Log log = to(MetadataService.class);

  private ObjectMapper defaultMapper = defaultMapper();
  
  private MetadataProps metaProps;

  public MetadataService(MetadataProps metaProps) {
    this.metaProps = metaProps;
  }

  public String mapToJson(Object metadataInstance) 
  throws ValidationFailureException{
    String jsonValue = null;
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
  
  public MetadataSchemaV1 mapV1SchemaMetadata(RaidV2Record raid){
    var result = mapObject(raid.getMetadata(), MetadataSchemaV1.class);
    if( !areEqual(
      result.getMetadataSchema().getValue(), 
      raid.getMetadataSchema().getLiteral()) 
    ){
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
  
  public static Metaschema mapJs2Jq(raido.idl.raidv2.model.Metaschema schema){
    if( areEqual(schema.getValue(), raido_metadata_schema_v1.getLiteral()) ){
      return raido_metadata_schema_v1;
    }
    
    var ex = iae("unknown json metaschema value");
    log.with("schema", schema).error(ex.getMessage());
    throw ex;
  }

  public static raido.idl.raidv2.model.Metaschema mapJq2Js(Metaschema schema){
    if( areEqual(schema.getLiteral(), RAIDO_METADATA_SCHEMA_V1.getValue()) ){
      return RAIDO_METADATA_SCHEMA_V1;
    }
    
    var ex = iae("unknown json metaschema value");
    log.with("schema", schema).error(ex.getMessage());
    throw ex;
  }

  public String formatRaidoLandingPageUrl(String handle){
    return "%s/%s".formatted(metaProps.raidoLandingPrefix, handle);
  }

  public String formatGlobalUrl(String handle){
    return "%s/%s".formatted(metaProps.globalUrlPrefix, handle);
  }

  public IdBlock createIdBlock(String handle, String raidUrl) {
    return new IdBlock().
      identifier(handle).
      identifierTypeUri("https://raid.org").
      globalUrl(formatGlobalUrl(handle)).
      raidAgencyUrl(raidUrl).
      raidAgencyIdentifier(metaProps.raidAgencyIdentifier);
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


}

