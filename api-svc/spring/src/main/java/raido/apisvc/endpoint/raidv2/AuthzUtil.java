package raido.apisvc.endpoint.raidv2;

import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.service.auth.NonAuthzTokenPayload;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static raido.apisvc.endpoint.message.RaidApiMessage.DISALLOWED_X_SVC_CALL;
import static raido.apisvc.endpoint.message.RaidApiMessage.ONLY_OP_OR_SP_ADMIN;
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
  public static void guardOperatorOrAssociated(
    AuthzTokenPayload user,
    Long servicePointId
  ) {
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update requests for any service point
      return;
    }

    if( !areEqual(servicePointId, user.getServicePointId()) ){
      var iae = iae(DISALLOWED_X_SVC_CALL);
      log.with("user", user).with("servicePointId", servicePointId).
        error(iae.getMessage());
      throw iae;
    }
  }

  public static void guardOperatorOrAssociatedSpAdmin(
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
      var iae = iae(DISALLOWED_X_SVC_CALL);
      log.with("user", user).with("servicePointId", servicePointId).
        error(iae.getMessage());
      throw iae;
    }

    var iae = iae(ONLY_OP_OR_SP_ADMIN);
    log.with("user", user).error(iae.getMessage());
    throw iae;
  }

  public static void guardOperatorOrAssociatedSpUser(
    AuthzTokenPayload user,
    Long servicePointId
  ) {
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update any service point
      return;
    }

    if( areEqual(servicePointId, user.getServicePointId()) ){
      // sp_user and admin can list raids for their own SP
      return;
    }

    // not allowed to read raids from other SP's 
    var iae = iae(DISALLOWED_X_SVC_CALL);
    log.with("user", user).with("servicePointId", servicePointId).
      error(iae.getMessage());
    throw iae;
  }

  public static boolean isOperatorOrSpAdmin(
    AuthzTokenPayload user
  ) {
    return areEqual(user.getRole(), OPERATOR.getLiteral()) ||
      areEqual(user.getRole(), SP_ADMIN.getLiteral() );
  }
  
  public static void guardOperatorOrSpAdmin(
    AuthzTokenPayload user
  ) {
    if( isOperatorOrSpAdmin(user) ){
      return;
    }

    var iae = iae(ONLY_OP_OR_SP_ADMIN);
    log.with("user", user).error(iae.getMessage());
    throw iae;
  }
  
}
