package raido.spring.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import raido.spring.security.raidv1.Raid1PreAuthenticatedJsonWebToken;
import raido.util.Log;

import java.util.Optional;
import java.util.function.Supplier;

import static raido.spring.config.RaidV1SecurityConfig.RAID_V1_API;
import static raido.util.Log.to;

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
  public Supplier<SecurityContext> loadContext(HttpServletRequest request) {
    return ()->{
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      var token = authTokenFromRequest(request);
      if( token.isEmpty() ){
        return context;
      }

      if( request.getServletPath().startsWith(RAID_V1_API) ){
        log.with("path", request.getServletPath()).with("token", token).info(
          "RaidV1SCR yes");

        Authentication authentication =
          Raid1PreAuthenticatedJsonWebToken.usingToken(token.get());
        if( authentication != null ){
          context.setAuthentication(authentication);
        }
        else {
          log.warn("v1 path with token, pre-auth JWT returned null");
        }

      }
      else {
        log.with("path", request.getServletPath()).with("token", token).info(
          "RaidV1SCR no");
        throw new UnsupportedOperationException(
          "can only do /v1 endpoints ATM");
      }
      return context;

    };
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
