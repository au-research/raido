package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RaidoAuthnProps {

  /** This the location of the endpoint where Raido does OIDC responses.
   It's the Raido location where the IDP redirects the browser to after they 
   user has approved authentication.
   IMPROVE: Should be able to infer this based on local knowledge of the 
   server/port we're running on instead of as a config item?
   */
  @Value("${RaidoAuthn.serverRedirectUri:http://localhost:8080/idpresponse}")
  public String serverRedirectUri;

  /**
   These are the urls that a client is allowed pass in the state param.
   It's the place that the /idpresponse url  will redirect the client to after 
   successful authentication. 
   */
  @Value("${RaidoAuthn.allowedRedirectUrls:" +
    "['https://demo.raido-infra.com', 'http://localhost:7080']}")
  public List<String> allowedClientRedirectUrls;


}
