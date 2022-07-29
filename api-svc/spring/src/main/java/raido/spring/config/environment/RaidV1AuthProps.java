package raido.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RaidV1AuthProps {
  @Value("${RaidV1Auth.jwtSecret}")
  public String jwtSecret;

  @Value("${RaidV1Auth.issuer:https://www.raid.org.au}")
  public String issuer;
}
