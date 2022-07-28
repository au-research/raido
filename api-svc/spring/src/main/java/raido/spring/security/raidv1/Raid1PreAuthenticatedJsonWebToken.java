package raido.spring.security.raidv1;


import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import raido.util.Log;

import java.util.Collection;
import java.util.Collections;

import static raido.util.ExceptionUtil.createIae;
import static raido.util.Log.to;

/**
 Final is intended to avoid sub-classing - there should be no need for a 
 subclass of a pre-auth token.
 */
public final class Raid1PreAuthenticatedJsonWebToken 
implements Authentication {

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

  public DecodedJWT getToken() {
    return token;
  }

  @Override
  public Object getCredentials() {
    return token.getToken();
  }

  @Override
  public Object getDetails() {
    return null;
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
  public void setAuthenticated(boolean isAuthenticated) 
  throws IllegalArgumentException {
    // implementation conforms to interface spec
    if( isAuthenticated ){
      throw createIae("cannot set a pre-auth as authenticated");
    }
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
      // doest not verify the token signature, just decodes it
      DecodedJWT jwt = JWT.decode(token);
      return new Raid1PreAuthenticatedJsonWebToken(jwt);
    } catch (JWTDecodeException e) {
      /* log the stack because exceeption resolver won't see it */
      log.info("Failed to decode token as jwt", e);
      return null;
    }
  }

}
