package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.admin.AuthzRequestService;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.UnauthzExperimentalApi;
import raido.idl.raidv2.model.UpdateAuthzRequest;
import raido.idl.raidv2.model.UpdateAuthzResponse;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getNonAuthzPayload;
import static raido.apisvc.util.Log.to;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class UnauthzExperimental implements UnauthzExperimentalApi {
  private static final Log log = to(UnauthzExperimental.class);
  
  private DSLContext db;
  private AuthzRequestService authzRequestSvc;

  public UnauthzExperimental(DSLContext db,
    AuthzRequestService authzRequestSvc
  ) {
    this.db = db;
    this.authzRequestSvc = authzRequestSvc;
  }

  @Override
  public UpdateAuthzResponse updateRequestAuthz(UpdateAuthzRequest req) {
    var user = getNonAuthzPayload();

    // TODO:STO validate the request data

    return authzRequestSvc.updateRequestAuthz(user, req);
  }

}
