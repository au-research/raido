package raido.apisvc.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import raido.apisvc.spring.config.environment.RaidV2AuthProps;
import raido.apisvc.spring.security.ApiSvcAuthenticationException;
import raido.apisvc.spring.security.raidv2.RaidV2PreAuthenticatedJsonWebToken;
import raido.apisvc.util.Log;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static java.util.Optional.of;
import static raido.apisvc.service.auth.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.util.ExceptionUtil.wrapException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.mask;

@Component
public class RaidV2AuthService {
  private static final Log log = to(RaidV2AuthService.class);
  
  private RaidV2AuthProps props;
  private DSLContext db;
  
  private Duration expiryPeriod = Duration.ofHours(9);
  
  public RaidV2AuthService(RaidV2AuthProps props, DSLContext db) {
    this.props = props;
    this.db = db;
  }

  public String sign(AuthzTokenPayload payload){
    try {
      String token = JWT.create().
        // remember the standard claim for subject is "sub"
        withSubject(payload.getSubject()).
        withIssuer(props.issuer).
        withIssuedAt(Instant.now()).
        withExpiresAt(Instant.now().plus(expiryPeriod)).
        withClaim("clientId", payload.getClientId()).
        withClaim("email", payload.getEmail()).
        withClaim("role", payload.getRole()).
        sign(props.signingAlgo);

      return token;
    } catch ( JWTCreationException ex){
      throw wrapException(ex, "while signing");
    }
  }


  public Optional<AuthzTokenPayload> authenticate(
    RaidV2PreAuthenticatedJsonWebToken preAuth
  ){
    String originalToken = preAuth.getToken().getToken();
    DecodedJWT jwt = this.verify(originalToken);

    // security:sto check claims and expiry and stuff
    
    return of(anAuthzTokenPayload().
      withSubject(jwt.getSubject()).
      withEmail(jwt.getClaim("email").asString()).
      withClientId(jwt.getClaim("clientId").asString()).
      withRole(jwt.getClaim("role").asString()).
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

}
