package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.RaidService;
import raido.apisvc.spring.StartupListener;
import raido.apisvc.spring.bean.AppInfoBean;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.PublicReadRaidResponseV1;
import raido.idl.raidv2.model.PublicServicePoint;
import raido.idl.raidv2.model.VersionResult;

import java.util.List;

import static java.time.ZoneOffset.UTC;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.util.DateUtil.local2Offset;
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
  private RaidService raidSvc;

  public PublicExperimental(
    DSLContext db,
    AppInfoBean appInfo,
    StartupListener startup,
    RaidService raidSvc
  ) {
    this.db = db;
    this.appInfo = appInfo;
    this.startup = startup;
    this.raidSvc = raidSvc;
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
  public List<PublicServicePoint> publicListServicePoint() {
    return db.
      select(
        SERVICE_POINT.ID,
        SERVICE_POINT.NAME).
      from(SERVICE_POINT).
      where(SERVICE_POINT.ENABLED.isTrue()).
      fetchInto(PublicServicePoint.class);
  }

  @Override
  public PublicReadRaidResponseV1 publicReadRaid(String handle) {
    
    var data = raidSvc.readRaidData(handle);
    
    // improve:sto - deal with confidential and embargoed raids
    // if isNotPublic return handle, url, createDate

    return new PublicReadRaidResponseV1().
      handle(data.raid().getHandle()).
      servicePointId(data.servicePoint().getId()).
      servicePointName(data.servicePoint().getName()).
      name(data.raid().getName()).
      startDate(local2Offset(data.raid().getStartDate())).
      createDate(local2Offset(data.raid().getDateCreated())).
      url(data.raid().getContentPath()).
      metadataEnvelopeSchema("unknown").
      metadata(data.raid().getMetadata().data());
  }


}
