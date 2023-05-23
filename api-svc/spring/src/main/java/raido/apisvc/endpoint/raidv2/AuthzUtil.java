package raido.apisvc.endpoint.raidv2;

import raido.apisvc.exception.CrossAccountAccessException;
import raido.apisvc.spring.security.raidv2.ApiToken;
import raido.apisvc.spring.security.raidv2.UnapprovedUserApiToken;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.IdProvider;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static raido.apisvc.endpoint.message.RaidApiMessage.DISALLOWED_X_SVC_CALL;
import static raido.apisvc.endpoint.message.RaidApiMessage.ONLY_OP_OR_SP_ADMIN;
import static raido.apisvc.endpoint.message.RaidApiMessage.ONLY_RAIDO_ADMIN;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;

public class AuthzUtil {
  /* Hardcoded, we know this statically because we hardcoded the sequence to
   20M and raido is the first SP inserted via flyway. */
  public static final long RAIDO_SP_ID = 20_000_000;
  
  private static final Log log = to(AuthzUtil.class);
  
  /** This will fail if the authentication is not a AuthzTokenPayload */
  public static ApiToken getApiToken() {
    return Guard.isInstance(
      ApiToken.class,
      getContext().getAuthentication());
  }

  /** This will fail if the authentication is not a NonAuthzTokenPayload */
  public static UnapprovedUserApiToken getNonAuthzPayload() {
    return Guard.isInstance(
      UnapprovedUserApiToken.class,
      getContext().getAuthentication());
  }

  /** User must be an admin associated specifically with the raido SP.
  Originially implemented so I could write the migration endpoint that can 
  insert raids across service points (i.e. migrating RDM and NotreDame raids,
  etc.) - without having to allow api-keys to have an "operator" role. */
  public static void guardRaidoAdminApiKey(ApiToken user){
    if( !areEqual(user.getClientId(), IdProvider.RAIDO_API.getLiteral()) ){
      /* IMPROVE: there really ought to be a better/more explicit way to tell 
      if a given user is an api-key or a real user. */
      var iae = iae(ONLY_RAIDO_ADMIN);
      log.with("user", user).with("reason", "not an api-key").
        error(iae.getMessage());
      throw iae;
    }
    
    if( !areEqual(user.getRole(), SP_ADMIN.getLiteral()) ){
      var iae = iae(ONLY_RAIDO_ADMIN);
      log.with("user", user).with("reason", "not admin role").
        error(iae.getMessage());
      throw iae;
    }
    
    if( !areEqual(user.getServicePointId(), RAIDO_SP_ID) ){
      var iae = iae(ONLY_RAIDO_ADMIN);
      log.with("user", user).with("reason", "not raido SP").
        error(iae.getMessage());
      throw iae;
    }
  }
  
  /**
   User must be an OP, or must be "associated" to the SP passed.
   Right now, "associated" means "directly associated", but 
   later might mean a more indirect association.
   */
  public static void guardOperatorOrAssociated(
    ApiToken user,
    Long servicePointId
  ) {
    Guard.notNull("user must be set", user);
    Guard.isPositive("servicePointId must be set", servicePointId);
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update requests for any service point
      return;
    }

    if( !areEqual(servicePointId, user.getServicePointId()) ){
      var exception = new CrossAccountAccessException(servicePointId);
      log.with("user", user).with("servicePointId", servicePointId).
        error(exception.getMessage());
      throw exception;
    }
  }

  public static void guardOperatorOrAssociatedSpAdmin(
    ApiToken user,
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
    ApiToken user,
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
    ApiToken user
  ) {
    return areEqual(user.getRole(), OPERATOR.getLiteral()) ||
      areEqual(user.getRole(), SP_ADMIN.getLiteral() );
  }
  
  public static void guardOperatorOrSpAdmin(
    ApiToken user
  ) {
    if( isOperatorOrSpAdmin(user) ){
      return;
    }

    var iae = iae(ONLY_OP_OR_SP_ADMIN);
    log.with("user", user).error(iae.getMessage());
    throw iae;
  }
  
}
