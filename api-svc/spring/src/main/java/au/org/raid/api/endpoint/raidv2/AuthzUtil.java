package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.endpoint.message.RaidApiMessage;
import au.org.raid.api.exception.CrossAccountAccessException;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.api.util.ExceptionUtil;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import au.org.raid.api.util.ObjectUtil;
import au.org.raid.db.jooq.api_svc.enums.IdProvider;

import static au.org.raid.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static au.org.raid.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public class AuthzUtil {
  /* Hardcoded, we know this statically because we hardcoded the sequence to
   20M and raido is the first SP inserted via flyway. */
  public static final long RAIDO_SP_ID = 20_000_000;
  
  private static final Log log = Log.to(AuthzUtil.class);
  
  /** This will fail if the authentication is not a AuthzTokenPayload */
  public static ApiToken getApiToken() {
    var authentication = getContext().getAuthentication();
    if( authentication instanceof ApiToken apiToken){
      return apiToken;
    }
    
    log.with("authentication", authentication).
      error("user provided a bearer-token that is not an api-token");
    throw ExceptionUtil.authFailed();
  }

  /** User must be an admin associated specifically with the raido SP.
  Originally implemented so I could write the migration endpoint that can 
  insert raids across service points (i.e. migrating RDM and NotreDame raids,
  etc.) - without having to allow api-keys to have an "operator" role. */
  public static void guardRaidoAdminApiKey(ApiToken user){
    if( !ObjectUtil.areEqual(user.getClientId(), IdProvider.RAIDO_API.getLiteral()) ){
      /* IMPROVE: there really ought to be a better/more explicit way to tell 
      if a given user is an api-key or a real user. */
      var iae = ExceptionUtil.iae(RaidApiMessage.ONLY_RAIDO_ADMIN);
      log.with("user", user).with("reason", "not an api-key").
        error(iae.getMessage());
      throw iae;
    }
    
    if( !ObjectUtil.areEqual(user.getRole(), SP_ADMIN.getLiteral()) ){
      var iae = ExceptionUtil.iae(RaidApiMessage.ONLY_RAIDO_ADMIN);
      log.with("user", user).with("reason", "not admin role").
        error(iae.getMessage());
      throw iae;
    }
    
    if( !ObjectUtil.areEqual(user.getServicePointId(), RAIDO_SP_ID) ){
      var iae = ExceptionUtil.iae(RaidApiMessage.ONLY_RAIDO_ADMIN);
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
    if( ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update requests for any service point
      return;
    }

    if( !ObjectUtil.areEqual(servicePointId, user.getServicePointId()) ){
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
    if( ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update any service point
      return;
    }

    if( ObjectUtil.areEqual(user.getRole(), SP_ADMIN.getLiteral()) ){
      if( ObjectUtil.areEqual(servicePointId, user.getServicePointId()) ){
        // admin can update their own service point
        return;
      }

      // SP_ADMIN is not allowed to do stuff to other SP's 
      var iae = ExceptionUtil.iae(RaidApiMessage.DISALLOWED_X_SVC_CALL);
      log.with("user", user).with("servicePointId", servicePointId).
        error(iae.getMessage());
      throw iae;
    }

    var iae = ExceptionUtil.iae(RaidApiMessage.ONLY_OP_OR_SP_ADMIN);
    log.with("user", user).error(iae.getMessage());
    throw iae;
  }

  public static void guardOperatorOrAssociatedSpUser(
    ApiToken user,
    Long servicePointId
  ) {
    if( ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update any service point
      return;
    }

    if( ObjectUtil.areEqual(servicePointId, user.getServicePointId()) ){
      // sp_user and admin can list raids for their own SP
      return;
    }

    // not allowed to read raids from other SP's 
    var iae = ExceptionUtil.iae(RaidApiMessage.DISALLOWED_X_SVC_CALL);
    log.with("user", user).with("servicePointId", servicePointId).
      error(iae.getMessage());
    throw iae;
  }

  public static boolean isOperatorOrSpAdmin(
    ApiToken user
  ) {
    return ObjectUtil.areEqual(user.getRole(), OPERATOR.getLiteral()) ||
      ObjectUtil.areEqual(user.getRole(), SP_ADMIN.getLiteral() );
  }
  
  public static void guardOperatorOrSpAdmin(
    ApiToken user
  ) {
    if( isOperatorOrSpAdmin(user) ){
      return;
    }

    var iae = ExceptionUtil.iae(RaidApiMessage.ONLY_OP_OR_SP_ADMIN);
    log.with("user", user).error(iae.getMessage());
    throw iae;
  }
  
}
