package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.service.raid.MetadataService;
import au.org.raid.api.service.raid.RaidService;
import au.org.raid.api.spring.bean.AppInfoBean;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.api.PublicExperimentalApi;
import au.org.raid.idl.raidv2.model.PublicReadRaidResponseV3;
import au.org.raid.idl.raidv2.model.PublicServicePoint;
import au.org.raid.idl.raidv2.model.VersionResult;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletRequest;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static au.org.raid.api.spring.config.RaidWebSecurityConfig.RAID_V2_API;
import static au.org.raid.api.spring.security.ApiSafeException.apiSafe;
import static au.org.raid.api.util.ExceptionUtil.iae;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.RestUtil.urlDecode;
import static au.org.raid.db.jooq.tables.ServicePoint.SERVICE_POINT;
import static java.util.List.of;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
@CrossOrigin
public class PublicExperimental implements PublicExperimentalApi {
    public static final String HANDLE_V3_CATCHALL_PREFIX =
            RAID_V2_API + "/public/handle/v3" + "/";
    public static final String HANDLE_SEPARATOR = "/";
    private DSLContext db;
    private AppInfoBean appInfo;
    private RaidService raidSvc;
    private MetadataService metaSvc;

    public PublicExperimental(
            DSLContext db,
            AppInfoBean appInfo,
            RaidService raidSvc,
            MetadataService metaSvc
    ) {
        this.db = db;
        this.appInfo = appInfo;
        this.raidSvc = raidSvc;
        this.metaSvc = metaSvc;
    }

    /**
     * Transactional=SUPPORTS because when testing this out in AWS and I had
     * bad DB config, found out this method was creating a TX.  Doesn't need to do
     * that, so I added supports so that it would not create a TX if called at
     * top level.
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public ResponseEntity<VersionResult> version() {
        return ResponseEntity.ok(new VersionResult().
                buildVersion(appInfo.getBuildVersion()).
                buildCommitId(appInfo.getBuildCommitId()).
                buildDate(appInfo.getBuildDate()));
    }

    @Override
    public ResponseEntity<List<PublicServicePoint>> publicListServicePoint() {
        return ResponseEntity.ok(db.
                select(
                        SERVICE_POINT.ID,
                        SERVICE_POINT.NAME).
                from(SERVICE_POINT).
                where(SERVICE_POINT.ENABLED.isTrue()).
                fetchInto(PublicServicePoint.class));
    }

    @Override
    public ResponseEntity<PublicReadRaidResponseV3> publicReadRaidV3(String handle) {
        var data = raidSvc.readRaidV2Data(handle);
        return ResponseEntity.ok(metaSvc.mapPublicReadResponse(data));
    }

    /**
     * This method catches all prefixes with path prefix `/v3/raid` and attempts
     * to parse the parameter manually, so that we can receive handles that are
     * just formatted with simple slashes.
     * The openapi spec is defined with the "{raidId}' path param because it makes
     * it more clear to the reader/caller what the url is expected to look like.
     * <p>
     * IMPROVE: factor out parsing logic and write detailed/edge-case unit tests
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = HANDLE_V3_CATCHALL_PREFIX + "**")
    public PublicReadRaidResponseV3 handleRaidV3CatchAll(
            HttpServletRequest req
    ) {
        String path = urlDecode(req.getServletPath().trim());

        if (!path.startsWith(HANDLE_V3_CATCHALL_PREFIX)) {
            throw iae("unexpected path: %s", path);
        }

        String handle = path.substring(HANDLE_V3_CATCHALL_PREFIX.length());
        if (!handle.contains(HANDLE_SEPARATOR)) {
            throw apiSafe("handle did not contain a slash character",
                    400, of(handle));
        }

        return publicReadRaidV3(handle).getBody();
    }

}
