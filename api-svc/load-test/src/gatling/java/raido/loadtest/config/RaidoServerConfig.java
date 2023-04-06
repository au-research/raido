package raido.loadtest.config;

import raido.apisvc.util.Guard;

import static raido.apisvc.util.StringUtil.hasValue;

public class RaidoServerConfig {
  
  public static final RaidoServerConfig serverConfig = new RaidoServerConfig();
  
  /** The baseUrl that will be used for all requests to the api-svc. */
  public String apiSvcUrl = "http://localhost:8080";
  public String apiKeyJwtSecret = "";

  public RaidoServerConfig() {
    apiSvcUrl = getConfig("apiSvcUrl", apiSvcUrl);
    apiKeyJwtSecret = getConfig("apiKeyJwtSecret", apiKeyJwtSecret);

    Guard.hasValue("apiKeyJwtSecret", apiKeyJwtSecret);

  }

  public static String getConfig(String name, String defaultValue) {
    String configName = "%s.%s".formatted(
      RaidoServerConfig.class.getSimpleName(), name);

    String envValue = System.getenv(configName);
    if( hasValue(envValue) ){
      return envValue;
    }

    return System.getProperty(configName, defaultValue);

  }
}
