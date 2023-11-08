package au.org.raid.inttest.auth;

import au.org.raid.api.service.auth.RaidV2ApiKeyApiTokenService;
import au.org.raid.api.service.auth.RaidV2AppUserApiTokenService;
import au.org.raid.api.service.raidv1.RaidV1AuthService;
import au.org.raid.api.spring.config.environment.*;
import au.org.raid.db.jooq.api_svc.enums.UserRole;
import au.org.raid.db.jooq.api_svc.tables.records.TokenRecord;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static au.org.raid.api.spring.security.raidv2.ApiToken.ApiTokenBuilder.anApiToken;
import static au.org.raid.api.spring.security.raidv2.UnapprovedUserApiToken.UnapprovedUserApiTokenBuilder.anUnapprovedUserApiToken;
import static au.org.raid.db.jooq.api_svc.enums.IdProvider.RAIDO_API;
import static au.org.raid.db.jooq.api_svc.tables.AppUser.APP_USER;
import static au.org.raid.db.jooq.api_svc.tables.Token.TOKEN;
import static org.jooq.impl.DSL.inline;

/**
 * Component for bootstrapping tokens directly into the Database.
 * Integration Tests often just use this token directly, more complicated test
 * scenarios (i.e. testing as other service-points, testing multiple roles, etc.)
 * use it to create other proper api-tokens for more complicated test scenarios.
 * <p>
 * This direct write to the DB is why the int-tests need to know the DB connection
 * details.
 * <p>
 * The V1 bootstrapped token is hardcode for `RDM@UQ` because they're the only
 * user of the V1 tokens anyway.
 */
@Component
public class BootstrapAuthTokenService {
    public static final String INTTEST_ENV = "inttest";
    @Autowired
    protected DSLContext db;
    @Autowired
    protected RaidV1AuthProps authProps;
    @Autowired
    protected RaidV2ApiKeyAuthProps authApiKeyProps;
    @Autowired
    protected RaidV2AppUserAuthProps authAppUserKeyProps;
    @Autowired
    protected GoogleOidcProps googleOidcProps;
    @Autowired
    protected EnvironmentProps env;

    /* v1TestOwner is only used for minting via raidV1 endpoint, which is
    only designed for use by RDM anyway.  The logic in the endpoint implementation
    requires it to match an existing service-point, might as well use the real
    one. */
    private String v1TestOwner = "RDM@UQ";


    @Transactional
    public String initRaidV1TestToken() {

        var authSvc = new RaidV1AuthService(db, authProps, env);

        TokenRecord record = db.newRecord(TOKEN);
        String testToken = authSvc.sign(v1TestOwner, INTTEST_ENV);
        record.setName(v1TestOwner).
                setEnvironment(INTTEST_ENV).
                setDateCreated(LocalDateTime.now()).
                setToken(testToken).
                setS3Export(JSONB.valueOf("{}")).
                merge();

        return record.getToken();
    }

    @Transactional
    public String bootstrapToken(long svcPointId, String subject, UserRole role) {
        LocalDateTime expiry = LocalDateTime.now().plusDays(30);
        var apiKeyId = insertApiKey(svcPointId, subject, role);

        var apiToken = RaidV2ApiKeyApiTokenService.sign(
                authApiKeyProps.signingAlgo,
                anApiToken().
                        withAppUserId(apiKeyId).
                        withServicePointId(svcPointId).
                        withSubject(subject).
                        withClientId(RAIDO_API.getLiteral()).
                        withEmail(subject).
                        withRole(role.getLiteral()).
                        build(),
                expiry.toInstant(ZoneOffset.UTC),
                authApiKeyProps.issuer);

        return apiToken;
    }

    /**
     * Initially implemented for testing the authz-request creation
     * functionality.
     * Note that the api-token created is obviously not really for a properly
     * signed user id_token attested by Google, the creation of this api-token
     * basically "fakes" the whole OIDC->/idpresponse->api-token flow.
     */
    @Transactional
    public String fakeUnapprovedGoogle(String subject) {
        LocalDateTime expiry = LocalDateTime.now().plusDays(30);

        var unapprovedPayload = anUnapprovedUserApiToken().
                withClientId(googleOidcProps.clientId).
                withEmail("intTestEmail@example.com").
                withSubject(subject).build();

        var apiToken = RaidV2AppUserApiTokenService.sign(
                authAppUserKeyProps.signingAlgo,
                authAppUserKeyProps.issuer,
                expiry.toInstant(ZoneOffset.UTC),
                unapprovedPayload
        );

        return apiToken;
    }

    private Long insertApiKey(
            long servicePointId,
            String subject,
            UserRole role
    ) {
        return db.insertInto(APP_USER).
                set(APP_USER.SERVICE_POINT_ID, servicePointId).
                set(APP_USER.EMAIL, subject).
                set(APP_USER.CLIENT_ID, RAIDO_API.getLiteral()).
                set(APP_USER.ID_PROVIDER, RAIDO_API).
                set(APP_USER.SUBJECT, subject).
                set(APP_USER.ROLE, role).
                onConflict(
                        APP_USER.EMAIL,
                        APP_USER.CLIENT_ID,
                        APP_USER.SUBJECT).
                // inline needed because: https://stackoverflow.com/a/73782610/924597
                        where(APP_USER.ENABLED.eq(inline(true))).
                doUpdate().
                set(APP_USER.TOKEN_CUTOFF, (LocalDateTime) null).
                returningResult(APP_USER.ID).
                fetchOneInto(Long.class);
    }

}
