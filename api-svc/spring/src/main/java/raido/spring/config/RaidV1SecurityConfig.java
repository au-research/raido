package raido.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import raido.spring.security.BearerSecurityContextRepository;
import raido.spring.security.RaidV1AuthenticationProvider;
import raido.util.Log;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static raido.util.Log.to;

// https://github.com/spring-projects/spring-security-samples/blob/655cf77ea4fed8dcd910b1151c126991bf5527d5/servlet/java-configuration/hello-security-explicit/src/main/java/example/SecurityConfiguration.java
@EnableWebSecurity
public class RaidV1SecurityConfig {

  //  extends WebSecurityConfigurerAdapter {
    public static final String RAID_V1_API = "/v1";
  public static final String PUBLIC = "/public";
  private static final Log log = to(RaidV1SecurityConfig.class);

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.
      authenticationProvider(new RaidV1AuthenticationProvider()).
      securityContext().
      securityContextRepository(new BearerSecurityContextRepository()).
      and().
      exceptionHandling().
      and().authorizeRequests().
      mvcMatchers(RAID_V1_API + "/**").fullyAuthenticated().
      mvcMatchers(PUBLIC + "/**").permitAll().
      anyRequest().denyAll().
      and().
      httpBasic().disable().
      csrf().disable().
      sessionManagement().sessionCreationPolicy(STATELESS);

    return http.build();
  }

}