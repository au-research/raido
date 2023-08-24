package au.org.raid.api.service.auth;

import au.org.raid.api.repository.AppUserRepository;
import au.org.raid.api.spring.config.environment.RaidV2AppUserAuthProps;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.api.spring.security.raidv2.UnapprovedUserApiToken;
import au.org.raid.api.util.Guard;
import au.org.raid.api.util.Log;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static au.org.raid.api.spring.security.raidv2.ApiToken.ApiTokenBuilder.anApiToken;
import static au.org.raid.api.spring.security.raidv2.UnapprovedUserApiToken.UnapprovedUserApiTokenBuilder.anUnapprovedUserApiToken;
import static au.org.raid.api.util.ExceptionUtil.authFailed;
import static au.org.raid.api.util.ExceptionUtil.wrapException;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.areEqual;
import static au.org.raid.api.util.StringUtil.mask;
import static java.util.Optional.of;
import static org.eclipse.jetty.util.TypeUtil.isFalse;

/**
 * Handles signing and verifying api-token JWTs for signing in for app-users
 * (human users; does not handle api-keys which are for machine users).
 */
@Component
public class RaidV2AppUserApiTokenService {
    public static final String IS_AUTHORIZED_APP_USER = "isAuthorizedAppUser";
    private static final Log log = to(RaidV2AppUserApiTokenService.class);
    private RaidV2AppUserAuthProps userAuthProps;
    private AppUserRepository appUserRepo;

    public RaidV2AppUserApiTokenService(
            RaidV2AppUserAuthProps userAuthProps,
            AppUserRepository appUserRepo
    ) {
        this.userAuthProps = userAuthProps;
        this.appUserRepo = appUserRepo;
    }

    public static String sign(
            Algorithm algo,
            String issuer,
            Instant expiresAt,
            UnapprovedUserApiToken payload
    ) {
        try {
            String token = JWT.create().
                    // remember the standard claim for subject is "sub"
                            withSubject(payload.getSubject()).
                    withIssuer(issuer).
                    withIssuedAt(Instant.now()).
                    withExpiresAt(expiresAt).
                    withClaim(IS_AUTHORIZED_APP_USER, false).
                    withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
                    withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
                    sign(algo);

            return token;
        } catch (JWTCreationException ex) {
            throw wrapException(ex, "while signing");
        }
    }

    public String sign(ApiToken payload) {
        try {
            String token = JWT.create().
                    // remember the standard claim for subject is "sub"
                            withSubject(payload.getSubject()).
                    withIssuer(userAuthProps.issuer).
                    withIssuedAt(Instant.now()).
                    withExpiresAt(calculateExpiresAt()).
                    withClaim(RaidoClaim.IS_AUTHORIZED_APP_USER.getId(), true).
                    withClaim(RaidoClaim.APP_USER_ID.getId(), payload.getAppUserId()).
                    withClaim(RaidoClaim.SERVICE_POINT_ID.getId(),
                            payload.getServicePointId()).
                    withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
                    withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
                    withClaim(RaidoClaim.ROLE.getId(), payload.getRole()).
                    sign(userAuthProps.signingAlgo);

            return token;
        } catch (JWTCreationException ex) {
            throw wrapException(ex, "while signing");
        }
    }

    private Instant calculateExpiresAt() {
        return Instant.now().plusSeconds(
                userAuthProps.authzTokenExpirySeconds);
    }

    public String sign(UnapprovedUserApiToken payload) {
        return sign(
                userAuthProps.signingAlgo,
                userAuthProps.issuer,
                calculateExpiresAt(),
                payload);
    }

