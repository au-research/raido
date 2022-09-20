package raido.apisvc.endpoint.raidv2;

import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.service.auth.NonAuthzTokenPayload;
import raido.apisvc.util.Guard;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public class AuthzUtil {

  /** This will fail if the authentication is not a AuthzTokenPayload */
  public static AuthzTokenPayload getAuthzPayload() {
    return Guard.isInstance(
      AuthzTokenPayload.class,
      getContext().getAuthentication());
  }

  /** This will fail if the authentication is not a NonAuthzTokenPayload */
  public static NonAuthzTokenPayload getNonAuthzPayload() {
    return Guard.isInstance(
      NonAuthzTokenPayload.class,
      getContext().getAuthentication());
  }
}
