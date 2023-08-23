package raido.loadtest.util;

import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import java.util.Map;

import static au.org.raid.api.endpoint.anonymous.PublicEndpoint.STATUS_PATH;
import static au.org.raid.api.util.Log.to;

public class RaidoApi {
  private static final Log log = to(RaidoApi.class);

  // needs to be a real ror if you don't run against the in-mem stub
  public static final String ROR_ID = "https://ror.org/015w2mp89";
  public static final String API_KEY_SUBJECT = "load-test-api-key";

  
  /* would like this to come from open-api generator code, but the spring 
  generator we use for api-svc doesn't work that way (the paths just get
  embedded as annotations on interface methods, can't access them).
  Might have to go through a whole separate generation step just for 
  load-testing if we really want to compile time checking on this (using the 
  "java" generator or something). */
  public enum Endpoint {
    publicListServicePoint("/v2/public/list-service-point/v1"),
    servicePoint("/v2/experimental/service-point/v1"),
    apiKey("/v2/experimental/api-key/v1"),
    listApiKey("/v2/experimental/service-point/v1/%s/list-api-key"),
    generateToken("/v2/experimental/generate-api-token/v1"),
    listRaids("/v2/experimental/list-raid/v2"),
    mintRaid("/v2/experimental/mint-raido-schema/v1"),
    updateRaid("/v2/experimental/update-raido-schema/v1"),
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
