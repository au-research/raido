package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.endpoint.Constant;
import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.service.auth.admin.AuthzRequestService;
import raido.apisvc.service.auth.admin.ServicePointService;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.AuthRequestStatus;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.idl.raidv2.api.AdminExperimentalApi;
import raido.idl.raidv2.model.AppUser;
import raido.idl.raidv2.model.AppUserExtraV1;
import raido.idl.raidv2.model.AuthzRequestExtraV1;
import raido.idl.raidv2.model.ServicePoint;
import raido.idl.raidv2.model.UpdateAuthzRequestStatus;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociatedSpAdmin;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrSpAdmin;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.isOperatorOrSpAdmin;
import static raido.apisvc.util.DateUtil.offset2Local;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
import static raido.db.jooq.api_svc.enums.UserRole.SP_USER;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static raido.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class AdminExperimental implements AdminExperimentalApi {
  private static final Log log = to(AdminExperimental.class);
  
  private AuthzRequestService authzReqeustSvc;
  private ServicePointService servicePointSvc;
  private DSLContext db;

  public AdminExperimental(
    AuthzRequestService authzReqeustSvc,
    ServicePointService servicePointSvc,
    DSLContext db
  ) {
    this.authzReqeustSvc = authzReqeustSvc;
    this.servicePointSvc = servicePointSvc;
    this.db = db;
  }

  @Override
  public List<AuthzRequestExtraV1> listAuthzRequest() {
    var user = AuthzUtil.getAuthzPayload();
    // this is the authz check, will be moved to a role annotation soon
    Guard.areEqual(user.getRole(), OPERATOR.getLiteral());

    return authzReqeustSvc.listAllRecentAuthzRequest();
  }

  @Override
  public AuthzRequestExtraV1 readRequestAuthz(Long authzRequestId) {
    // have to read it before we can see if user is allowed for servicePoint 
    var authRequest = authzReqeustSvc.readAuthzRequest(authzRequestId);
    var user = AuthzUtil.getAuthzPayload();
    guardAuthzRequestSecurity(user, authRequest.getServicePointId());
    return authRequest;
  }

  @Override
  public Void updateAuthzRequestStatus(UpdateAuthzRequestStatus req) {
    var user = AuthzUtil.getAuthzPayload();
    
    var authzRecord = db.fetchSingle(
      USER_AUTHZ_REQUEST, 
      USER_AUTHZ_REQUEST.ID.eq(req.getAuthzRequestId()) );

    guardAuthzRequestSecurity(user, authzRecord.getServicePointId());

    authzReqeustSvc.updateAuthzRequestStatus(user, req, authzRecord);

    return null;
  }

  private static void guardAuthzRequestSecurity(
    AuthzTokenPayload user,
    Long servicePointId
  ) {
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update requests for any service point
      return;
    }
    
    if( areEqual(user.getRole(), SP_ADMIN.getLiteral()) ){
      if( areEqual(servicePointId, user.getServicePointId()) ){
        // admin can update requests for their own service point
        return;
      }

      // SP_ADMIN is not allowed to touch authz requests from other SP's 
      var iae = iae("disallowed cross-service point call");
      log.with("user", user).with("servicePointId", servicePointId).
        error(iae.getMessage());
      throw iae;
    }
    
    var iae = iae("only operators or sp_admins can call this endpoint");
    log.with("user", user).error(iae.getMessage());
    // TODO:STO I think we should have specific authz failure
    // not for the client, just for differentiating internally (i.e. logging)
    throw iae;
  }


  @Override
  public List<ServicePoint> listServicePoint() {
    var user = AuthzUtil.getAuthzPayload();
    guardOperatorOrSpAdmin(user);
    
    return db.select().from(SERVICE_POINT).
      orderBy(SERVICE_POINT.NAME.asc()).
      limit(Constant.MAX_RETURN_RECORDS).
      fetchInto(ServicePoint.class);
  }

  /** IMPROVE: Currently gives a 500 error if not found, 404 might be better? */
  @Override
  public ServicePoint readServicePoint(Long servicePointId) {
    var user = AuthzUtil.getAuthzPayload();
    guardOperatorOrAssociated(user, servicePointId);
    
    return db.select().from(SERVICE_POINT).
      where(SERVICE_POINT.ID.eq(servicePointId)).
      fetchSingleInto(ServicePoint.class);
  }

  @Override
  public ServicePoint updateServicePoint(ServicePoint req) {
    var user = AuthzUtil.getAuthzPayload();
    guardOperatorOrAssociatedSpAdmin(user, req.getId());
    
    Guard.notNull(req);
    Guard.hasValue("must have a name", req.getName());
    Guard.notNull("must have adminEmail", req.getAdminEmail());
    Guard.notNull("must have techEmail", req.getTechEmail());
    Guard.notNull("must have a enabled flag", req.getEnabled());

    return servicePointSvc.updateServicePoint(req);
  }

  @Override
  public List<AppUser> listAppUser(Long servicePointId) {
    var user = AuthzUtil.getAuthzPayload();
    guardOperatorOrAssociatedSpAdmin(user, servicePointId);

    return db.select().
      from(APP_USER).
      where(APP_USER.SERVICE_POINT_ID.eq(servicePointId)).
      orderBy(APP_USER.EMAIL.asc()).
      limit(Constant.MAX_RETURN_RECORDS).
      fetchInto(AppUser.class);
  }

  @Override
  public AppUser readAppUser(Long appUserId) {
    var user = AuthzUtil.getAuthzPayload();
    if( areEqual(user.getAppUserId(), appUserId) ){
      // user is allowed to read their own record
    }
    else if( isOperatorOrSpAdmin(user) ){
      /* operators or spAdmin can read info about any user in any service 
      point, spAdmin might be looking at a user that was approved onto a 
      different service point. */
    }
    else {
      var iae = iae("user read not allowed");
      log.with("user", user).with("appUserId", appUserId).
        error(iae.getMessage());
      throw iae;
    }
    
    var appUser = db.select().from(APP_USER).
      where(APP_USER.ID.eq(appUserId)).
      fetchSingleInto(AppUser.class);

    return appUser;
  }

  @Override
  public AppUserExtraV1 readAppUserExtra(Long appUserId) {
    var user = AuthzUtil.getAuthzPayload();
    guardOperatorOrSpAdmin(user);

    var appUser = readAppUser(appUserId);
    var servicePoint = readServicePoint(appUser.getServicePointId());
    
    var authzRequest = authzReqeustSvc.readAuthzRequestForUser(appUser);
    
    // bootstrapped user has no authzRequest, was auto-approved
    if( authzRequest.isEmpty() ){
      return new AppUserExtraV1().
        appUser(appUser).
        servicePoint(servicePoint);
    }

    log.with("authzRequest", authzRequest).debug("authzReqeust");
    return new AppUserExtraV1().
      appUser(appUser).
      servicePoint(servicePoint).
      authzRequest(authzRequest.get());
    
  }

  @Override
  public AppUser updateAppUser(AppUser req) {
    var invokingUser = AuthzUtil.getAuthzPayload();

    var targetUser = db.fetchSingle(
      APP_USER, APP_USER.ID.eq(req.getId()) );
    
    // spAdmin can only edit users in their associated SP 
    guardOperatorOrAssociatedSpAdmin(
      invokingUser, targetUser.getServicePointId());

    // the role of the person doing the action
    UserRole invokingRole = mapRestRole2Jq(invokingUser.getRole());
    // the current role of the user being updated from
    UserRole currentRole = targetUser.getRole();
    // the new role of the user being update to 
    UserRole targetRole = mapRestRole2Jq(req.getRole());
    
    if( currentRole == OPERATOR && targetRole != OPERATOR ){
      if( invokingRole != OPERATOR ){
        var iae = iae("only an OPERATOR can demote an OPERATOR");
        log.with("invokingUser", invokingUser).
          with("targetUserId", req.getId()).
          with("targetUserEmail", req.getEmail()).
          with("targetRole", targetRole.getLiteral()).
          with("currentRole", currentRole.getLiteral()).
          error(iae.getMessage());
        throw iae;
      }
    }
    else if( currentRole != OPERATOR && targetRole == OPERATOR ){
      if( invokingRole != OPERATOR ){
        var iae = iae("only an OPERATOR can promote an OPERATOR");
        log.with("invokingUser", invokingUser).
          with("targetUserId", req.getId()).
          with("targetUserEmail", req.getEmail()).
          with("targetRole", targetRole.getLiteral()).
          with("currentRole", currentRole.getLiteral()).
          error(iae.getMessage());
        throw iae;
      }
    }
    
    /* at this point, we've check that the invokingUser is OP or ADMIN, and 
     that they're for the associated SP if ADMIN, and that only an OP is 
     dealing with OP stuff.  
     Should be good to just set the role. */
    targetUser.setRole(targetRole);

    targetUser.setEnabled(req.getEnabled());
    targetUser.setTokenCutoff(offset2Local(req.getTokenCutoff()));

    targetUser.update();
   
    return readAppUser(targetUser.getId());
  }

  private UserRole mapRestRole2Jq(String role) {
    if( areEqual(OPERATOR.getLiteral(), role) ){
      return OPERATOR;
    }
    else if( areEqual(SP_ADMIN.getLiteral(), role) ){
      return SP_ADMIN;
    }
    else if( areEqual(SP_USER.getLiteral(), role) ){
      return SP_USER;
    }
    else {
      throw iae("could not map role: %s", role);
    }
  }

}
