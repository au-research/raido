package raido.service.raidv1;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import db.migration.jooq.raid_v1_import.tables.records.TokenRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.spring.security.ApiSvcAuthenticationException;
import raido.spring.security.raidv1.Raid1PostAuthenicationJsonWebToken;
import raido.spring.security.raidv1.Raid1PreAuthenticatedJsonWebToken;
import raido.util.Guard;
import raido.util.Log;

import java.util.Optional;

import static db.migration.jooq.raid_v1_import.tables.Token.TOKEN;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static raido.util.Log.to;

@Component
public class RaidV1AuthService {
  private static final Log log = to(RaidV1AuthService.class);
  
  @Value("${RaidV1Auth.jwtSecret}")
  private String jwtSecret;
  
  @Value("${RaidV1Auth.issuer:https://www.raid.org.au}")
  private String issuer;

  private DSLContext db;

  public RaidV1AuthService(DSLContext db) {
    this.db = db;
  }

  public DecodedJWT verify(String token) {
    Guard.hasValue(jwtSecret);

    Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
    JWTVerifier verifier = JWT.require(algorithm).
      withIssuer(issuer).
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
