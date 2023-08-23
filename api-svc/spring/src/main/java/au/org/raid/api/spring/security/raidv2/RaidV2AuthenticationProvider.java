package au.org.raid.api.spring.security.raidv2;

import au.org.raid.api.service.auth.RaidV2ApiKeyApiTokenService;
import au.org.raid.api.service.auth.RaidV2AppUserApiTokenService;
import au.org.raid.api.service.auth.RaidoClaim;
import au.org.raid.api.util.ExceptionUtil;
import au.org.raid.api.util.Log;
import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.StringUtil.mask;
import static au.org.raid.db.jooq.api_svc.enums.IdProvider.RAIDO_API;

public class RaidV2AuthenticationProvider implements AuthenticationProvider {
  private static final Log log = to(RaidV2AuthenticationProvider.class);

  private RaidV2AppUserApiTokenService userApiTokenSvc;
  private RaidV2ApiKeyApiTokenService apiKeyApiTokenSvc;

  public RaidV2AuthenticationProvider(
    RaidV2AppUserApiTokenService userApiTokenSvc, 
    RaidV2ApiKeyApiTokenService apiKeyApiTokenSvc
  ) {
    this.userApiTokenSvc = userApiTokenSvc;
    this.apiKeyApiTokenSvc = apiKeyApiTokenSvc;
  }

  /* IMPROVE: this needs unit tests that prove
      - doesn't accept random signature string
        (I nearly refactored this to use `decode()` instead of `verify`()`)
      - doesn't accept `algorithm:none`
   */
  @Override
  public Authentication authenticate(Authentication authentication) 
  throws AuthenticationException {

    if( !(authentication instanceof BearerTokenAuthenticationToken bearer) ){
      return null;
    }

    DecodedJWT jwt = null;
    try {
      jwt = JWT.decode(bearer.getToken());
    }
    catch( JWTDecodeException e ){
      log.with("error", e.getMessage()).
        with("token", mask(bearer.getToken())).
        info("bearer token could not be decoded");
      throw ExceptionUtil.authFailed();
    }

    // IMPROVE: add more checks that the pre-auth is the expected shape
    // that it's a V2 token, expiry, issuer, etc.

    /* could move this switch logic to authn resolver and have 3rd type of
     AuthProvider for api-keys.
     Note: we can't move the switch logic from the resolver to here, because
     we don't have access to the HttpRequest. */
    if( isApiKey(jwt) ){
      return apiKeyApiTokenSvc.verifyAndAuthorizeApiToken(jwt).orElse(null);
    }
    else {
      return userApiTokenSvc.verifyAndAuthorizeApiToken(jwt).orElse(null);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(
      BearerTokenAuthenticationToken.class );
  }

  public static boolean isApiKey(DecodedJWT jwt){
    return RAIDO_API.getLiteral().equals(
      jwt.getClaim(RaidoClaim.CLIENT_ID.getId()).asString() );
  }

  
}
