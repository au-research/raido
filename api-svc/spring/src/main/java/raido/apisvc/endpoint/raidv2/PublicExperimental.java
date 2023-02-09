package raido.apisvc.endpoint.raidv2;

import jakarta.servlet.http.HttpServletRequest;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.raid.MetadataService;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.spring.StartupListener;
import raido.apisvc.spring.bean.AppInfoBean;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.PublicReadRaidResponseV3;
import raido.idl.raidv2.model.PublicServicePoint;
import raido.idl.raidv2.model.VersionResult;

import java.util.List;

import static java.time.ZoneOffset.UTC;
import static java.util.List.of;
import static org.eclipse.jetty.http.HttpStatus.BAD_REQUEST_400;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V2_API;
import static raido.apisvc.spring.security.ApiSafeException.apiSafe;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.ExceptionUtil.ise;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.urlDecode;
import static raido.db.jooq.api_svc.enums.Metaschema.legacy_metadata_schema_v1;
import static raido.db.jooq.api_svc.enums.Metaschema.raido_metadata_schema_v1;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class PublicExperimental implements PublicExperimentalApi {
  public static final String HANDLE_V3_CATCHALL_PREFIX =
    RAID_V2_API + "/public/handle/v3" + "/";
  public static final String HANDLE_SEPERATOR = "/";
  private static final Log log = to(PublicExperimental.class);
  
  private DSLContext db;
  private AppInfoBean appInfo;
  private StartupListener startup;
  private RaidService raidSvc;
  private MetadataService metaSvc;

  public PublicExperimental(
    DSLContext db,
    AppInfoBean appInfo,
    StartupListener startup,
    RaidService raidSvc,
    MetadataService metaSvc
  ) {
    this.db = db;
    this.appInfo = appInfo;
    this.startup = startup;
    this.raidSvc = raidSvc;
    this.metaSvc = metaSvc;
  }

  /** Transactional=SUPPORTS because when testing this out in AWS and I had 
   bad DB config, found out this method was creating a TX.  Doesn't need to do
   that, so I added supports so that it would not create a TX if called at
   top level. */
  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public VersionResult version() {
    return new VersionResult().
      buildVersion(appInfo.getBuildVersion()).
      buildCommitId(appInfo.getBuildCommitId()).
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
  public PublicReadRaidResponseV3 publicReadRaidV3(String handle) {
    var data = raidSvc.readRaidV2Data(handle);
    Metaschema schema = data.raid().getMetadataSchema();
    
    if( schema == legacy_metadata_schema_v1 ){
      return metaSvc.mapLegacySchemaToPublic(data);
    }
    
    if( schema == raido_metadata_schema_v1 ){
      return metaSvc.mapRaidoV1SchemaToPublic(data);
    }

    var ex = ise("unknown raid schema");
    log.with("schema", schema).with("handle", handle).error(ex.getMessage());
    throw ex;
  }

  /**
   This method catches all prefixes with path prefix `/v3/raid` and attempts
   to parse the parameter manually, so that we can receive handles that are
   just formatted with simple slashes.
   The openapi spec is defined with the "{raidId}' path param because it makes
   it more clear to the reader/caller what the url is expected to look like.
   <p>
   IMPROVE: factor out parsing logic and write detailed/edge-case unit tests 
   */
  @RequestMapping(
    method = RequestMethod.GET,
    value = HANDLE_V3_CATCHALL_PREFIX + "**")
  public PublicReadRaidResponseV3 handleRaidV3CatchAll(
    HttpServletRequest req
  ) {
    String path = urlDecode(req.getServletPath().trim());
    log.with("path", req.getServletPath()).
      with("decodedPath", path).
      with("params", req.getParameterMap()).
      info("handleRaidV2CatchAllAsHtml() called");

    if( !path.startsWith(HANDLE_V3_CATCHALL_PREFIX) ){
      throw iae("unexpected path: %s", path);
    }

    String handle = path.substring(HANDLE_V3_CATCHALL_PREFIX.length());
    if( !handle.contains(HANDLE_SEPERATOR) ){
      throw apiSafe("handle did not contain a slash character",
        BAD_REQUEST_400, of(handle));
    }

    return publicReadRaidV3(handle);
  }

}
