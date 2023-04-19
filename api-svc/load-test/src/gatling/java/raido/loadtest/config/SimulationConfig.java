package raido.loadtest.config;

import raido.apisvc.util.Log;

import java.util.StringJoiner;

import static java.lang.Integer.parseInt;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.hasValue;

public class SimulationConfig {
  private static final Log log = to(SimulationConfig.class);
  
  public static final SimulationConfig simConfig = new SimulationConfig();

  public int rampUpSeconds;
  public int steadyStateSeconds;
  public int userCount;
  
  public long thinkTimeMultiplier;

  public SimulationConfig() {
    rampUpSeconds = parseInt(getConfig("rampUpSeconds", "10"));
    steadyStateSeconds = parseInt(getConfig("steadyStateSeconds", "20"));
    userCount = parseInt(getConfig("users", "1"));
    thinkTimeMultiplier = parseInt(getConfig("thinkTimeMultiplier", "1"));
    
    // do not commit
    // peak load is showing duplicate handle errors approx 2% of the time
    // pretty sure these errors are on the load-test side, the in-mem ApidsStub 
    // is likely not generating unique handles
//    thinkTimeMultiplier = 1;
//    userCount = 50;
//    steadyStateSeconds = 60;

    log.with("config", this.toString()).info("startup");
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

  @Override
  public String toString() {
    return new StringJoiner(
      ", ",
      SimulationConfig.class.getSimpleName() + "[",
      "]")
      .add("rampUpSeconds=" + rampUpSeconds)
      .add("steadyStateSeconds=" + steadyStateSeconds)
      .add("userCount=" + userCount)
      .add("thinkTimeMultiplier=" + thinkTimeMultiplier)
      .toString();
  }
}
