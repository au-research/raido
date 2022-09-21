package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.request.AuthzRequestService;
import raido.apisvc.util.Guard;
import raido.idl.raidv2.api.AdminExperimentalApi;
import raido.idl.raidv2.model.AuthzRequest;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class AdminExperimental implements AdminExperimentalApi {

  private AuthzRequestService authzReqeustSvc;

  public AdminExperimental(AuthzRequestService authzReqeustSvc) {
    this.authzReqeustSvc = authzReqeustSvc;
  }

  @Override
  public List<AuthzRequest> listAuthzRequest() {
    var user = AuthzUtil.getAuthzPayload();
    // this is the authz check, will be moved to a role annotation soon
    Guard.areEqual(user.getRole(), OPERATOR.getLiteral());

    return authzReqeustSvc.listAllAuthzRequest();
  }


}
