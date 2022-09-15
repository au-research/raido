package raido.apisvc.endpoint.raidv2;

import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.service.auth.NonAuthzTokenPayload;
import raido.apisvc.util.Guard;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public class AuthzUtil {

  /**
   For testing, c.f. declaring a method parameter of type `Principal` 
   (I can't figure out how to get openapi to generate code like that).
   */
  public static AuthzTokenPayload getAuthzPayload() {
    return Guard.isInstance(
      AuthzTokenPayload.class,
      getContext().getAuthentication());
  }

  public static NonAuthzTokenPayload getNonAuthzPayload() {
    return Guard.isInstance(
      NonAuthzTokenPayload.class,
      getContext().getAuthentication());
  }


}
