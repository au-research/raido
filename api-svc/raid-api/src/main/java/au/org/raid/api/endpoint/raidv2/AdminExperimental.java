package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.factory.ServicePointFactory;
import au.org.raid.api.service.ServicePointService;
import au.org.raid.api.service.auth.admin.AppUserService;
import au.org.raid.api.service.auth.admin.AuthzRequestService;
import au.org.raid.api.util.ExceptionUtil;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.ObjectUtil;
import au.org.raid.db.jooq.tables.records.AppUserRecord;
import au.org.raid.idl.raidv2.api.AdminExperimentalApi;
import au.org.raid.idl.raidv2.model.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static au.org.raid.db.jooq.enums.UserRole.OPERATOR;
import static au.org.raid.db.jooq.tables.AppUser.APP_USER;
import static au.org.raid.db.jooq.tables.UserAuthzRequest.USER_AUTHZ_REQUEST;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@CrossOrigin
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
@RequiredArgsConstructor
public class AdminExperimental implements AdminExperimentalApi {
    private final AuthzRequestService authzRequestSvc;
    private final AppUserService appUserSvc;
    private final DSLContext db;
    private final ServicePointService servicePointService;
    private final ServicePointFactory servicePointFactory;

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
//        var servicePoint = readServicePoint(appUser.getServicePointId()).getBody();

        final var servicePoint = servicePointService.findById(appUser.getServicePointId())
                .orElseThrow(() -> new RuntimeException(
                        "Service point not found with id %d".formatted(appUser.getServicePointId())));

        var authzRequest = authzRequestSvc.readAuthzRequestForUser(appUser);

        // bootstrapped user has no authzRequest, was auto-approved
        return authzRequest.map(authzRequestExtraV1 -> ResponseEntity.ok(new AppUserExtraV1()
                .appUser(appUser)
                .servicePoint(servicePoint)
                .authzRequest(authzRequestExtraV1))).orElseGet(() -> ResponseEntity.ok(new AppUserExtraV1()
                .appUser(appUser)
                .servicePoint(servicePointFactory.create(servicePoint))));

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
}
