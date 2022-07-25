package raido.spring.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import raido.util.Log;

import static raido.util.Log.to;

public class BearerSecurityContextRepository implements SecurityContextRepository {
  private final static Log log = to(BearerSecurityContextRepository.class);

  @Override
  public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
    SecurityContext context = SecurityContextHolder.createEmptyContext();
    String token = tokenFromRequest(requestResponseHolder.getRequest());
//    Authentication authentication = PreAuthenticatedAuthenticationJsonWebToken.usingToken(token);
//    if (authentication != null) {
//      context.setAuthentication(authentication);
//      logger.debug("Found bearer token in request. Saving it in SecurityContext");
//    }
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
    return tokenFromRequest(request) != null;
  }

  private String tokenFromRequest(HttpServletRequest request) {
    final String value = request.getHeader("Authorization");

    if (value == null || !value.toLowerCase().startsWith("bearer")) {
      return null;
    }

    String[] parts = value.split(" ");

    if (parts.length < 2) {
      return null;
    }

    return parts[1].trim();
  }
}
