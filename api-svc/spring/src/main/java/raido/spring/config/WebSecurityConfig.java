package raido.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import raido.spring.security.BearerSecurityContextRepository;
import raido.util.Log;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static raido.util.Log.to;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  public static final String API = "/api";
  public static final String PUBLIC = "/public";

  private static final Log log = to(WebSecurityConfig.class);

  public WebSecurityConfig(
  ) {
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    http.
//      authenticationProvider(
//        new RaidV1AuthenticationProvider(jwkProvider, issuer, audience).
      securityContext().
      securityContextRepository(new BearerSecurityContextRepository()).
      and().
      exceptionHandling().
      and().authorizeRequests().
      mvcMatchers(API + "/**").fullyAuthenticated().
      mvcMatchers(PUBLIC + "/**").permitAll().
      anyRequest().denyAll().
      and().
      httpBasic().disable().
      csrf().disable().
      sessionManagement().sessionCreationPolicy(STATELESS);
  }
}
