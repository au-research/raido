package raido.apisvc.spring.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.DeferredSecurityContext;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import raido.apisvc.spring.security.raidv1.RaidV1PreAuthenticatedJsonWebToken;
import raido.apisvc.spring.security.raidv2.RaidV2PreAuthenticatedJsonWebToken;
import raido.apisvc.util.ExceptionUtil;
import raido.apisvc.util.Log;

import java.util.Optional;

import static org.springframework.security.core.context.SecurityContextHolder.createEmptyContext;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V2_API;
import static raido.apisvc.spring.security.raidv1.RaidV1PreAuthenticatedJsonWebToken.decodeRaidV1Token;
import static raido.apisvc.spring.security.raidv2.RaidV2PreAuthenticatedJsonWebToken.decodeRaidV2Token;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.mask;

/**
 Understands all different kinds of security context:
 - raidv1 (from token table)
 - raidV2 authz token
 - other basic or pre-shared, etc.
 <p>
 Instead of one SCR that knows all types, could try mutliple SecurityConfigs,
 but it looks like a bit of a hassle to implment:
 https://github.com/spring-projects/spring-security/issues/5593
 */
public class RaidoSecurityContextRepository implements SecurityContextRepository {
  private final static Log log = to(RaidoSecurityContextRepository.class);

  @SuppressWarnings("deprecation")  // not sure how to implement without this 
  @Override
  public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
    throw new UnsupportedOperationException();
  }

  @Override
  public DeferredSecurityContext loadDeferredContext(HttpServletRequest request) {
    return new DeferredSecurityContext() {
      @Override
      public boolean isGenerated() {
        /* Introduced when going from spring-security 6.0.0-M6 to 6.0.0.
         Petty sure this is right, but could use more digging into what the 
         effect of isGenerated() is on the security flow. */
        return true;
      }

      @Override
      public SecurityContext get() {
        var token = authTokenFromRequest(request);
        if( token.isEmpty() ){
          return createEmptyContext();
        }

        if( isRaidV2Api(request) ){
          var authentication = decodeRaidV2Token(token.get());
          if( authentication == null ){
            return createEmptyContext();
          }
          return createRaidV2AuthContext(authentication);
        }

        if( isRaidV1Api(request) ){
          var authentication = decodeRaidV1Token(token.get());
          if( authentication == null ){
            return createEmptyContext();
          }
          return createRaidV1AuthContext(authentication);
        }

        log.with("path", request.getServletPath()).
          with("token", mask(token.get())).
          info("SCR no match");
        throw ExceptionUtil.authFailed();      
      }
    };
  }

  private static boolean isRaidV1Api(HttpServletRequest request) {
    return request.getServletPath().startsWith(RAID_V1_API);
  }

  private static boolean isRaidV2Api(HttpServletRequest request) {
    return request.getServletPath().startsWith(RAID_V2_API);
  }

  private static SecurityContext createRaidV1AuthContext(
    RaidV1PreAuthenticatedJsonWebToken authentication
  ) {
    var context = createEmptyContext();
    context.setAuthentication(authentication);
    return context;
  }

  private static SecurityContext createRaidV2AuthContext(
    RaidV2PreAuthenticatedJsonWebToken authentication
  ) {
    var context = createEmptyContext();
    context.setAuthentication(authentication);
    return context;
  }

  @Override
  public void saveContext(
    SecurityContext context,
    HttpServletRequest request,
    HttpServletResponse response
  ) {
  }

  @Override
  public boolean containsContext(HttpServletRequest request) {
    return authTokenFromRequest(request).isPresent();
  }


  private Optional<String> authTokenFromRequest(HttpServletRequest request) {
    final String value = request.getHeader("Authorization");

    if( value == null || !value.toLowerCase().startsWith("bearer") ){
      return Optional.empty();
    }

    String[] parts = value.split(" ");

    if( parts.length < 2 ){
      return Optional.empty();
    }

    return Optional.of(parts[1].trim());
  }
}
