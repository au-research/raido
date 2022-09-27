package raido.apisvc.endpoint.raidv2;

import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.service.auth.NonAuthzTokenPayload;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;

public class AuthzUtil {
  private static final Log log = to(AuthzUtil.class);
  
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

  /**
   Right now, "associated" means "directly associated", but 
   later might mean a more indirect association.
   */
  public static void isOperatorOrAssociated(
    AuthzTokenPayload user,
    Long servicePointId
  ) {
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update requests for any service point
      return;
    }

    if( !areEqual(servicePointId, user.getServicePointId()) ){
      var iae = iae("disallowed cross-service point call");
      log.with("user", user).with("servicePointId", servicePointId).
        error(iae.getMessage());
      throw iae;
    }
  }

  public static void isOperatorOrAssociatedSpAdmin(
    AuthzTokenPayload user,
    Long servicePointId
  ) {
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update any service point
      return;
    }

    if( areEqual(user.getRole(), SP_ADMIN.getLiteral()) ){
      if( areEqual(servicePointId, user.getServicePointId()) ){
        // admin can update their own service point
        return;
      }

      // SP_ADMIN is not allowed to do stuff to other SP's 
      var iae = iae("disallowed cross-service point call");
      log.with("user", user).with("servicePointId", servicePointId).
        error(iae.getMessage());
      throw iae;
    }

    var iae = iae("only operators or sp_admins can call this endpoint");
    log.with("user", user).error(iae.getMessage());
    throw iae;
  }

  public static void isOperatorOrSpAdmin(
    AuthzTokenPayload user
  ) {
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ||
      areEqual(user.getRole(), SP_ADMIN.getLiteral())
    ){
      // operator can update requests for any service point
      return;
    }

    var iae = iae("only operators or sp_admins can call this endpoint");
    log.with("user", user).error(iae.getMessage());
    throw iae;
  }
  
}
