package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.admin.AuthzRequestService;
import raido.apisvc.spring.security.raidv2.UnapprovedUserApiToken;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.UnapprovedExperimentalApi;
import raido.idl.raidv2.model.UpdateAuthzRequest;
import raido.idl.raidv2.model.UpdateAuthzResponse;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static raido.apisvc.util.ExceptionUtil.authFailed;
import static raido.apisvc.util.Log.to;

/**
 "Unapproved" in that this class is about dealing with users who have been 
 authenticated by an IdProvider (google, etc.) but have yet been approved to
 use the system for a particular service-point.
 */
@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class UnapprovedExperimental implements UnapprovedExperimentalApi {
  private static final Log log = to(UnapprovedExperimental.class);
  
  private AuthzRequestService authzRequestSvc;

  public UnapprovedExperimental(
    AuthzRequestService authzRequestSvc
  ) {
    this.authzRequestSvc = authzRequestSvc;
  }

  /* inline authz code because there's only on endpoint, if you end up creating
  more "unapproved user" endpoints (what for?) factor out the SecurityContext 
  code to AuthzUtil or somewhere. */
  @Override
  public UpdateAuthzResponse updateAuthzRequest(UpdateAuthzRequest req) {
    Guard.notNull(req);
    Guard.notNull(req.getServicePointId());
    Guard.notNull("comments may be empty, but not null", req.getComments());

    var apiToken = getContext().getAuthentication();

    if( apiToken == null ){
      log.error("SecurityContext is empty)");
      throw authFailed();
    }

    if( !(apiToken instanceof UnapprovedUserApiToken unapprovedUserToken) ){
      log.with("tokenType", apiToken.getClass().getName()).
        // this is a decoded structure, no signature - ok to log it
        with("apiToken", apiToken).
        error("authentication type is not an unapproved user api-token)");
      throw authFailed();
    }
    
    return authzRequestSvc.updateRequestAuthz(
      unapprovedUserToken.getClientId(),
      unapprovedUserToken.getEmail(),
      unapprovedUserToken.getSubject(),
      req);
  }

}
