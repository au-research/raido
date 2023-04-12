package raido.loadtest.config;

import raido.apisvc.util.Log;

import static java.lang.Integer.parseInt;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;

public class SimulationConfig {
  private static final Log log = to(SimulationConfig.class);
  
  public static final SimulationConfig simConfig = new SimulationConfig();

  public int rampUp;
  public int steadyState;
  public int users;
  

  public SimulationConfig() {
    rampUp = parseInt(getConfig("rampUp", "5"));
    steadyState = parseInt(getConfig("steadyState", "5"));
    users = parseInt(getConfig("users", "10"));
    
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
