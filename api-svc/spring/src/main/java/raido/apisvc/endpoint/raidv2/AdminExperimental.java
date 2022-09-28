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
import raido.idl.raidv2.api.AdminExperimentalApi;
import raido.idl.raidv2.model.AppUser;
import raido.idl.raidv2.model.AuthzRequest;
import raido.idl.raidv2.model.ServicePoint;
import raido.idl.raidv2.model.UpdateAuthzRequestStatus;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociatedSpAdmin;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrSpAdmin;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.ExceptionUtil.notYetImplemented;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
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
  public List<AuthzRequest> listAuthzRequest() {
    var user = AuthzUtil.getAuthzPayload();
    // this is the authz check, will be moved to a role annotation soon
    Guard.areEqual(user.getRole(), OPERATOR.getLiteral());

    return authzReqeustSvc.listAllRecentAuthzRequest();
  }

  @Override
  public AuthzRequest readRequestAuthz(Long authzRequestId) {
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

    return db.select().from(APP_USER).
      orderBy(APP_USER.EMAIL.asc()).
      limit(Constant.MAX_RETURN_RECORDS).
      fetchInto(AppUser.class);
  }


  @Override
  public AppUser readAppUser(Long appUserId) {
    var user = AuthzUtil.getAuthzPayload();
    guardOperatorOrSpAdmin(user);

    var appUser = db.select().from(APP_USER).
      where(APP_USER.ID.eq(appUserId)).
      fetchSingleInto(AppUser.class);

    guardOperatorOrAssociatedSpAdmin(user, appUser.getServicePointId());

    return appUser;
  }

  @Override
  public AppUser updateAppUser(AppUser appUser) {
    throw notYetImplemented();
  }

}
