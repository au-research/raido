package raido.apisvc.endpoint.raidv2.pub;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.spring.StartupListener;
import raido.apisvc.spring.bean.AppInfoBean;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.VersionResult;

import static java.time.ZoneOffset.UTC;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(proxyMode = TARGET_CLASS)
@RestController
public class PublicExperimental implements PublicExperimentalApi {
  
  private DSLContext db;
  private AppInfoBean appInfo;
  private StartupListener startup;

  public PublicExperimental(DSLContext db,
    AppInfoBean appInfo,
    StartupListener startup
  ) {
    this.db = db;
    this.appInfo = appInfo;
    this.startup = startup;
  }

  @Override
  public VersionResult version() {
    return new VersionResult().
      buildVersion(appInfo.getBuildVersion()).
      buildCommitId(appInfo.getBuildVersion()).
      buildDate(appInfo.getBuildDate()).
      startDate(startup.getStartTime().atOffset(UTC));
  }
}
