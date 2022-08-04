package raido.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import raido.service.raidv1.RaidV1AuthService;
import raido.spring.security.RaidoSecurityContextRepository;
import raido.spring.security.raidv1.RaidV1AuthenticationProvider;
import raido.util.Log;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static raido.util.Log.to;

// https://github.com/spring-projects/spring-security-samples/blob/655cf77ea4fed8dcd910b1151c126991bf5527d5/servlet/java-configuration/hello-security-explicit/src/main/java/example/SecurityConfiguration.java
@EnableWebSecurity
public class RaidV1WebSecurityConfig {
  private static final Log log = to(RaidV1WebSecurityConfig.class);

  public static final String RAID_V1_API = "/v1";
  public static final String PUBLIC = "/public";


  @Bean
  public SecurityFilterChain securityFilterChain(
    HttpSecurity http, 
    RaidV1AuthService raidSvc
  ) throws Exception {
    log.info("securityFilterChain()");
    http.
      authenticationProvider(
        new RaidV1AuthenticationProvider(raidSvc)).
      securityContext().
      securityContextRepository(new RaidoSecurityContextRepository()).
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