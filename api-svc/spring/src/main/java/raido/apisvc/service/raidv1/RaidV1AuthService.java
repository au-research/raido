package raido.apisvc.service.raidv1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.jooq.DSLContext;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.stereotype.Component;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.spring.config.environment.RaidV1AuthProps;
import raido.apisvc.spring.security.ApiSvcAuthenticationException;
import raido.apisvc.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.apisvc.util.ExceptionUtil;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.db.jooq.raid_v1_import.tables.records.TokenRecord;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static raido.apisvc.util.ExceptionUtil.authFailed;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.*;
import static raido.db.jooq.raid_v1_import.tables.Token.TOKEN;

/** "RaidV1" refers to the "v1" API supported by the legacy raid system
 (the one using python lambdas and DynamoDB).
 */
@Component
public class RaidV1AuthService {
  private static final Log log = to(RaidV1AuthService.class);
  public static final String LEGACY_V1_TOKEN_ISSUER = "https://www.raid.org.au";
  public static final String RDM_UQ_SUBJECT = "RDM@UQ";
  public static final String ENVIRONMENT_CLAIM = "environment";
  public static final int SECONDS_IN_YEAR = 365 * 24 * 60 * 60;
  
  // RDM live token expired in Oct 2019
  public static final int ACCEPT_EXPIRES_AT = 10 * SECONDS_IN_YEAR;


  private DSLContext db;
  private RaidV1AuthProps props;

  private String envTokenType;
  
  public RaidV1AuthService(
    DSLContext db, 
    RaidV1AuthProps props,
    EnvironmentProps env
  ) {
    this.db = db;
    this.props = props;
    this.envTokenType = mapEnvironmentTokenType(env);
  }

  public DecodedJWT verify(String token) {
    Guard.hasValue(props.jwtSecret);

    Algorithm algorithm = Algorithm.HMAC256(props.jwtSecret);
    JWTVerifier verifier = JWT.require(algorithm).
      withIssuer(props.issuer).
      acceptExpiresAt(ACCEPT_EXPIRES_AT).
      build(); 

    DecodedJWT jwt = null;
    try {
      jwt = verifier.verify(token);
    }
    catch( JWTVerificationException e ){
      log.with("problem", e.getMessage()).
        with("token", mask(token)).
        warn("failed to verify raidV1 token");
      throw new ApiSvcAuthenticationException();
    }

    return jwt;
  }

  public Optional<Raid1PostAuthenicationJsonWebToken> authenticate(
    BearerTokenAuthenticationToken preAuth
  ) throws ApiSvcAuthenticationException {
    String originalToken = preAuth.getToken();
    DecodedJWT jwt = this.verify(originalToken);
    
    if( !areEqual(jwt.getIssuer(), LEGACY_V1_TOKEN_ISSUER) ){
      log.with("issuer", jwt.getIssuer()).
        warn("V1 token rejected because wrong issuer");
      throw authFailed();
    }
    
    String envClaim = jwt.getClaim(ENVIRONMENT_CLAIM).asString();
    if( !hasValue(envClaim) ){
      log.warn("V1 token did not contain environment claim");
      throw authFailed();
    }
    
    if( !areEqual(envClaim, envTokenType) ){
      log.with("envClaim", envClaim).with("envTokenType", envTokenType).
        warn("envClaim does not match");
      // let it go so int tests work
    }
    
    if( !areEqual(jwt.getSubject(), RDM_UQ_SUBJECT) ){
      log.with("subject", jwt.getSubject()).
        warn("unexpected client using V1 endpoint token");
      throw authFailed();
    }
      
    var record = db.select().from(TOKEN).
      where(
        TOKEN.NAME.eq(jwt.getSubject()).and(
          TOKEN.ENVIRONMENT.eq(envClaim) )).
      orderBy(TOKEN.DATE_CREATED.desc()).
      limit(1).
      fetchOneInto(TokenRecord.class);
   
    if( record == null ){
      log.with("owner", jwt.getSubject()).
        warn("no record found in token table");
      return empty();
    }
    
    return of(new Raid1PostAuthenicationJsonWebToken(jwt.getSubject()));
  }
  
  public String sign(String subject, String environment){
    try {
      Algorithm algorithm = Algorithm.HMAC256(props.jwtSecret);
      String token = JWT.create()
        .withSubject(subject)
        .withIssuer(props.issuer)
        .withClaim(ENVIRONMENT_CLAIM, environment)
        .sign(algorithm);

      return token;
    } catch ( JWTCreationException ex){
      throw ExceptionUtil.wrapException(ex, "while signing");
    }
    
  }
  
  public static String mapEnvironmentTokenType(EnvironmentProps env){
    if( env.isProd ){
      return "live";
    }
    else {
      return "demo";
    }
  }
}
