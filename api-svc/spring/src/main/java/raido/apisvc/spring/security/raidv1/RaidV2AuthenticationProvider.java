package raido.apisvc.spring.security.raidv1;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import raido.apisvc.service.auth.RaidV2AuthService;
import raido.apisvc.spring.security.raidv2.RaidV2PreAuthenticatedJsonWebToken;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

/**
 IMPROVE: I don't think we need multiple types of pre-auth objects.
 Just have one and share it for
 - raidV1 API token
 - raidV2 API token
 - RaidV2 non-authz user token
 - RaidV2 authz user token
 
 And have this class be the AuthProvider that does the multi-plexing.
 
 Do keep using separate post-auth objects though, it's good to have those 
 explicitly stated requirements around what fields each type of token will have.
 */
public class RaidV2AuthenticationProvider implements AuthenticationProvider {
  private static final Log log = to(RaidV2AuthenticationProvider.class);
  
  private RaidV2AuthService raidSvc;

  public RaidV2AuthenticationProvider(RaidV2AuthService raidSvc) {
    this.raidSvc = raidSvc;
  }

  @Override
  public Authentication authenticate(Authentication authentication) 
  throws AuthenticationException {

    if( isRaid2PreAuth(authentication.getClass()) ){
      return raidSvc.authorize(
        (RaidV2PreAuthenticatedJsonWebToken) authentication).orElse(null);
    }
    
    
    /* Do not log the details of the object.  
     Since we we don't know what it is we certainly don't know if 
     it might print out sensitive info. */
    log.with("class", authentication.getClass().getName()).
      warn("AuthProvider can't handle");
    return null;
  }

  private boolean isRaid2PreAuth(Class<?> authentication){
    return authentication.equals(RaidV2PreAuthenticatedJsonWebToken.class);
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return isRaid2PreAuth(authentication);
  }
}
