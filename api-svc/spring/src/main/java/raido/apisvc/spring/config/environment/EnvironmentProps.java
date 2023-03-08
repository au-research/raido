package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import raido.apisvc.util.Log;

import static raido.apisvc.util.Log.to;

@Component
public class EnvironmentProps {
  private static final Log log = to(EnvironmentProps.class);

  /** avoid comparing the envName with a value, use isProd or make a proper
   config parameter for the feature you want to control. */
  @Value("${EnvironmentConfig.envName:unknown}")
  public String envName;

  @Value("${EnvironmentConfig.isProd:false}")
  public boolean isProd;

  @Value("${EnvironmentConfig.nodeId:unknown}")
  public String nodeId;

  @Value("${EnvironmentConfig.startTaskDelaySeconds:2}")
  public int startTaskDelaySeconds;

  
  @Value("${EnvironmentConfig.rootPathRedirect:https://www.raid.org.au}")
  public String rootPathRedirect;
  
  /* This has to be http for local testing to work in a real browser, but
  unexpectedly, setting this to `http` causes int tests to break.  
  
  But this needs to be http if you want it to work in your local browser.
  For the moment, set this in your api-svc-env.properties, but I need to work
  out how to override for just the int tests (or tell RestTemplate to not 
  follow redirects, but that's tricky).
  When this is fixed, remember to remove the doco from spring/readme.md about
  needing to override.
  
  There's an issue with following redirects when they use different protocols.
  So it turns out having this set to "https" makes the int tests work because 
  then the RestTemplate won't follow the redirect:  
  https://stackoverflow.com/a/1884427/924597  
  */
  @Value("${EnvironmentConfig.raidoLandingPage:" +
    "http://localhost:7080/handle" +
    "}")
  public String raidoLandingPage;

}
