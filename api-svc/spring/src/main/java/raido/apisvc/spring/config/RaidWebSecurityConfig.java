package raido.apisvc.spring.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManagerResolver;
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
import raido.apisvc.spring.security.raidv1.RaidV1AuthenticationProvider;
import raido.apisvc.spring.security.raidv2.RaidV2AuthenticationProvider;
import raido.apisvc.util.ExceptionUtil;
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
  public static final String RAID_STABLE_API = "/raid";
  public static final String RAID_V2_PUBLIC_API = RAID_V2_API + "/public";
  public static final String PUBLIC = "/public";


  /* the name is significant - when prefixed "spring", got error about  
  it returning the wrong type (it wanted a `Filter` instead of FilterChain. */
  @Bean
  public SecurityFilterChain securityFilterChain(
    HttpSecurity http,  
    AuthenticationManagerResolver<HttpServletRequest>  tokenResolver
  ) throws Exception {
    log.info("securityFilterChain()");

    http.authorizeHttpRequests().
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
      requestMatchers(RAID_STABLE_API + "/**").fullyAuthenticated().

      /** everything above this counts as an "API endpoint" and uses standard 
      spring RestController mappings to match paths and methods, etc.
      Everything not matching the above, is now handled by the 
      {@link raido.apisvc.endpoint.anonymous.CatchAllController}
       */
      anyRequest().permitAll();

    http.oauth2ResourceServer(oauth2 ->
      oauth2.authenticationManagerResolver(tokenResolver) );    
    
    http.httpBasic().disable().
      /* api-svc is stateless and the browser client does not use cookies;
      in this article, section 2.1 and 2.2 are most applicable:
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

  @Bean
  public AuthenticationManagerResolver<HttpServletRequest> 
  tokenAuthenticationManagerResolver(
    RaidV1AuthenticationProvider raidV1AuthProvider,
    RaidV2AuthenticationProvider raidV2AuthProvider
  ) {
    return (request)-> {
      if( isRaidV2Api(request) ){
        return raidV2AuthProvider::authenticate;
      }
      else if( isRaidV1Api(request) ){
        return raidV1AuthProvider::authenticate;
      }
      else if( isStableApi(request) ){
        return raidV2AuthProvider::authenticate;
      }
      else {
        /* client has done a request (probably a POST), with a bearer token,
        but not on a recognised "API path". 
        IMPROVE: dig out the token and decode it, so we can log details? */
        log.info("ignored bearer token authenticated request %s:%s", 
          request.getMethod(), request.getRequestURI());
        throw ExceptionUtil.authFailed();
      }
    };
  }

  // maybe AuthnProvider can just be @Components now instead of explicit beans?
  @Bean
  public RaidV2AuthenticationProvider raidV2AuthProvider(
    RaidV2AppUserAuthService appUserAuthSvc,
    RaidV2ApiKeyAuthService apiKeyAuthSvc
  ){
    return new RaidV2AuthenticationProvider(
      appUserAuthSvc, apiKeyAuthSvc);
  }
  
  @Bean
  public RaidV1AuthenticationProvider raidV1AuthProvider(
    RaidV1AuthService raid1Svc
  ){
    return new RaidV1AuthenticationProvider(raid1Svc);
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

  public static boolean isRaidV1Api(HttpServletRequest request) {
    return request.getServletPath().startsWith(RAID_V1_API);
  }

  public static boolean isRaidV2Api(HttpServletRequest request) {
    return request.getServletPath().startsWith(RAID_V2_API);
  }

  public static boolean isStableApi(HttpServletRequest request) {
    return request.getServletPath().startsWith(RAID_STABLE_API);
  }

}