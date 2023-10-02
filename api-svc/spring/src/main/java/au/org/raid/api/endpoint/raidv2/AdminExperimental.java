package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.endpoint.Constant;
import au.org.raid.api.endpoint.message.ValidationMessage;
import au.org.raid.api.service.auth.admin.AppUserService;
import au.org.raid.api.service.auth.admin.AuthzRequestService;
import au.org.raid.api.service.auth.admin.ServicePointService;
import au.org.raid.api.service.raid.RaidService;
import au.org.raid.api.service.raid.ValidationFailureException;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.service.raid.validation.RaidoSchemaV1ValidationService;
import au.org.raid.api.service.ror.RorService;
import au.org.raid.api.util.*;
import au.org.raid.db.jooq.api_svc.tables.records.AppUserRecord;
import au.org.raid.idl.raidv2.api.AdminExperimentalApi;
import au.org.raid.idl.raidv2.model.*;
import lombok.SneakyThrows;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static au.org.raid.db.jooq.api_svc.tables.AppUser.APP_USER;
import static au.org.raid.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;
import static au.org.raid.db.jooq.api_svc.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class AdminExperimental implements AdminExperimentalApi {
    private final RorService rorService;
    private AuthzRequestService authzRequestSvc;
    private ServicePointService servicePointSvc;
    private AppUserService appUserSvc;
    private RaidoSchemaV1ValidationService validSvc;
    private RaidService raidSvc;
    private BasicRaidExperimental basicRaid;
    private DSLContext db;
    private IdentifierParser idParser;


    public AdminExperimental(
            AuthzRequestService authzRequestSvc,
            ServicePointService servicePointSvc,
            AppUserService appUserSvc,
            RaidoSchemaV1ValidationService validSvc,
            RaidService raidSvc,
            BasicRaidExperimental basicRaid,
            DSLContext db,
            IdentifierParser idParser,
            final RorService rorService) {
        this.authzRequestSvc = authzRequestSvc;
        this.servicePointSvc = servicePointSvc;
        this.appUserSvc = appUserSvc;
        this.validSvc = validSvc;
        this.raidSvc = raidSvc;
        this.basicRaid = basicRaid;
        this.db = db;
        this.idParser = idParser;
        this.rorService = rorService;
    }

    @Override
    public ResponseEntity<List<AuthzRequestExtraV1>> listAuthzRequest() {
        var user = AuthzUtil.getApiToken();
        // this is the authz check, will be moved to a role annotation soon
        Guard.areEqual(user.getRole(), OPERATOR.getLiteral());

        return ResponseEntity.ok(authzRequestSvc.listAllRecentAuthzRequest());
    }

    @Override
    public ResponseEntity<AuthzRequestExtraV1> readRequestAuthz(Long authzRequestId) {
        // have to read it before we can see if user is allowed for servicePoint
        var authRequest = authzRequestSvc.readAuthzRequest(authzRequestId);
        var user = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(user, authRequest.getServicePointId());
        return ResponseEntity.ok(authRequest);
    }

    @Override
    public ResponseEntity<Void> updateAuthzRequestStatus(UpdateAuthzRequestStatus req) {
        Guard.notNull("must provide authzRequestId", req.getAuthzRequestId());
        Guard.notNull("must provide status", req.getStatus());

        var user = AuthzUtil.getApiToken();

        var authzRecord = db.fetchSingle(
                USER_AUTHZ_REQUEST,
                USER_AUTHZ_REQUEST.ID.eq(req.getAuthzRequestId()));

        AuthzUtil.guardOperatorOrAssociatedSpAdmin(user, authzRecord.getServicePointId());

        authzRequestSvc.updateAuthzRequestStatus(user, req, authzRecord);

        return null;
    }

    @Override
    public ResponseEntity<List<ServicePoint>> listServicePoint() {
        var user = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrSpAdmin(user);

        return ResponseEntity.ok(db.select().from(SERVICE_POINT).
                orderBy(SERVICE_POINT.NAME.asc()).
                limit(Constant.MAX_EXPERIMENTAL_RECORDS).
                fetchInto(ServicePoint.class));
    }

    /**
     * IMPROVE: Currently gives a 500 error if not found, 404 might be better?
     */
    @Override
    public ResponseEntity<ServicePoint> readServicePoint(Long servicePointId) {
        var user = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrAssociated(user, servicePointId);

        return ResponseEntity.ok(db.select().from(SERVICE_POINT).
                where(SERVICE_POINT.ID.eq(servicePointId)).
                fetchSingleInto(ServicePoint.class));
    }

    @Override
    @SneakyThrows
    public ResponseEntity<ServicePoint> updateServicePoint(ServicePoint req) {
        var user = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(user, req.getId());

        // IMPROVE: probably time to start doing proper validation
        Guard.notNull(req);
        Guard.hasValue("must have a name", req.getName());
        Guard.hasValue("must have an identifier owner", req.getIdentifierOwner());
        Guard.notNull("must have adminEmail", req.getAdminEmail());
        Guard.notNull("must have techEmail", req.getTechEmail());
        Guard.notNull("must have a enabled flag", req.getEnabled());

        Guard.isTrue(
                () -> "identifierOwner is too long: %s".formatted(req.getIdentifierOwner()),
                JooqUtil.valueFits(SERVICE_POINT.IDENTIFIER_OWNER, req.getIdentifierOwner()));

        final var failures = rorService.validate(req.getIdentifierOwner(), "servicePoint.identifierOwner");
        if (!failures.isEmpty()) {
            throw new ValidationFailureException(failures);
        }

        return ResponseEntity.ok(servicePointSvc.updateServicePoint(req));
    }

    @Override
    public ResponseEntity<AppUser> readAppUser(Long appUserId) {
        var user = AuthzUtil.getApiToken();
        if (ObjectUtil.areEqual(user.getAppUserId(), appUserId)) {
            // user is allowed to read their own record
        } else if (AuthzUtil.isOperatorOrSpAdmin(user)) {
      /* operators or spAdmin can read info about any user in any service 
      point, spAdmin might be looking at a user that was approved onto a 
      different service point. */
        } else {
            var iae = ExceptionUtil.iae("user read not allowed");
            throw iae;
        }

        return ResponseEntity.ok(appUserSvc.readAppUser(appUserId));
    }

    @Override
    public ResponseEntity<AppUserExtraV1> readAppUserExtra(Long appUserId) {
        var user = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrSpAdmin(user);

        var appUser = readAppUser(appUserId).getBody();
        assert appUser != null;
        var servicePoint = readServicePoint(appUser.getServicePointId()).getBody();

        var authzRequest = authzRequestSvc.readAuthzRequestForUser(appUser);

        // bootstrapped user has no authzRequest, was auto-approved
        return authzRequest.map(authzRequestExtraV1 -> ResponseEntity.ok(new AppUserExtraV1().
                appUser(appUser).
                servicePoint(servicePoint).
                authzRequest(authzRequestExtraV1))).orElseGet(() -> ResponseEntity.ok(new AppUserExtraV1().
                appUser(appUser).
                servicePoint(servicePoint)));

    }

    @Override
    public ResponseEntity<List<AppUser>> listAppUser(Long servicePointId) {
        var user = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(user, servicePointId);

        return ResponseEntity.ok(appUserSvc.listAppUser(servicePointId));
    }

    @Override
    public ResponseEntity<AppUser> updateAppUser(AppUser req) {
        var invokingUser = AuthzUtil.getApiToken();

        var targetUser = db.fetchSingle(APP_USER, APP_USER.ID.eq(req.getId()));

        // spAdmin can only edit users in their associated SP
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(
                invokingUser, targetUser.getServicePointId());

        appUserSvc.updateAppUser(req, invokingUser, targetUser);

        return readAppUser(targetUser.getId());
    }

    @Override
    public ResponseEntity<List<ApiKey>> listApiKey(Long servicePointId) {
        var user = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(user, servicePointId);

        return ResponseEntity.ok(appUserSvc.listApiKey(servicePointId));
    }

    @Override
    public ResponseEntity<ApiKey> updateApiKey(ApiKey req) {
        var invokingUser = AuthzUtil.getApiToken();
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(invokingUser, req.getServicePointId());

        long id = appUserSvc.updateApiKey(req, invokingUser);

        return readApiKey(id);
    }

    @Override
    public ResponseEntity<ApiKey> readApiKey(Long apiKeyId) {
        var invokingUser = AuthzUtil.getApiToken();
        ApiKey apiKey = appUserSvc.readApiKey(apiKeyId);
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(invokingUser, apiKey.getServicePointId());
        return ResponseEntity.ok(apiKey);
    }

    @Override
    public ResponseEntity<GenerateApiTokenResponse> generateApiToken(
            GenerateApiTokenRequest req
    ) {
        var invokingUser = AuthzUtil.getApiToken();
        AppUserRecord apiKey = db.
                fetchSingle(APP_USER, APP_USER.ID.eq(req.getApiKeyId()));
        AuthzUtil.guardOperatorOrAssociatedSpAdmin(invokingUser, apiKey.getServicePointId());

        String apiToken = appUserSvc.generateApiToken(apiKey);

        return ResponseEntity.ok(new GenerateApiTokenResponse().
                apiKeyId(req.getApiKeyId()).
                apiToken(apiToken));
    }

    @Override
    public ResponseEntity<MintResponse> migrateLegacyRaid(MigrateLegacyRaidRequest req) {
        var mint = req.getMintRequest();
        var user = AuthzUtil.getApiToken();
        /* instead of allowing api-keys to have operator role, we just enforce
         * that the key is admin role and is for the raido SP. */
        AuthzUtil.guardRaidoAdminApiKey(user);

        IdBlock idBlock = req.getMetadata().getId();

        var failures = new ArrayList<ValidationFailure>();
        failures.addAll(validSvc.validateIdBlockForMigration(idBlock));
        failures.addAll(validSvc.validateLegacySchemaV1(req.getMetadata()));
        if (req.getMintRequest().getServicePointId() == null) {
            failures.add(ValidationMessage.fieldNotSet("mintRequest.servicePointId"));
        }
        if (req.getMintRequest().getContentIndex() == null) {
            failures.add(ValidationMessage.fieldNotSet("mintRequest.contentIndex"));
        }
        if (req.getMintRequest().getCreateDate() == null) {
            failures.add(ValidationMessage.fieldNotSet("mintRequest.createDate"));
        }
        if (!failures.isEmpty()) {
            return ResponseEntity.ok(new MintResponse().success(false).failures(failures));
        }

        // it'll work because IdBlock's already been validated
        var id = (IdentifierUrl) idParser.parseUrl(idBlock.getIdentifier());

        try {
            raidSvc.migrateRaidoSchemaV1(
                    req.getMintRequest().getServicePointId(),
                    req.getMintRequest().getContentIndex(),
                    req.getMintRequest().getCreateDate(),
                    req.getMetadata());
        } catch (ValidationFailureException e) {
            return ResponseEntity.ok(new MintResponse().success(false).failures(e.getFailures()));
        }

        // improve: this is unnecessary overhead - migration scripts don't care
        // about the response.
        return ResponseEntity.ok(new MintResponse().success(true).
                raid(raidSvc.readRaidResponseV2(id.handle().format())));
    }
}
