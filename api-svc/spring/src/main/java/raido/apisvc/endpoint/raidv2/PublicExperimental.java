package raido.apisvc.endpoint.raidv2;

import jakarta.servlet.http.HttpServletRequest;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.spring.StartupListener;
import raido.apisvc.spring.bean.AppInfoBean;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.PublicReadRaidResponseV1;
import raido.idl.raidv2.model.PublicServicePoint;
import raido.idl.raidv2.model.VersionResult;

import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.util.List.of;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V2_API;
import static raido.apisvc.spring.security.ApiSafeException.apiSafe;
import static raido.apisvc.util.DateUtil.local2Offset;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.urlDecode;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class PublicExperimental implements PublicExperimentalApi {
  public static final String HANDLE_URL_PREFIX = "/public/handle/v1";
  public static final String HANDLE_CATCHALL_PREFIX =
    RAID_V2_API + HANDLE_URL_PREFIX + "/";
  public static final String HANDLE_SEPERATOR = "/";
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

    if( data.raid().getConfidential() ){
      return new PublicReadRaidResponseV1().
        handle(data.raid().getHandle()).
        createDate(local2Offset(data.raid().getDateCreated())).
        url(data.raid().getContentPath()).
        confidential(data.raid().getConfidential());
    }
    
    return new PublicReadRaidResponseV1().
      handle(data.raid().getHandle()).
      servicePointId(data.servicePoint().getId()).
      servicePointName(data.servicePoint().getName()).
      name(data.raid().getName()).
      startDate(local2Offset(data.raid().getStartDate())).
      createDate(local2Offset(data.raid().getDateCreated())).
      url(data.raid().getContentPath()).
      metadataEnvelopeSchema("unknown").
      metadata(data.raid().getMetadata().data()).
      confidential(data.raid().getConfidential());
  }

  /**
   This method catches all prefixes with path prefix `/v2/raid` and attempts
   to parse the parameter manually, so that we can receive handles that are
   just formatted with simple slashes.
   The openapi spec is defined with the "{raidId}' path param because it makes
   it more clear to the caller what the url is expected to look like.
   <p>
   IMPROVE: should write some detailed/edge-case unit tests for this
   path parsing logic.
   */
  @RequestMapping(
    method = RequestMethod.GET,
    value = HANDLE_CATCHALL_PREFIX + "**",
    produces = {"application/json"}
  )
  public PublicReadRaidResponseV1 handleRaidCatchAll(
    HttpServletRequest req
  ) {
    String path = urlDecode(req.getServletPath().trim());
    log.with("path", req.getServletPath()).
      with("decodedPath", path).
      with("params", req.getParameterMap()).
      info("handleRaidCatchAll() called");

    if( !path.startsWith(HANDLE_CATCHALL_PREFIX) ){
      throw iae("unexpected path: %s", path);
    }

    String handle = path.substring(HANDLE_CATCHALL_PREFIX.length());
    if( !handle.contains(HANDLE_SEPERATOR) ){
      throw apiSafe("handle did not contain a slash character",
        BAD_REQUEST_400, of(handle));
    }

    return publicReadRaid(handle);
  }
}
