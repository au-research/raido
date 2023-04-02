package raido.loadtest;

import raido.apisvc.util.Guard;

import static raido.apisvc.util.StringUtil.hasValue;

public class RaidoConfig {
  /** The baseUrl that will be used for all requests to the api-svc. */
  public String apiSvcUrl = "http://localhost:8080";
  public String apiKeyJwtSecret = "";

  public RaidoConfig() {
    apiSvcUrl = getConfig("apiSvcUrl", apiSvcUrl);
    apiKeyJwtSecret = getConfig("apiKeyJwtSecret", apiKeyJwtSecret);

    Guard.hasValue("apiKeyJwtSecret", apiKeyJwtSecret);

  }

  public static String getConfig(String name, String defaultValue) {
    String configName = "%s.%s".formatted(
      RaidoConfig.class.getSimpleName(), name);

    String envValue = System.getenv(configName);
    if( hasValue(envValue) ){
      return envValue;
    }

    return System.getProperty(configName, defaultValue);

  }
}
