package raido.loadtest.util;

import java.util.Map;

import static raido.apisvc.endpoint.anonymous.PublicEndpoint.STATUS_PATH;

public class RaidoApi {

  public enum Endpoint {
    listServicePoint("/v2/public/list-service-point/v1"),
    servicePoint("/v2/experimental/service-point/v1"),
    publicStatus(STATUS_PATH)
    ;

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
}
