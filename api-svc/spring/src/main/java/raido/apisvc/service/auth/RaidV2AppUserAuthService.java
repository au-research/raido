package raido.apisvc.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import raido.apisvc.repository.AppUserRepository;
import raido.apisvc.spring.config.environment.RaidV2AppUserAuthProps;
import raido.apisvc.spring.security.raidv2.AuthzTokenPayload;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.IdProvider;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static java.util.Optional.of;
import static org.eclipse.jetty.util.TypeUtil.isFalse;
import static raido.apisvc.service.auth.NonAuthzTokenPayload.NonAuthzTokenPayloadBuilder.aNonAuthzTokenPayload;
import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.spring.security.raidv2.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.util.ExceptionUtil.authFailed;
import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.apisvc.util.StringUtil.mask;

/**
 Handles signing and verifying JWTs for signing in (does not handle api-keys).
 */
@Component
public class RaidV2AppUserAuthService {
  private static final Log log = to(RaidV2AppUserAuthService.class);

  public static final String IS_AUTHORIZED_APP_USER = "isAuthorizedAppUser";

  private RaidV2AppUserAuthProps userAuthProps;
  private AppUserRepository appUserRepo;
  private AafOidc aaf;
  private GoogleOidc google;
  private OrcidOidc orcid;
  
  public RaidV2AppUserAuthService(
    RaidV2AppUserAuthProps userAuthProps,
    AppUserRepository appUserRepo, 
    AafOidc aaf,
    GoogleOidc google,
    OrcidOidc orcid
  ) {
    this.userAuthProps = userAuthProps;
    this.appUserRepo = appUserRepo;
    this.aaf = aaf;
    this.google = google;
    this.orcid = orcid;
  }

  public String sign(AuthzTokenPayload payload){
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
          payload.getServicePointId() ).
        withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
        withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
        withClaim(RaidoClaim.ROLE.getId(), payload.getRole()).
        sign(userAuthProps.signingAlgo);

      return token;
    } catch ( JWTCreationException ex){
      throw wrapException(ex, "while signing");
    }
  }

  private Instant calculateExpiresAt() {
    return Instant.now().plusSeconds(
      userAuthProps.authzTokenExpirySeconds );
  }

  public String sign(NonAuthzTokenPayload payload){
    try {
      String token = JWT.create().
        // remember the standard claim for subject is "sub"
        withSubject(payload.getSubject()).
        withIssuer(userAuthProps.issuer).
        withIssuedAt(Instant.now()).
        withExpiresAt(calculateExpiresAt()).
        withClaim(IS_AUTHORIZED_APP_USER, false).
        withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
        withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
        sign(userAuthProps.signingAlgo);

      return token;
    } catch ( JWTCreationException ex){
      throw wrapException(ex, "while signing");
    }
  }


  public Optional<Authentication> verifyAndAuthorize(DecodedJWT decodedJwt){
    var verifiedJwt = verify(decodedJwt);
    
    String clientId = verifiedJwt.
      getClaim(RaidoClaim.CLIENT_ID.getId()).asString();
    String email = verifiedJwt.getClaim(RaidoClaim.EMAIL.getId()).asString();
    Boolean isAuthzAppUser = verifiedJwt.getClaim(
      RaidoClaim.IS_AUTHORIZED_APP_USER.getId() ).asBoolean();
    
    var issuedAt = verifiedJwt.getIssuedAtAsInstant();
    Guard.notNull(issuedAt);
    
    Guard.hasValue(verifiedJwt.getSubject());
    Guard.hasValue(clientId);
    Guard.hasValue(email);
    
    if( !isAuthzAppUser ){
      return of(aNonAuthzTokenPayload().
        withSubject(verifiedJwt.getSubject()).
        withEmail(email).
        withClientId(clientId).
        build() );
    }

    Long appUserId = verifiedJwt.
      getClaim(RaidoClaim.APP_USER_ID.getId()).asLong();
    Long servicePointId = verifiedJwt.getClaim(
      RaidoClaim.SERVICE_POINT_ID.getId()).asLong();
    String role = verifiedJwt.getClaim(RaidoClaim.ROLE.getId()).asString();
    Guard.notNull(appUserId);
    Guard.hasValue(role);
    
    var user = appUserRepo.getAppUserRecord(appUserId).
      orElseThrow(()->{
        log.with("appUserId", appUserId).
          with("email", email).
          with("subject", verifiedJwt.getSubject()).
          with("clientId", clientId).
          with("role", role).
          warn("attempted token authz -" +
            " 'isAuthorizedAppUser' but no DB record" );
        return authFailed();
      });

    if( isFalse(user.getEnabled()) ){
      log.with("appUserId", appUserId).with("email", email).
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
      withSubject(verifiedJwt.getSubject()).
      withEmail(email).
      withClientId(clientId).
      withRole(role).
      build() );
  }

  public DecodedJWT verify(DecodedJWT decodedJwt) {
    DecodedJWT jwt = null;
    JWTVerificationException firstEx = null;
    for( int i = 0; i < userAuthProps.verifiers.length; i++ ){
      try {
        jwt = userAuthProps.verifiers[i].verify(decodedJwt);
      }
      catch( JWTVerificationException e ){
        if( firstEx == null ){
          firstEx = e;
        }
      }
    }
    if( jwt != null ){
      return jwt;
    }
    log.with("firstException", firstEx == null ? "null" : firstEx.getMessage()).
      with("token", mask(decodedJwt.getToken())).
      with("verifiers", userAuthProps.verifiers.length).
      info("jwt not verified by any of the secrets");
    throw authFailed();
  }


  /**
   Returns the verified `id_token` from the IDP by calling the OIDC /token
   endpoint.
   */
  public DecodedJWT exchangeCodeForVerifiedJwt(
    String clientId,
    String idpResponseCode
  ){
    var idProvider = mapIdProvider(clientId);
    DecodedJWT idProviderJwt;
    switch( idProvider ){
      case GOOGLE -> idProviderJwt =
        google.exchangeCodeForVerifiedJwt(idpResponseCode);
      case AAF -> idProviderJwt =
        aaf.exchangeCodeForVerifiedJwt(idpResponseCode);
      case ORCID -> idProviderJwt =
        orcid.exchangeCodeForVerifiedJwt(idpResponseCode);
      default -> throw idpException("unknown clientId %s", clientId);
    }

    return idProviderJwt;
  }

  public IdProvider mapIdProvider(String clientId){
    if( aaf.canHandle(clientId) ){
      return IdProvider.AAF;
    }
    else if( google.canHandle(clientId) ){
      return IdProvider.GOOGLE;
    }
    else if( orcid.canHandle(clientId) ){
      return IdProvider.ORCID;
    }
    else {
      // improve should be a specific authn error?
      // and should it expose the error?
      throw idpException("unknown clientId %s", clientId);
    }
  }
  

}
