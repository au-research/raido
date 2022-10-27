package raido.apisvc.service.raid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.jooq.JSONB;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.db.jooq.api_svc.tables.records.RaidV2Record;
import raido.idl.raidv2.model.MetadataSchemaV1;

import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES;
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
  private ObjectMapper indentedMapper = 
    defaultMapper().enable(SerializationFeature.INDENT_OUTPUT);

  public String mapToJson(Object metadataInstance){
    String jsonValue = null;
    try {
      jsonValue = defaultMapper.writeValueAsString(metadataInstance);
    }
    catch( JsonProcessingException e ){
      log.with("metadata", metadataInstance).error(e.getMessage());
      throw new RuntimeException(e);
    }
    
    return jsonValue;
  }

  public String mapToIndentedJson(Object metadataInstance){
    String jsonValue = null;
    try {
      jsonValue = indentedMapper.writerWithDefaultPrettyPrinter().writeValueAsString(metadataInstance);
    }
    catch( JsonProcessingException e ){
      log.with("metadata", metadataInstance).error(e.getMessage());
      throw new RuntimeException(e);
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
      throw ise("mismatch between column and json: %s", raid.getMetadataSchema());
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
}

