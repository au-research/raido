package raido.apisvc.service.raidv1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import raido.db.jooq.raid_v1_import.tables.records.TokenRecord;
import raido.apisvc.spring.config.environment.RaidV1AuthProps;
import raido.apisvc.spring.security.ApiSvcAuthenticationException;
import raido.apisvc.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.apisvc.spring.security.raidv1.Raid1PreAuthenticatedJsonWebToken;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static raido.db.jooq.raid_v1_import.tables.Token.TOKEN;
import static raido.apisvc.util.Log.to;

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
      build(); //Reusable verifier instance

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
    Raid1PreAuthenticatedJsonWebToken preAuth
  ){
    String originalToken = preAuth.getToken().getToken();
    DecodedJWT jwt = this.verify(originalToken);

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
}
