package raido.inttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.MetadataSchemaV1;
import raido.idl.raidv2.model.PublicReadRaidResponseV2;

import java.util.LinkedHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.idl.raidv2.model.Metaschema.RAIDO_METADATA_SCHEMA_V1;

/**
 convenience stuff for doing common stuff with the Raido API.
 Normally would be all static methods, but I don't think it's a good idea 
 to be constantly making new API client instantions (currently OpenFeign).
 
 Do the wrappers really matter?
 */
public class RaidoApiUtil {

  private PublicExperimentalApi publicApi;
  private ObjectMapper mapper;
  
  public RaidoApiUtil(PublicExperimentalApi publicApi, ObjectMapper mapper) {
    this.publicApi = publicApi;
    this.mapper = mapper;
  }

  public MetadataSchemaV1 readPublicV1RaidMeta(String handle){
    // improve: creating a client just for this is silly and wasteful

    var pubReadObject = publicApi.publicReadRaidV2(handle);
    var pubRead = (PublicReadRaidResponseV2) pubReadObject;

    assertThat(pubRead.getMetadata()).isInstanceOf(LinkedHashMap.class);
    MetadataSchemaV1 pubReadMeta = null;
    pubReadMeta = mapper.convertValue(
      pubRead.getMetadata(), MetadataSchemaV1.class);
    assertThat(pubReadMeta.getMetadataSchema()).
      isEqualTo(RAIDO_METADATA_SCHEMA_V1);

    return pubReadMeta;
  }


  public PublicExperimentalApi getPublicExperimintal() {
    return publicApi;
  }
}
