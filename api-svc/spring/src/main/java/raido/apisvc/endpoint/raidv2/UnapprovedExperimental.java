package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.admin.AuthzRequestService;
import raido.apisvc.spring.security.raidv2.ApiToken;
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
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;

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

  @Override
  public UpdateAuthzResponse updateAuthzRequest(UpdateAuthzRequest req) {
    Guard.notNull(req);
    Guard.notNull(req.getServicePointId());
    
    var apiToken = getContext().getAuthentication();

    if( apiToken == null ){
      log.error("SecurityContext is empty)");
      throw authFailed();
    }
    
    if( apiToken instanceof ApiToken approvedUserToken ){
      /* Going to allow OPERATOR to do this to make int-testing easier. 
      Alternative is to have a whole different sign-in mechanism that we know 
      how to use in an automated fashion, but I don't know how to do that, yet.
      A proper automated mechanism is the right idea long-term, this is a 
      short-term workaround. */
      Guard.areEqual(approvedUserToken.getRole(), OPERATOR.getLiteral());
      return authzRequestSvc.updateRequestAuthz(
        approvedUserToken.getClientId(),
        approvedUserToken.getEmail(),
        approvedUserToken.getSubject(),
        req);
    }
    else if( apiToken instanceof UnapprovedUserApiToken unapprovedUserToken ){
      return authzRequestSvc.updateRequestAuthz(
        unapprovedUserToken.getClientId(),
        unapprovedUserToken.getEmail(),
        unapprovedUserToken.getSubject(),
        req);
    }

    log.with("tokenType", apiToken.getClass().getName()).
      error("authentication type is not an api-token)");
    throw authFailed();
  }

}
