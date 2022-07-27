package raido.spring.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

public class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

  public SecurityWebApplicationInitializer() {
    super(RaidV1SecurityConfig.class);
  }

}