package au.org.raid.api.spring.security.raidv1;

import au.org.raid.api.service.raidv1.RaidV1AuthService;
import au.org.raid.api.util.Log;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;

import static au.org.raid.api.util.Log.to;

public class RaidV1AuthenticationProvider implements AuthenticationProvider {
  private static final Log log = to(RaidV1AuthenticationProvider.class);
  
  private RaidV1AuthService raidSvc;

  public RaidV1AuthenticationProvider(RaidV1AuthService raidSvc) {
    this.raidSvc = raidSvc;
  }

  @Override
  public Authentication authenticate(Authentication authentication) 
  throws AuthenticationException {
    if( !(authentication instanceof BearerTokenAuthenticationToken bearer) ){
      return null;
    }

    return raidSvc.authenticate(bearer).orElse(null);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.isAssignableFrom(
      BearerTokenAuthenticationToken.class );
  }
}
