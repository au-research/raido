package raido.spring.security.jwt;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import raido.util.Log;

import java.util.Collection;
import java.util.Collections;

import static raido.util.Log.to;

public class Raid1PreAuthenticatedJsonWebToken 
implements Authentication, JwtAuthentication {

  private static final Log log = 
    to(Raid1PreAuthenticatedJsonWebToken.class);

  private final DecodedJWT token;

  Raid1PreAuthenticatedJsonWebToken(DecodedJWT token) {
    this.token = token;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.emptyList();
  }

  @Override
  public Object getCredentials() {
    return token.getToken();
  }

  @Override
  public Object getDetails() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return token.getSubject();
  }

  @Override
  public boolean isAuthenticated() {
    return false;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

  }

  @Override
  public String getName() {
    return token.getSubject();
  }

  public static Raid1PreAuthenticatedJsonWebToken usingToken(String token) {
    if (token == null) {
      log.debug("No token was provided to build PreAuth token");
      return null;
    }
    try {
      DecodedJWT jwt = JWT.decode(token);
      return new Raid1PreAuthenticatedJsonWebToken(jwt);
    } catch (JWTDecodeException e) {
      /* explicitly log stack because excpetion resolver won't see it */
      log.info("Failed to decode token as jwt", e);
      return null;
    }
  }

  @Override
  public String getToken() {
    return token.getToken();
  }

  @Override
  public String getKeyId() {
    return token.getKeyId();
  }

  @Override
  public Authentication verify(JWTVerifier verifier) 
  throws JWTVerificationException {
    log.info("verifying");
    return new AuthenticationJsonWebToken(token.getToken(), verifier);
  }
}
