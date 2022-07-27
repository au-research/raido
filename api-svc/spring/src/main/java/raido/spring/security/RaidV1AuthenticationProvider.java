package raido.spring.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import raido.spring.security.jwt.Raid1PreAuthenticatedJsonWebToken;
import raido.util.Log;

import static raido.util.Log.to;

public class RaidV1AuthenticationProvider implements AuthenticationProvider {
  private static final Log log = to(RaidV1AuthenticationProvider.class);
  
  @Override
  public Authentication authenticate(Authentication authentication) 
  throws AuthenticationException {
    log.info("authenticate");
    return null;
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return authentication.equals(Raid1PreAuthenticatedJsonWebToken.class);
  }
}
