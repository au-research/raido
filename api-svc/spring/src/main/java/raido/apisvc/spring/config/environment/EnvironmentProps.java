package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class EnvironmentProps {
  private static final Log log = to(EnvironmentProps.class);
  
  @Value("${EnvironmentConfig.envName:unknown}")
  public String envName;

  @Value("${EnvironmentConfig.nodeId:unknown}")
  public String nodeId;

  @Value("${EnvironmentConfig.startTaskDelaySeconds:2}")
  public int startTaskDelaySeconds;

  
  @Value("${EnvironmentConfig.rootPathRedirect:https://www.raid.org.au}")
  public String rootPathRedirect;
  
  @Value("${EnvironmentConfig.raidoLandingPage:" +
    "https://localhost:7080/handle" +
    "}")
  public String raidoLandingPage;

}
