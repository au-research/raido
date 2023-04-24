package raido.loadtest.config;

import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;
import static raido.apisvc.util.StringUtil.mask;

/*
 Use the word "gatling" in the class name so that it's more obvious to the  
 observer that `-DGatlingRaidoServerConfig.apiKeyJwtSecret=xxx` is related to
 gatling test config.
*/
public class GatlingRaidoServerConfig {
  private static final Log log = to(GatlingRaidoServerConfig.class);
  
  public static final GatlingRaidoServerConfig serverConfig =
    new GatlingRaidoServerConfig();

  /** The baseUrl that will be used for all requests to the api-svc. */
  public String apiSvcUrl = "http://localhost:8080";
  public String apiKeyJwtSecret = "";

  // usually the same as the apiSvcUrl, but configurable in case I need it
  public String apiTokenIssuer;


  public GatlingRaidoServerConfig() {
    apiSvcUrl = getConfig("apiSvcUrl", apiSvcUrl);
    apiKeyJwtSecret = getConfig("apiKeyJwtSecret", apiKeyJwtSecret);
    apiTokenIssuer = getConfig("apiTokenIssuer", apiSvcUrl);

    Guard.hasValue("apiSvcUrl", apiSvcUrl);
    Guard.hasValue("apiKeyJwtSecret", apiKeyJwtSecret);
    Guard.hasValue("apiTokenIssuer", apiTokenIssuer);
    
    log.with("apiSvcUrl", apiSvcUrl).
      with("apiTokenIssuer", apiTokenIssuer).
      with("apiKeyJwtSecret", mask(apiKeyJwtSecret, 5)).
      info("raidoServer");
  }

  public static String getConfig(String name, String defaultValue) {
    String configName = "%s.%s".formatted(
      GatlingRaidoServerConfig.class.getSimpleName(), name);

    String envValue = System.getenv(configName);
    if( hasValue(envValue) ){
      return envValue;
    }

    return System.getProperty(configName, defaultValue);

  }
}
