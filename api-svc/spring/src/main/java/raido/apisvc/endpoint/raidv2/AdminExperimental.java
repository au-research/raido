package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.service.auth.request.AuthzRequestService;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.AuthRequestStatus;
import raido.idl.raidv2.api.AdminExperimentalApi;
import raido.idl.raidv2.model.AuthzRequest;
import raido.idl.raidv2.model.AuthzRequestStatus;
import raido.idl.raidv2.model.UpdateAuthzRequestStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
import static raido.db.jooq.api_svc.enums.UserRole.SP_USER;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;
import static raido.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class AdminExperimental implements AdminExperimentalApi {
  private static final Log log = to(AdminExperimental.class);
  
  private AuthzRequestService authzReqeustSvc;
  private DSLContext db;

  public AdminExperimental(AuthzRequestService authzReqeustSvc, DSLContext db) {
    this.authzReqeustSvc = authzReqeustSvc;
    this.db = db;
  }

  @Override
  public List<AuthzRequest> listAuthzRequest() {
    var user = AuthzUtil.getAuthzPayload();
    // this is the authz check, will be moved to a role annotation soon
    Guard.areEqual(user.getRole(), OPERATOR.getLiteral());

    return authzReqeustSvc.listAllAuthzRequest();
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

    if( req.getStatus() == AuthzRequestStatus.APPROVED ){
      Guard.isTrue(authzRecord.getStatus() == AuthRequestStatus.REQUESTED);
      authzRecord.setStatus(AuthRequestStatus.APPROVED);
      authzRecord.setRespondingUser(user.getAppUserId());
      authzRecord.setDateResponded(LocalDateTime.now());
      authzRecord.update();
      
      db.insertInto(APP_USER).
        set(APP_USER.SERVICE_POINT_ID, authzRecord.getServicePointId()).
        set(APP_USER.EMAIL, authzRecord.getEmail()).
        set(APP_USER.CLIENT_ID, authzRecord.getClientId()).
        set(APP_USER.SUBJECT, authzRecord.getSubject()).
        set(APP_USER.ID_PROVIDER, authzRecord.getIdProvider()).
        set(APP_USER.ROLE, SP_USER).
        onConflict(APP_USER.EMAIL, APP_USER.CLIENT_ID, APP_USER.SUBJECT).
        where(APP_USER.DISABLED.eq(false)).
        doUpdate().
        set(APP_USER.SERVICE_POINT_ID, authzRecord.getServicePointId()).
        execute();
    }
    else if( req.getStatus() == AuthzRequestStatus.REJECTED ){
      authzRecord.setStatus(AuthRequestStatus.REJECTED);
      authzRecord.setRespondingUser(user.getAppUserId());
      authzRecord.setDateResponded(LocalDateTime.now());
      authzRecord.update();
    }
    else {
      var iae = iae("invalid status update value");
      log.with("user", user).
        with("request", req).
        with("db.status", authzRecord.getStatus()).
        error(iae.getMessage());
      throw iae;
    }
    
    return null;
  }

  private static void guardAuthzRequestSecurity(
    AuthzTokenPayload user,
    Long servicePointId
  ) {
    if( areEqual(user.getRole(), OPERATOR.getLiteral()) ){
      // operator can update requests for any service point
    }
    else if( areEqual(user.getRole(), SP_ADMIN.getLiteral()) ){
      if( !areEqual(servicePointId, user.getServicePointId()) ){
        var iae = iae("disallowed cross-service point call");
        log.with("user", user).with("servicePointId", servicePointId).
          error(iae.getMessage());
        throw iae;
      }
    }
    else {
      var iae = iae("only operators or sp_admins can call this endpoint");
      log.with("user", user).error(iae.getMessage());
      // TODO:STO I think we should have specific authz failure
      // not for the client, just for differentiating internally (i.e. logging)
      throw iae;
    }
  }


}
