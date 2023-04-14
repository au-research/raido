package raido.loadtest.config;

import raido.apisvc.util.Log;

import static java.lang.Integer.parseInt;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;

public class SimulationConfig {
  private static final Log log = to(SimulationConfig.class);
  
  public static final SimulationConfig simConfig = new SimulationConfig();

  public int rampUpSeconds;
  public int steadyStateSeconds;
  public int userCount;
  

  public SimulationConfig() {
    rampUpSeconds = parseInt(getConfig("rampUpSeconds", "10"));
    steadyStateSeconds = parseInt(getConfig("steadyStateSeconds", "20"));
    userCount = parseInt(getConfig("users", "1"));
  }

  public static String getConfig(String name, String defaultValue) {
    String configName = "%s.%s".formatted(
      SimulationConfig.class.getSimpleName(), name);

    String envValue = System.getenv(configName);
    if( hasValue(envValue) ){
      return envValue;
    }

    return System.getProperty(configName, defaultValue);

  }
}
