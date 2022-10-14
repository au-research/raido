package raido.apisvc.spring.security.raidv2;


import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import raido.apisvc.service.auth.RaidoClaim;
import raido.apisvc.util.Log;

import java.util.Collection;
import java.util.Collections;

import static raido.apisvc.service.auth.RaidoClaim.IS_AUTHORIZED_APP_USER;
import static raido.apisvc.util.ExceptionUtil.iae;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.mask;
import static raido.db.jooq.api_svc.enums.IdProvider.RAIDO_API;

/**
 Final is intended to avoid sub-classing - there should be no need for a 
 subclass of a pre-auth token.  It's not much of a defence - if attacker can
 inject code, we've already lost - but might as well do it.
 */
public final class RaidV2PreAuthenticatedJsonWebToken 
implements Authentication {

  private static final Log log = 
    to(RaidV2PreAuthenticatedJsonWebToken.class);

  private final DecodedJWT token;

  RaidV2PreAuthenticatedJsonWebToken(DecodedJWT token) {
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

  public boolean isAuthorizedAppUser(){
    return token.getClaim(IS_AUTHORIZED_APP_USER.getId()).asBoolean();
  }
  
  public boolean isApiKey(){
    return RAIDO_API.getLiteral().equals(
      token.getClaim(RaidoClaim.CLIENT_ID.getId()).asString() );
  }
  
  @Override
  public void setAuthenticated(boolean isAuthenticated) 
  throws IllegalArgumentException {
    // implementation conforms to interface spec
    if( isAuthenticated ){
      throw iae("cannot set a pre-auth as authenticated");
    }
  }

  @Override
  public String getName() {
    return token.getSubject();
  }

  /**
   PreAuth token is "decoded", not "verified" - i.e. the signature is not 
   checked.
   */
  public static RaidV2PreAuthenticatedJsonWebToken decodeRaidV2Token(
    String token
  ) {
    if (token == null) {
      log.debug("No token was provided to build PreAuth token");
      return null;
    }
    
    try {
      DecodedJWT jwt = JWT.decode(token);
      return new RaidV2PreAuthenticatedJsonWebToken(jwt);
    } catch (JWTDecodeException e) {
      log.with("message", e.getMessage()).
        with("token", mask(token)).
        info("Failed to decode token as jwt");
      /* log the stack as debug, so it'll show in dev, but won't pollute 
      the logs in deployment because of users or attackers using bad tokens.
      Yes, debugging a deployed problem will require a log config change. */
      log.debug("JWT decode exception", e);
      return null;
    }
  }

}
