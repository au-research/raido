package au.org.raid.api.service.auth;

import au.org.raid.api.repository.AppUserRepository;
import au.org.raid.api.spring.config.environment.RaidV2ApiKeyAuthProps;
import au.org.raid.api.spring.security.raidv2.ApiToken;
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
import static au.org.raid.api.util.ExceptionUtil.authFailed;
import static au.org.raid.api.util.ExceptionUtil.wrapException;
import static au.org.raid.api.util.JwtUtil.JWT_TOKEN_TYPE;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.areEqual;
import static au.org.raid.api.util.StringUtil.mask;
import static java.util.Optional.of;

/**
 * Handles signing and verifying JWTs for signing in (does not handle api-keys).
 */
@Component
public class RaidV2ApiKeyApiTokenService {
    private static final Log log = to(RaidV2ApiKeyApiTokenService.class);

    private RaidV2ApiKeyAuthProps apiAuthProps;
    private AppUserRepository appUserRepo;

    public RaidV2ApiKeyApiTokenService(
            RaidV2ApiKeyAuthProps apiAuthProps,
            AppUserRepository appUserRepo
    ) {
        this.apiAuthProps = apiAuthProps;
        this.appUserRepo = appUserRepo;
    }

    public static String sign(
            Algorithm algorithm,
            ApiToken payload,
            Instant expiresAt,
            String issuer
    ) {
        try {
            String token = JWT.create().
                    withIssuer(issuer).
                    withIssuedAt(Instant.now()).
                    withExpiresAt(expiresAt).
                    // remember the standard claim for subject is "sub"
                            withSubject(payload.getSubject()).
                    withClaim(RaidoClaim.IS_AUTHORIZED_APP_USER.getId(), true).
                    withClaim(RaidoClaim.APP_USER_ID.getId(), payload.getAppUserId()).
                    withClaim(RaidoClaim.SERVICE_POINT_ID.getId(), payload.getServicePointId()).
                    withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
                    withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
                    withClaim(RaidoClaim.ROLE.getId(), payload.getRole()).
                    sign(algorithm);

            return token;
        } catch (JWTCreationException ex) {
            throw wrapException(ex, "while signing");
        }
    }

    public String sign(
            ApiToken payload,
            Instant expiresAt
    ) {
        return sign(
                apiAuthProps.signingAlgo,
                payload,
                expiresAt,
                apiAuthProps.issuer);
    }

    public Optional<Authentication> verifyAndAuthorizeApiToken(
            DecodedJWT decodedJwt
    ) {

        // avoid dodgy stuff like someone crafting a JWT with alg = "none"
        if (!areEqual(
                decodedJwt.getAlgorithm(),
                apiAuthProps.signingAlgo.getName())
        ) {
            log.with("signingAlgo", apiAuthProps.signingAlgo.getName()).
                    with("jwtAlgo", decodedJwt.getAlgorithm()).
                    with("claims", decodedJwt.getClaims()).
                    error("JWT signing algorithm mismatch for api-key");
            throw authFailed();
        }

        if (!areEqual(decodedJwt.getType(), JWT_TOKEN_TYPE)) {
            log.with("decodedJwt.type", decodedJwt.getType()).
                    with("claims", decodedJwt.getClaims()).
                    error("JWT type mismatch for api-key");
            throw authFailed();
        }

    /* verify will fail if JWT is expired, the iat claim is driven by the 
    tokenCutoff field. */
        var verifiedJwt = verifyJwtSignature(decodedJwt);

        String clientId = verifiedJwt.
                getClaim(RaidoClaim.CLIENT_ID.getId()).asString();
    /* one day, we ought to change this claim to "IDENTITY", when that's 
    implemented though, we need to consider backward compatibility. */
        String identity = verifiedJwt.getClaim(RaidoClaim.EMAIL.getId()).asString();
        Boolean isAuthzAppUser = verifiedJwt.getClaim(
                RaidoClaim.IS_AUTHORIZED_APP_USER.getId()).asBoolean();
        var issuedAt = verifiedJwt.getIssuedAtAsInstant();
        Guard.notNull(issuedAt);
        var subject = verifiedJwt.getSubject();

        Guard.hasValue(subject);
        Guard.hasValue(clientId);
        Guard.hasValue(identity);

        if (!isAuthzAppUser) {
      /* there's no "auth request" step for api-keys, they're "authorized as 
      soon as an Op or Admin creates them.
      This shouldn't happen - investigate.  */
            log.with("claims", verifiedJwt.getClaims()).
                    error("verified api-key with bad IS_AUTHORIZED_APP_USER value");
            throw authFailed();
        }

        Long appUserId = verifiedJwt.
                getClaim(RaidoClaim.APP_USER_ID.getId()).asLong();
        Long servicePointId = verifiedJwt.getClaim(
                RaidoClaim.SERVICE_POINT_ID.getId()).asLong();
        String role = verifiedJwt.getClaim(RaidoClaim.ROLE.getId()).asString();
        Guard.notNull(appUserId);

    /* note that we don't currently validate the role from the claim (from when 
    the api-token was generated) against the current value on the api-key in 
    the DB. That means an api-token created before the api-key is changed is
    still valid, but functions using the role from the api-token, not from 
    the api-key. Update the api-key.md doco if you change this. */
        Guard.hasValue(role);

    /* I considered changing this to a quad of [servicePoint, email, clientId, 
    subject], but given this query runs for every single API request, I decided
    to leave it so that the SQL query can just work off the PK index.
    As long you create the api-key in PROD, the next DB refresh will bring that
    to DEMO and then every DB refresh after that the appUserId is stable and
    so their DEMO keys will work across refreshes. */
        var user = appUserRepo.getApiKeyRecord(appUserId).orElseThrow(() -> {
            log.with("appUserId", appUserId).
                    with("email", identity).
                    with("subject", subject).
                    with("clientId", clientId).
                    with("role", role).
                    warn("attempted token authz - 'isAuthorizedAppUser' but no DB record");
            return authFailed();
        });

        if (!user.getEnabled()) {
            log.with("appUserId", appUserId).with("email", identity).
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
                withSubject(subject).
                withEmail(identity).
                withClientId(clientId).
                withRole(role).
                build());
    }

    public DecodedJWT verifyJwtSignature(DecodedJWT decodedJwt) {
        DecodedJWT verifiedJwt = null;
        JWTVerificationException firstEx = null;
        for (int i = 0; i < apiAuthProps.verifiers.length; i++) {
            try {
                verifiedJwt = apiAuthProps.verifiers[i].verify(decodedJwt);
            } catch (JWTVerificationException e) {
                if (firstEx == null) {
                    firstEx = e;
                }
            }
        }
        if (verifiedJwt != null) {
            return verifiedJwt;
        }
        log.with("firstException", firstEx == null ? "null" : firstEx.getMessage()).
                with("token", mask(decodedJwt.getToken())).
                with("verifiers", apiAuthProps.verifiers.length).
                with("serverIssuer", apiAuthProps).
                with("tokenIssuer", decodedJwt.getIssuer()).
                info("jwt not verified by any of the secrets");
        throw authFailed();
    }


}
