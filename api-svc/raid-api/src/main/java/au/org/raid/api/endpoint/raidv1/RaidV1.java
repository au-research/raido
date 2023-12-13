package au.org.raid.api.endpoint.raidv1;

import au.org.raid.api.service.raid.MetadataService;
import au.org.raid.api.service.raid.RaidService;
import au.org.raid.api.service.raid.RaidoSchemaV1Util;
import au.org.raid.api.service.raid.ValidationFailureException;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.config.RaidWebSecurityConfig;
import au.org.raid.api.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv1.api.RaidV1Api;
import au.org.raid.idl.raidv1.model.RaidCreateModel;
import au.org.raid.idl.raidv1.model.RaidModel;
import au.org.raid.idl.raidv1.model.RaidModelMeta;
import au.org.raid.idl.raidv1.model.RaidPublicModel;
import au.org.raid.idl.raidv2.model.*;
import jakarta.servlet.http.HttpServletRequest;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.RaidApiMessage.*;
import static au.org.raid.api.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static au.org.raid.api.spring.security.ApiSafeException.apiSafe;
import static au.org.raid.api.util.DateUtil.*;
import static au.org.raid.api.util.ExceptionUtil.iae;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.isTrue;
import static au.org.raid.api.util.RestUtil.urlDecode;
import static au.org.raid.api.util.StringUtil.hasValue;
import static au.org.raid.api.util.StringUtil.isBlank;
import static au.org.raid.db.jooq.tables.Raid.RAID;
import static au.org.raid.idl.raidv2.model.AccessType.CLOSED;
import static au.org.raid.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static au.org.raid.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static org.springframework.transaction.annotation.Propagation.NEVER;

/* without the proxy mode setting, Spring doesn't see the RequestMappings from 
the interface and so won't define our RaidV1 endpoints */
@Scope(proxyMode = TARGET_CLASS)
@RequestMapping(RAID_V1_API)
@RestController
public class RaidV1 implements RaidV1Api {
    public static final String HANDLE_URL_PREFIX = "/handle";
    public static final String HANDLE_CATCHALL_PREFIX =
            RAID_V1_API + HANDLE_URL_PREFIX + "/";
    public static final String HANDLE_SEPARATOR = "/";

    public static final String V1_RAID_POST_URL = "/v1/RAiD";

    private DSLContext db;
    private RaidService raidSvc;
    private MetadataService metaSvc;

    public RaidV1(
            DSLContext db,
            RaidService raidSvc,
            MetadataService metaSvc
    ) {
        this.db = db;
        this.raidSvc = raidSvc;
        this.metaSvc = metaSvc;
    }

    private static LegacyMetadataSchemaV1 mapLegacyApiModelToLegacySchema(
            RaidCreateModel req,
            LocalDate startDate
    ) {
        var metadataToMint = new LegacyMetadataSchemaV1().
                metadataSchema(RaidoMetaschema.LEGACYMETADATASCHEMAV1).
                titles(List.of(new TitleBlock().
                        type(PRIMARY_TITLE).
                        title(req.getMeta().getName()).
                        startDate(startDate))).
                dates(new DatesBlock().startDate(startDate)).
                descriptions(List.of(new DescriptionBlock().
                        type(PRIMARY_DESCRIPTION).
                        description(req.getMeta().getDescription()))).
                access(new AccessBlock().
                        type(CLOSED).
                        accessStatement(RAID_V1_ACCESS_STATEMENT));
        return metadataToMint;
    }

    /**
     * For testing, c.f. declaring a method parameter of type `Principal`
     * (I can't figure out how to get openapi to generate code like that).
     */
    public Raid1PostAuthenicationJsonWebToken getAuthentication() {
        return Guard.isInstance(
                Raid1PostAuthenicationJsonWebToken.class,
                getContext().getAuthentication());
    }

    /**
     * This method catches "encoded" handle slashes.
     * Watch out - handles have slashes in them, by definition 😢
     * Currently, API clients encode the handle slash as `%2f` - but that triggers
     * the default Spring HttpStrictFirewall.
     * We've disabled that in {@link RaidWebSecurityConfig}, which is a risk.
     * V2 API should always pass handles as params instead of in the path?
     *
     * @see RaidWebSecurityConfig#allowUrlEncodedSlashHttpFirewall
     * @see #handleCatchAll(HttpServletRequest, Boolean)
     */
    public RaidPublicModel handleRaidIdGet(
            String raidId,
            Boolean demo
    ) {
        Guard.hasValue("raidId must have a value", raidId);
        guardDemoEnv(demo);

        RaidPublicModel result = db.
                select(RAID.HANDLE, RAID.URL, RAID.DATE_CREATED).
                from(RAID).
                where(RAID.HANDLE.eq(raidId)).
                fetchOne(r -> new RaidPublicModel().
                        handle(r.get(RAID.HANDLE)).
                        contentPath(r.get(RAID.URL)).
                        creationDate(formatIsoDateTime(r.get(RAID.DATE_CREATED))));
        if (result == null) {
            throw apiSafe(HANDLE_NOT_FOUND, 404, of(raidId));
        }
        return result;
    }

