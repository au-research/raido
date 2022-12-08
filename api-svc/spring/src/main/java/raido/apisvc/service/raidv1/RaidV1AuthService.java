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
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.raid_v1_import.tables.Token.TOKEN;

/** "RaidV1" refers to the "v1" API supported by the legacy raid system
 (the one using python lambdas and DynamoDB).
 */
@Component
public class RaidV1AuthService {
  private static final Log log = to(RaidV1AuthService.class);
  

  private DSLContext db;
  private RaidV1AuthProps props;
  
  public RaidV1AuthService(DSLContext db, RaidV1AuthProps props) {
    this.db = db;
    this.props = props;
  }

  public DecodedJWT verify(String token) {
    Guard.hasValue(props.jwtSecret);

    Algorithm algorithm = Algorithm.HMAC256(props.jwtSecret);
    JWTVerifier verifier = JWT.require(algorithm).
      withIssuer(props.issuer).
      build(); 

    DecodedJWT jwt = null;
    try {
      jwt = verifier.verify(token);
    }
    catch( JWTVerificationException e ){
      log.with("problem", e.getMessage()).
        warn("failed to verify raidV1 token");
      throw new ApiSvcAuthenticationException();
    }

    return jwt;

  }

  public Optional<Raid1PostAuthenicationJsonWebToken> authenticate(
    BearerTokenAuthenticationToken preAuth
  ){
    String originalToken = preAuth.getToken();
    DecodedJWT jwt = this.verify(originalToken);
    
    // security:sto assert the token is the shape we expect
    // issuer, etc.
    // make sure it's a V1 token, not a V2 token

    var record = db.select().from(TOKEN).
      where(TOKEN.NAME.eq(jwt.getSubject())).
      fetchOneInto(TokenRecord.class);
   
    if( record == null ){
      log.with("owner", jwt.getSubject()).
        warn("no record found in token table");
      return empty();
    }
    
    return of(new Raid1PostAuthenicationJsonWebToken(jwt.getSubject()));
  }
  
  public String sign(String subject){
    try {
      Algorithm algorithm = Algorithm.HMAC256(props.jwtSecret);
      String token = JWT.create()
        .withSubject(subject)
        .withIssuer(props.issuer)
        .sign(algorithm);

      return token;
    } catch ( JWTCreationException ex){
      throw ExceptionUtil.wrapException(ex, "while signing");
    }
    
  }
}
