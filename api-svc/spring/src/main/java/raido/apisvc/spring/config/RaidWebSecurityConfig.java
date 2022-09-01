package raido.apisvc.spring.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import raido.apisvc.service.raidv1.RaidV1AuthService;
import raido.apisvc.spring.security.RaidoSecurityContextRepository;
import raido.apisvc.spring.security.raidv1.RaidV1AuthenticationProvider;
import raido.apisvc.util.Log;

import java.io.IOException;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static raido.apisvc.endpoint.auth.AuthnEndpoint.IDP_URL;
import static raido.apisvc.endpoint.raidv1.RaidV1.HANDLE_URL_PREFIX;
import static raido.apisvc.util.Log.to;

// https://github.com/spring-projects/spring-security-samples/blob/655cf77ea4fed8dcd910b1151c126991bf5527d5/servlet/java-configuration/hello-security-explicit/src/main/java/example/SecurityConfiguration.java
@EnableWebSecurity
public class RaidWebSecurityConfig {
  private static final Log log = to(RaidWebSecurityConfig.class);

  public static final String RAID_V1_API = "/v1";
  public static final String RAID_V2_API = "/v2";
  public static final String RAID_V2_PUBLIC_API = RAID_V2_API + "/public";
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
      // order is important, more specific has to come before more general
      mvcMatchers(RAID_V1_API + HANDLE_URL_PREFIX + "/**" ).permitAll().
      mvcMatchers(RAID_V1_API + "/**").fullyAuthenticated().
      mvcMatchers(RAID_V2_PUBLIC_API + "/**").permitAll().
      mvcMatchers(IDP_URL).permitAll().
      mvcMatchers(PUBLIC + "/**").permitAll().
      anyRequest().denyAll().
      and().
      httpBasic().disable().
      csrf().disable().
      sessionManagement().sessionCreationPolicy(STATELESS);

    return http.build();
  }

  @Bean
  public RequestRejectedHandler requestRejectedHandler() {
    /* sends an error response with a configurable status code (default is 400 
     BAD_REQUEST) we can pass a different value in the constructor. */
    return new HttpStatusRequestRejectedHandler(){
      @Override
      public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        RequestRejectedException ex
      ) throws IOException {
        /* i don't think we want a stack trace here? 
        user/principal stuff will not be populated because this tends to happen
        very early in request processing. */
        log.with("method", request.getMethod()).
          with("uri", request.getRequestURI()).
          with("params", request.getParameterMap()).
          with("message", ex.getMessage()).
          info("Request rejected");
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
      }
    };
  }

  /** If encoded slashes aren't allowed, then calls of the form:
  `http://localhost:8080/v1/handle/102.100.100%2F75517`
  would get rejected by HttpStrictFirewall.  Which is unfortunate, because
  handles contain slashes as defined in the ISO standard. 😢
   */
  @Bean 
  public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
    log.info("allowUrlEncodedSlashHttpFirewall()");
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowUrlEncodedSlash(true);
    return firewall;
  }

  
}