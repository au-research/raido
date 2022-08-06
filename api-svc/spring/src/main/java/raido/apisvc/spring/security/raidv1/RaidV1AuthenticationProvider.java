package raido.apisvc.spring.security.raidv1;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

    if( isRaid1PreAuth(authentication.getClass()) ){
      return raidSvc.authenticate(
        (RaidV1PreAuthenticatedJsonWebToken) authentication).orElse(null);
    }
    
    
    /* Do not log the details of the object.  
     Since we we don't know what it is we certainly don't know if 
     it might print out sensitive info. */
    log.with("class", authentication.getClass().getName()).
      warn("AuthProvider can't handle");
    return null;
  }

  private boolean isRaid1PreAuth(Class<?> authentication){
    return authentication.equals(RaidV1PreAuthenticatedJsonWebToken.class);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return isRaid1PreAuth(authentication);
  }
}
