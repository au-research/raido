package raido.apisvc.spring.security.raidv1;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import raido.apisvc.service.raidv1.RaidV1AuthService;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

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
