package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.admin.AuthzRequestService;
import raido.apisvc.spring.StartupListener;
import raido.apisvc.spring.bean.AppInfoBean;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.PublicServicePoint;
import raido.idl.raidv2.model.UpdateAuthzRequest;
import raido.idl.raidv2.model.UpdateAuthzResponse;
import raido.idl.raidv2.model.VersionResult;

import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getNonAuthzPayload;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class PublicExperimental implements PublicExperimentalApi {
  private static final Log log = to(PublicExperimental.class);
  
  private DSLContext db;
  private AppInfoBean appInfo;
  private StartupListener startup;
  private AuthzRequestService authzRequestSvc;

  public PublicExperimental(DSLContext db,
    AppInfoBean appInfo,
    StartupListener startup,
    AuthzRequestService authzRequestSvc
  ) {
    this.db = db;
    this.appInfo = appInfo;
    this.startup = startup;
    this.authzRequestSvc = authzRequestSvc;
  }

  @Override
  public VersionResult version() {
    return new VersionResult().
      buildVersion(appInfo.getBuildVersion()).
      buildCommitId(appInfo.getBuildVersion()).
      buildDate(appInfo.getBuildDate()).
      startDate(startup.getStartTime().atOffset(UTC));
 
  }

  @Override
  public List<PublicServicePoint> listPublicServicePoint() {
    return db.
      select(
        SERVICE_POINT.ID,
        SERVICE_POINT.NAME).
      from(SERVICE_POINT).
      where(SERVICE_POINT.DISABLED.isFalse()).
      fetchInto(PublicServicePoint.class);
  }

  @Override
  public UpdateAuthzResponse updateRequestAuthz(UpdateAuthzRequest req) {
    var user = getNonAuthzPayload();

    // TODO:STO validate the request data

    return authzRequestSvc.updateRequestAuthz(user, req);
  }

}
