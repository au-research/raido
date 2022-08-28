package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RaidV2AuthProps {
  // security:sto implement check that this is long enough and select signing key at startup
  @Value("${RaidV2Auth.jwtSecrets}")
  public List<String> jwtSecrets;

  @Value("${RaidV2Auth.issuer:https://localhost:8080}")
  public String issuer;
}