    public void guardDemoEnv(Boolean demo) {
        if (isTrue(demo)) {
            throw apiSafe(DEMO_NOT_SUPPPORTED, 400);
        }
    }

    /**
     * This method catches all prefixes with path prefix `/v1/handle` and attempts
     * to parse the parameter manually, so that we can receive handles that are
     * just formatted with simple slashes.
     * The openapi spec is defined with the "{raidId}' path param because it makes
     * it more clear to the caller what the url is expected to look like.
     * <p>
     * IMPROVE: should write some detailed/edgeCase unit tests for this
     * path parsing logic.
     */
    @RequestMapping(
            method = RequestMethod.GET,
            value = HANDLE_URL_PREFIX + "/**",
            produces = {"application/json"}
    )
    public RaidPublicModel handleCatchAll(
            HttpServletRequest req,
            @RequestParam(value = "demo", required = false) Boolean demo
    ) {
        String path = urlDecode(req.getServletPath().trim());
        if (!path.startsWith(HANDLE_CATCHALL_PREFIX)) {
            throw iae("unexpected path: %s", path);
        }

        String handle = path.substring(HANDLE_CATCHALL_PREFIX.length());
        if (!handle.contains(HANDLE_SEPARATOR)) {
            throw apiSafe("handle did not contain a slash character",
                    400, of(handle));
        }

        return handleRaidIdGet(handle, demo);
    }

    /**
     * There was no authz in the legacy app, this endpoint will allow minting
     * as longs as there's a valid legacy token whose name matches exactly with
     * a service point in the new system.
     * So the authz logic is "if they submit with a valid token from the old system
     * that has a "name" that matches a service point in the new system, they can
     * mint.
     */
    @Transactional(propagation = NEVER)
    @Override
    public RaidModel rAiDPost(RaidCreateModel req) {
        var v1UserToken = getAuthentication();
        guardV1MintInput(req);
        populateMintDefaultValues(req, v1UserToken.getName());

    /* not sure about the timezone stuff here, I guess we're assuming the
     client is sending what they used to send, which I think was AEST/Sydney
     time? */
        var startDate = parseDynamoDateTime(req.getStartDate()).toLocalDate();

        var servicePoint = raidSvc.findServicePoint(v1UserToken.getName());
        LegacyMetadataSchemaV1 metadataToMint =
                mapLegacyApiModelToLegacySchema(req, startDate);

        IdentifierUrl id;
        try {
            id = raidSvc.mintLegacySchemaV1(
                    servicePoint.getId(),
                    metadataToMint);
        } catch (ValidationFailureException e) {
            throw apiSafe(RAID_V1_MINT_DATA_ERROR, 400,
                    e.getFailures().stream().map(i1 ->
                            "%s - %s".formatted(i1.getFieldId(), i1.getMessage())
                    ).toList());
        }

        var raid = raidSvc.readRaidV2Data(id.handle().format());
        var mintedMetadata = metaSvc.mapObject(
                raid.raid().getMetadata(), RaidoMetadataSchemaV1.class);
        var description =
                RaidoSchemaV1Util.getFirstPrimaryDescription(mintedMetadata.getDescriptions()).
                        map(DescriptionBlock::getDescription).
                        orElse(null);

        return new RaidModel().
                handle(id.handle().format()).
                owner(raid.servicePoint().getName()).
                contentPath(raid.raid().getUrl()).
                startDate(formatIsoDate(raid.raid().getStartDate())).
                creationDate(formatDynamoDateTime(raid.raid().getDateCreated())).
                meta(new RaidModelMeta().
                        name(raid.raid().getPrimaryTitle()).
                        description(description)).
                providers(emptyList()).
                institutions(emptyList());
    }

    private void populateMintDefaultValues(RaidCreateModel create, String owner) {
        if (hasValue(create.getStartDate())) {
            // just leave it alone for the moment, maybe add to the guard method
            // to check the format
        } else {
            create.setStartDate(formatDynamoDateTime(LocalDateTime.now()));
        }

        // meta is expected to be guarded, should net get nulls here
        if (isBlank(create.getMeta().getDescription())) {
      /* current time in sydney is dodgy.
      Doesn't even work for most of Aus, let alone Oceania and people are
      allowed to mint from other timezones too! */
            create.getMeta().setDescription(
                    "RAiD created by '%s' at '%s'".formatted(
                            owner, formatRaidV1DateTime(LocalDateTime.now())));
        }

    }

    public void guardV1MintInput(RaidCreateModel create) {
        List<String> problems = new ArrayList<>();
        if (!hasValue(create.getContentPath())) {
            problems.add("no 'contentPath' provided");
        }

        if (create.getMeta() == null) {
            problems.add("no 'meta' provided");
            throw apiSafe(RAID_V1_MINT_DATA_ERROR, 400, problems);
        }
    
    /* the  fake name generation logic has been removed - makes no sense
    v2 UI will use v2 API and will create in one step.  
    Legacy API users are expected to be sending a name. */
        if (!hasValue(create.getMeta().getName())) {
            problems.add("no 'meta.name' provided");
        }

        if (!problems.isEmpty()) {
            throw apiSafe(RAID_V1_MINT_DATA_ERROR, 400, problems);
        }
    }

}