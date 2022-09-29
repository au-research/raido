package raido.apisvc.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.jooq.DSLContext;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import raido.apisvc.spring.config.environment.RaidV2AuthProps;
import raido.apisvc.spring.security.ApiSvcAuthenticationException;
import raido.apisvc.spring.security.raidv2.RaidV2PreAuthenticatedJsonWebToken;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.IdProvider;
import raido.db.jooq.api_svc.tables.records.AppUserRecord;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static java.util.Optional.of;
import static org.eclipse.jetty.util.TypeUtil.isFalse;
import static raido.apisvc.service.auth.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.service.auth.NonAuthzTokenPayload.NonAuthzTokenPayloadBuilder.aNonAuthzTokenPayload;
import static raido.apisvc.spring.security.IdProviderException.idpException;
import static raido.apisvc.util.ExceptionUtil.authFailed;
import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.areEqual;
import static raido.apisvc.util.ObjectUtil.isTrue;
import static raido.apisvc.util.StringUtil.mask;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;

@Component
public class RaidV2AuthService {
  private static final Log log = to(RaidV2AuthService.class);

  public static final String IS_AUTHORIZED_APP_USER = "isAuthorizedAppUser";

  private RaidV2AuthProps props;
  private DSLContext db;
  private AafOidc aaf;
  private GoogleOidc google;
  
  private Duration expiryPeriod = Duration.ofHours(9);
  
  public RaidV2AuthService(
    RaidV2AuthProps props, 
    DSLContext db,
    AafOidc aaf,
    GoogleOidc google
  ) {
    this.props = props;
    this.db = db;
    this.aaf = aaf;
    this.google = google;
  }

  public String sign(AuthzTokenPayload payload){
    try {
      String token = JWT.create().
        // remember the standard claim for subject is "sub"
        withSubject(payload.getSubject()).
        withIssuer(props.issuer).
        withIssuedAt(Instant.now()).
        withExpiresAt(Instant.now().plus(expiryPeriod)).
        withClaim(RaidoClaim.IS_AUTHORIZED_APP_USER.getId(), true).
        withClaim(RaidoClaim.APP_USER_ID.getId(), payload.getAppUserId()).
        withClaim(RaidoClaim.SERVICE_POINT_ID.getId(), payload.getServicePointId()).
        withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
        withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
        withClaim(RaidoClaim.ROLE.getId(), payload.getRole()).
        sign(props.signingAlgo);

      return token;
    } catch ( JWTCreationException ex){
      throw wrapException(ex, "while signing");
    }
  }

  public String sign(NonAuthzTokenPayload payload){
    try {
      String token = JWT.create().
        // remember the standard claim for subject is "sub"
        withSubject(payload.getSubject()).
        withIssuer(props.issuer).
        withIssuedAt(Instant.now()).
        withExpiresAt(Instant.now().plus(expiryPeriod)).
        withClaim(IS_AUTHORIZED_APP_USER, false).
        withClaim(RaidoClaim.CLIENT_ID.getId(), payload.getClientId()).
        withClaim(RaidoClaim.EMAIL.getId(), payload.getEmail()).
        sign(props.signingAlgo);

      return token;
    } catch ( JWTCreationException ex){
      throw wrapException(ex, "while signing");
    }
  }


  public Optional<Authentication> authorize(
    RaidV2PreAuthenticatedJsonWebToken preAuth
  ){
    String originalToken = preAuth.getToken().getToken();
    DecodedJWT jwt = this.verify(originalToken);

    // security:sto check claims and expiry and stuff
    String clientId = jwt.getClaim(RaidoClaim.CLIENT_ID.getId()).asString();
    String email = jwt.getClaim(RaidoClaim.EMAIL.getId()).asString();
    Boolean isAuthzAppUser = jwt.getClaim(
      RaidoClaim.IS_AUTHORIZED_APP_USER.getId() ).asBoolean();
    
    var issuedAt = jwt.getIssuedAtAsInstant();
    Guard.notNull(issuedAt);
    
    Guard.hasValue(jwt.getSubject());
    Guard.hasValue(clientId);
    Guard.hasValue(email);
    
    if( !isAuthzAppUser ){
      return of(aNonAuthzTokenPayload().
        withSubject(jwt.getSubject()).
        withEmail(email).
        withClientId(clientId).
        build() );
    }

    Long appUserId = jwt.getClaim(RaidoClaim.APP_USER_ID.getId()).asLong();
    Long servicePointId = jwt.getClaim(
      RaidoClaim.SERVICE_POINT_ID.getId()).asLong();
    String role = jwt.getClaim(RaidoClaim.ROLE.getId()).asString();
    Guard.notNull(appUserId);
    Guard.hasValue(role);
    
    var user = getAppUserRecord(appUserId).
      orElseThrow(()->{
        log.with("appUserId", appUserId).
          with("email", email).
          with("subject", jwt.getSubject()).
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
      if( cutoff.isAfter(issuedAt) ){
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
      withSubject(jwt.getSubject()).
      withEmail(email).
      withClientId(clientId).
      withRole(role).
      build() );
  }

  public DecodedJWT verify(String token) {
    DecodedJWT jwt = null;
    JWTVerificationException firstEx = null;
    for( int i = 0; i < props.verifiers.length; i++ ){
      try {
        jwt = props.verifiers[i].verify(token);
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
      with("token", mask(token)).
      with("verifiers", props.verifiers.length).
      info("jwt not verified by any of the secrets");
    throw new ApiSvcAuthenticationException();
  }

  /**
   This is for originating authentication, so don't need or want it to be 
   cached.
   */
  public Optional<AppUserRecord> getAppUserRecord(
    String email,
    String subject, 
    String clientId
  ){
    Guard.hasValue(email);
    Guard.hasValue(clientId);
    Guard.hasValue(subject);

    // service_point_id_fields_active_idx enforces uniqueness 
    return db.select().
      from(APP_USER).
      where(
        APP_USER.EMAIL.eq(email).
          and(APP_USER.CLIENT_ID.eq(clientId)).
          and(APP_USER.SUBJECT.eq(subject)).
          and(APP_USER.ENABLED.isTrue())
      ).fetchOptionalInto(AppUserRecord.class);
  }

  /** This should be cached read, otherwise we're gonna be doing 
   this for every single API call for a user.  Use Caffeine.
   */
  public Optional<AppUserRecord> getAppUserRecord(
    long appUserId
  ){
    return db.fetchOptional(APP_USER, APP_USER.ID.eq(appUserId));
  }

  /**
   Retuns the verified `id_token` from the IDP by calling the OIDC /token
   endpoint.
   */
  public DecodedJWT exchangeCodeForVerfiedJwt(
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
    else {
      // improve should be a specific authn error?
      // and should it expose the error?
      throw idpException("unknown clientId %s", clientId);
    }
  }
  

}