    public Optional<Authentication> verifyAndAuthorizeApiToken(
            DecodedJWT decodedJwt
    ) {
        var verifiedJwt = verifyJwtSignature(decodedJwt);

        String clientId = verifiedJwt.
                getClaim(RaidoClaim.CLIENT_ID.getId()).asString();
        String email = verifiedJwt.getClaim(RaidoClaim.EMAIL.getId()).asString();
        Boolean isApprovedAppUser = verifiedJwt.getClaim(
                RaidoClaim.IS_AUTHORIZED_APP_USER.getId()).asBoolean();

        var issuedAt = verifiedJwt.getIssuedAtAsInstant();
        Guard.notNull(issuedAt);

        Guard.hasValue(verifiedJwt.getSubject());
        Guard.hasValue(clientId);
        Guard.hasValue(email);

    /* Only ever happens just after the user signs-in and the /idpresponse 
    endpoint returns UnapprovedUserApiToken, user then creates an authz-request, 
    we have to do this logic to map to this for that one endpoint to check that 
    only unapproved users can use that endpoint. */
        if (!isApprovedAppUser) {
            return of(anUnapprovedUserApiToken().
                    withSubject(verifiedJwt.getSubject()).
                    withEmail(email).
                    withClientId(clientId).
                    build());
        }

        Long appUserId = verifiedJwt.
                getClaim(RaidoClaim.APP_USER_ID.getId()).asLong();
        Long servicePointId = verifiedJwt.getClaim(
                RaidoClaim.SERVICE_POINT_ID.getId()).asLong();
        String role = verifiedJwt.getClaim(RaidoClaim.ROLE.getId()).asString();
        Guard.notNull(appUserId);
        Guard.hasValue(role);

        var user = appUserRepo.getAppUserRecord(appUserId).
                orElseThrow(() -> {
                    log.with("appUserId", appUserId).
                            with("email", email).
                            with("subject", verifiedJwt.getSubject()).
                            with("clientId", clientId).
                            with("role", role).
                            warn("attempted token authz -" +
                                    " 'isAuthorizedAppUser' but no DB record");
                    return authFailed();
                });

        if (isFalse(user.getEnabled())) {
            log.with("appUserId", appUserId).with("email", email).
                    warn("attempted token authz - disabled user");
            throw authFailed();
        }

        if (user.getTokenCutoff() != null) {
            Instant cutoff = user.getTokenCutoff().toInstant(ZoneOffset.UTC);
            if (cutoff.isBefore(issuedAt)) {
        /* user is not disabled, but we've set a token cutoff, they will need
         to login again.
         SP would need to look in their user list to know user is expired. */
                log.with("appUserId", user.getId()).
                        with("email", user.getEmail()).
                        with("tokenCutoff", cutoff).
                        with("issuedAt", issuedAt).
                        warn("attempted token authz - with token issued before tokenCutoff");
                throw authFailed();
            }
        }

        if (!areEqual(servicePointId, user.getServicePointId())) {
            log.with("claim.servicePointId", servicePointId).
                    with("db.servicePointId", user.getServicePointId()).
                    error("service point id from DB and claim are different");
            throw authFailed();
        }

        return of(anApiToken().
                withAppUserId(appUserId).
                withServicePointId(servicePointId).
                withSubject(verifiedJwt.getSubject()).
                withEmail(email).
                withClientId(clientId).
                withRole(role).
                build());
    }

    public DecodedJWT verifyJwtSignature(DecodedJWT decodedJwt) {
        DecodedJWT jwt = null;
        JWTVerificationException firstEx = null;
        for (int i = 0; i < userAuthProps.verifiers.length; i++) {
            try {
                jwt = userAuthProps.verifiers[i].verify(decodedJwt);
            } catch (JWTVerificationException e) {
                if (firstEx == null) {
                    firstEx = e;
                }
            }
        }
        if (jwt != null) {
            return jwt;
        }
        log.with("firstException", firstEx == null ? "null" : firstEx.getMessage()).
                with("token", mask(decodedJwt.getToken())).
                with("verifiers", userAuthProps.verifiers.length).
                info("jwt not verified by any of the secrets");
        throw authFailed();
    }

}
