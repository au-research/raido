package raido.apisvc.spring.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.HttpStatusRequestRejectedHandler;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.RequestRejectedHandler;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import raido.apisvc.service.auth.RaidV2ApiKeyAuthService;
import raido.apisvc.service.auth.RaidV2AppUserAuthService;
import raido.apisvc.service.raidv1.RaidV1AuthService;
import raido.apisvc.spring.security.RaidoSecurityContextRepository;
import raido.apisvc.spring.security.raidv1.RaidV1AuthenticationProvider;
import raido.apisvc.spring.security.raidv1.RaidV2AuthenticationProvider;
import raido.apisvc.util.Log;

import java.io.IOException;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static raido.apisvc.endpoint.auth.AppUserAuthnEndpoint.IDP_URL;
import static raido.apisvc.endpoint.raidv1.RaidV1.HANDLE_URL_PREFIX;
import static raido.apisvc.util.Log.to;

@EnableWebSecurity
/* between Spring 6.0.0-M6 and RC1, something changed so that the 
`@Configuration` annotation is necessary - was getting "NoSuchBean: 
springSecurityFilterChain", apparently cause this wasn't being found, so 
spring wasn't finding our `securityFilterChain` definition and defaulted
to expecting there to be a `springSecurityFilterChain` bean. 
The @Configuration anno is now present on the spring-security-samples code. */
@Configuration
public class RaidWebSecurityConfig {
  private static final Log log = to(RaidWebSecurityConfig.class);

  public static final String RAID_V1_API = "/v1";
  public static final String RAID_V2_API = "/v2";
  public static final String RAID_V2_PUBLIC_API = RAID_V2_API + "/public";
  public static final String PUBLIC = "/public";


  /* the name is significant - when prefixed "spring", got error about  
  it returning the wrong type (it wanted a `Filter` instead of FilterChain. */
  @Bean
  public SecurityFilterChain securityFilterChain(
    HttpSecurity http, 
    AuthenticationManager authnManager
  ) throws Exception {
    log.info("securityFilterChain()");

    /* repo checks if user is authenticating with a raidV1 or raidV2 token
    (currently based on what endpoint they're accessing)  
    and populates the SecurityContext with a "pre-auth" token. */
    http.securityContext().securityContextRepository(
      new RaidoSecurityContextRepository() );
    
    /* AuthnManager contains multiple AuthnProviders, these are the what 
    verifies the specific type of token (raidV1 or V2) is secure, then swaps 
    the pre-auth token in the SecurityContext for a "post-auth" token. */
    http.authenticationManager(authnManager);

    /* deprecation notice says to use `authorizeHttpRequests()` but that 
    causes 403 errors because ProviderManager never gets called; 
    thus AuthnProviders don't get called, so the security context still 
    contains the pre-auth token instead of a verified post-auth token, 
    so the eventual call to AuthorizationStrategy.isGranted() ends up 
    calling isAuthenticated() on the pre-auth token, which (correctly) 
    returns false and the request is denied. */
    //noinspection deprecation
    http.authorizeRequests(). // authorizeHttpRequests().
      // order is important, more specific has to come before more general
      requestMatchers(RAID_V1_API + HANDLE_URL_PREFIX + "/**" ).permitAll().
      requestMatchers(RAID_V2_PUBLIC_API + "/**").permitAll().
      requestMatchers(IDP_URL).permitAll().
      /* Used only for the status endpoint; either make this explicit (no 
      wildcard, like IDP_URL) or better, move status endpoint under `/v2`. 
      Remember to update ASG health check, ALB rules, cloudfront rules. */
      requestMatchers(PUBLIC + "/**").permitAll().
      requestMatchers(RAID_V1_API + "/**").fullyAuthenticated().
      requestMatchers(RAID_V2_API + "/**").fullyAuthenticated().
      // "default deny" anything not explicitly allowed above
      anyRequest().denyAll();

    http.httpBasic().disable().
      /* api-svc is stateless and the browser client does not use cookies;
      in the baeldung article, section 2.1 and 2.2 are most applicable:
      https://www.baeldung.com/csrf-stateless-rest-api
      Spring-security ref doco itself only talks about CSRF being a concern 
      when the the token is stored in a cookie, which we don't.
      https://docs.spring.io/spring-security/reference/features/exploits/csrf.html#csrf-when-stateless
      That said, we're still open to a CSRF implementation, as long as it works 
      in an architecture of stateless backend nodes.  Note that CSRF strategies 
      involving replicated sessions are not likely to be accepted. 
      We would have to build the infrastructure for it - which would be an 
      unworkable amount of effort given the current threat model and our 
      available infrastructure resourcing constraints. */
      csrf().disable().
      sessionManagement().sessionCreationPolicy(STATELESS);
      
    http.headers().
      /* https://www.baeldung.com/spring-prevent-xss */
      xssProtection().and().
      /* No real point in doing this - api-svc only serves data.
      Added to avoid arguments and false-positives on security scans. */
      contentSecurityPolicy("script-src 'self'");

    // supposed to be implied by @EnableWebSecurity
    // put it back or remove the code after the big error handling refactor
    // http.exceptionHandling();

    return http.build();
  }

  /* AuthnProviders can be inlined directly into the http config, but it's 
  more readable this way. */
  @Bean
  public AuthenticationManager authenticationManager(
    RaidV1AuthService raid1Svc,
    RaidV2AppUserAuthService appUserAuthSvc,
    RaidV2ApiKeyAuthService apiKeyAuthSvc
  ){
    RaidV2AuthenticationProvider raidV2AuthProvider =
      new RaidV2AuthenticationProvider(appUserAuthSvc, apiKeyAuthSvc);
    RaidV1AuthenticationProvider raidV1AuthProvider =
      new RaidV1AuthenticationProvider(raid1Svc);

    return new ProviderManager(raidV2AuthProvider, raidV1AuthProvider);
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
  would get rejected by HttpStrictFirewall.  Which is unfortunate because
  handles contain slashes as defined in the ISO standard. 
  Most client-technologies (Feign, RestTemplate, openapi-fetch, etc.) will, by 
  default, percent-encode data that is passed to them as a "parameter value". 
  😢  */
  @Bean 
  public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
    log.info("allowUrlEncodedSlashHttpFirewall()");
    StrictHttpFirewall firewall = new StrictHttpFirewall();
    firewall.setAllowUrlEncodedSlash(true);
    return firewall;
  }
  
}