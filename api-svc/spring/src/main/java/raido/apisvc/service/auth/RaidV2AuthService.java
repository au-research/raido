package raido.apisvc.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import org.springframework.stereotype.Component;
import raido.apisvc.spring.config.environment.RaidV2AuthProps;
import raido.apisvc.util.ExceptionUtil;

import java.time.Duration;
import java.time.Instant;

@Component
public class RaidV2AuthService {
  private RaidV2AuthProps props;

  private Duration expiryPeriod = Duration.ofHours(9);
  
  public RaidV2AuthService(RaidV2AuthProps props) {
    this.props = props;
  }

  public String sign(AuthzTokenPayload payload){
    try {
      Algorithm algorithm = Algorithm.HMAC256(props.jwtSecrets.get(0));
      String token = JWT.create().
        // remember the standard claim for subject is "sub"
        withSubject(payload.subject).
        withIssuer(props.issuer).
        withIssuedAt(Instant.now()).
        withExpiresAt(Instant.now().plus(expiryPeriod)).
        withClaim("clientId", payload.clientId).
        withClaim("email", payload.email).
        withClaim("role", payload.role).
        sign(algorithm);

      return token;
    } catch ( JWTCreationException ex){
      throw ExceptionUtil.wrapException(ex, "while signing");
    }
  }
  
  public static class AuthzTokenPayload {
    public String clientId;
    /** `sub` claim in a standard jwt */
    public String subject;
    public String email;
    public String role;

    public AuthzTokenPayload setClientId(String clientId) {
      this.clientId = clientId;
      return this;
    }

    public AuthzTokenPayload setSubject(String subject) {
      this.subject = subject;
      return this;
    }

    public AuthzTokenPayload setEmail(String email) {
      this.email = email;
      return this;
    }

    public AuthzTokenPayload setRole(String role) {
      this.role = role;
      return this;
    }
  }
  
}
