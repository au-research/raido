package raido.loadtest.util;

import io.gatling.javaapi.http.HttpRequestActionBuilder;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.AccessBlock;
import raido.idl.raidv2.model.DatesBlock;
import raido.idl.raidv2.model.DescriptionBlock;
import raido.idl.raidv2.model.RaidoMetadataSchemaV1;
import raido.idl.raidv2.model.TitleBlock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static raido.apisvc.endpoint.anonymous.PublicEndpoint.STATUS_PATH;
import static raido.apisvc.util.Log.to;
import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;

public class RaidoApi {
  private static final Log log = to(RaidoApi.class);
  
  /* hardcoded values to use from the load-test, helps us identify logs
  and database records that were created by load testing */
  public static final String ROR_ID = "https://ror.org/load-test";
  public static final String API_KEY_SUBJECT = "load-test-api-key";

  
  /* would like this to come from open-api generator code, but the spring 
  generator we use for api-svc doesn't work that way (the paths just get
  embedded as annotations on interface methods, can't access them).
  Might have to go through a whole separate generation step just for 
  load-testing if we really want to compile time checking on this (using the 
  "java" generator or something). */
  public enum Endpoint {
    listServicePoint("/v2/public/list-service-point/v1"),
    servicePoint("/v2/experimental/service-point/v1"),
    apiKey("/v2/experimental/api-key/v1"),
    listApiKey("/v2/experimental/service-point/v1/%s/list-api-key"),
    generateToken("/v2/experimental/generate-api-token/v1"),
    listRaids("/v2/experimental/list-raid/v2"),
    mintRaid("/v2/experimental/mint-raido-schema/v1"),
    readRaid("/v2/experimental/read-raid/v2"),
    publicStatus(STATUS_PATH);

    public final String url;

    Endpoint(String url) {
      this.url = url;
    }

  }
  
  public static Map<String, String> authzApiHeaders(String apiToken){
    return Map.of(
      "Authorization", "Bearer " + apiToken,
      "Content-Type", "application/json"
    );
  }

  public static HttpRequestActionBuilder authzApiHeaders(
    String apiTokenVar, 
    HttpRequestActionBuilder check
  ) {
    return check.
      header("Authorization", sess->{
        String apiToken = sess.getString(apiTokenVar);
        Guard.hasValue("%s session var is empty", apiToken);
        return "Bearer " + apiToken;
      }).
      header("Content-Type", "application/json");
  }


  
  
}
