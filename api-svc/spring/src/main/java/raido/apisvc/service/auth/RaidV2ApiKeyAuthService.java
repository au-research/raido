package raido.apisvc.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import raido.apisvc.repository.AppUserRepository;
import raido.apisvc.spring.config.environment.RaidV2ApiKeyAuthProps;
import raido.apisvc.spring.security.raidv2.AuthzTokenPayload;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static java.util.Optional.of;
import static org.eclipse.jetty.util.TypeUtil.isFalse;
import static raido.apisvc.spring.security.raidv2.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.util.ExceptionUtil.authFailed;
import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.JwtUtil.JWT_TOKEN_TYPE;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.apisvc.util.StringUtil.mask;

/**
 Handles signing and verifying JWTs for signing in (does not handle api-keys).
 */
@Component
public class RaidV2ApiKeyAuthService {
  private static final Log log = to(RaidV2ApiKeyAuthService.class);

  private RaidV2ApiKeyAuthProps apiAuthProps;
  private AppUserRepository appUserRepo;
  
  public RaidV2ApiKeyAuthService(
    RaidV2ApiKeyAuthProps apiAuthProps,
    AppUserRepository appUserRepo
  ) {
    this.apiAuthProps = apiAuthProps;
    this.appUserRepo = appUserRepo;
  }

  public String sign(
    AuthzTokenPayload payload,
    Instant expiresAt
  ) {
    return sign(
      apiAuthProps.signingAlgo, 
      payload, 
      expiresAt, 
      apiAuthProps.issuer );
  }
  
  public static String sign(
    Algorithm algorithm,
    AuthzTokenPayload payload, 
    Instant expiresAt,
    String issuer
  ){
    try {
      String token = JWT.create().
        // remember the standard claim for subject is "sub"
        withSubject(payload.getSubject()).
        withIssuer(issuer).
        withIssuedAt(Instant.now()).
        withExpiresAt(expiresAt).
        withClaim(RaidoClaim.IS_AUTHORIZED_APP_USER.getId(), true).
        withClaim(RaidoClaim.APP_USER_ID.getId(), payload.getAppUserId()).
        withClaim(RaidoClaim.SERVICE_POINT_ID.getId(), payload.getServicePointId()).
        withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
        withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
        withClaim(RaidoClaim.ROLE.getId(), payload.getRole()).
        sign(algorithm);

      return token;
    } catch ( JWTCreationException ex){
      throw wrapException(ex, "while signing");
    }
  }

  public Optional<Authentication> verifyAndAuthorize(DecodedJWT decodedJwt){
    
    // avoid dodgy stuff like someone crafting a JWT with alg = "none"
    if( !areEqual(
      decodedJwt.getAlgorithm(), 
      apiAuthProps.signingAlgo.getName()) 
    ){
      log.with("signingAlgo", apiAuthProps.signingAlgo.getName()).
        with("jwtAlgo", decodedJwt.getAlgorithm()).
        with("claims", decodedJwt.getClaims()).
        error("JWT signing algorithm mismatch for api-key");
      throw authFailed();
    }
    
    if( !areEqual(decodedJwt.getType(), JWT_TOKEN_TYPE) ){
      log.with("decodedJwt.type", decodedJwt.getType()).
        with("claims", decodedJwt.getClaims()).
        error("JWT type mismatch for api-key");
      throw authFailed();
    }

    /* verify will fail if JWT is expired, the iat claim is driven by the 
    tokenCutoff field. */
    var verifiedJwt = verify(decodedJwt);

    String clientId = verifiedJwt.
      getClaim(RaidoClaim.CLIENT_ID.getId()).asString();
    /* one day, we ought to change this claim to "IDENTITY", when that's 
    implemented though, we need to consider backward compatibility. */
    String identity = verifiedJwt.getClaim(RaidoClaim.EMAIL.getId()).asString();
    Boolean isAuthzAppUser = verifiedJwt.getClaim(
      RaidoClaim.IS_AUTHORIZED_APP_USER.getId() ).asBoolean();
    var issuedAt = verifiedJwt.getIssuedAtAsInstant();
    Guard.notNull(issuedAt);
    var subject = verifiedJwt.getSubject();
    
    Guard.hasValue(subject);
    Guard.hasValue(clientId);
    Guard.hasValue(identity);
    
    if( !isAuthzAppUser ){
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
    var user = appUserRepo.getApiKeyRecord(appUserId).orElseThrow(()->{
      log.with("appUserId", appUserId).
        with("email", identity).
        with("subject", subject).
        with("clientId", clientId).
        with("role", role).
        warn("attempted token authz - 'isAuthorizedAppUser' but no DB record");
      return authFailed();
    });

    if( isFalse(user.getEnabled()) ){
      log.with("appUserId", appUserId).with("email", identity).
        warn("attempted token authz - disabled user");
      throw authFailed();
    }

    if( user.getTokenCutoff() != null ){
      Instant cutoff = user.getTokenCutoff().toInstant(ZoneOffset.UTC);
      if( cutoff.isBefore(issuedAt) ){
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
    
    if( !areEqual(servicePointId, user.getServicePointId()) ){
      log.with("claim.servicePointId", servicePointId).
        with("db.servicePointId", user.getServicePointId()).
        error("service point id from DB and claim are different");
      throw authFailed();
    }
    
    return of(anAuthzTokenPayload().
      withAppUserId(appUserId).
      withServicePointId(servicePointId).
      withSubject(subject).
      withEmail(identity).
      withClientId(clientId).
      withRole(role).
      build() );
  }

  public DecodedJWT verify(DecodedJWT decodedJwt) {
    DecodedJWT verifiedJwt = null;
    JWTVerificationException firstEx = null;
    for( int i = 0; i < apiAuthProps.verifiers.length; i++ ){
      try {
        verifiedJwt = apiAuthProps.verifiers[i].verify(decodedJwt);
      }
      catch( JWTVerificationException e ){
        if( firstEx == null ){
          firstEx = e;
        }
      }
    }
    if( verifiedJwt != null ){
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
