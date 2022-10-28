package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentProps {
  @Value("${EnvironmentConfig.envName:unknown}")
  public String envName;

  @Value("${EnvironmentConfig.nodeId:unknown}")
  public String nodeId;

  @Value("${EnvironmentConfig.startTaskDelaySeconds:2}")
  public int startTaskDelaySeconds;

}
