package raido.apisvc.spring.config.environment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvironmentProps {
  @Value("${EnvironmentConfig.envName:unknown}")
  public String envName;

  @Value("${EnvironmentConfig.nodeId:unknown}")
  public String nodeId;

  @Value("${EnvironmentConfig.startTaskDelaySeconds:2}")
  public int startTaskDelaySeconds;

  // probably not the right location for this, it'll do for now
  /** the "front door" (expected to be CloudFront or possibly even CloudFlare)
   for the raid landing page.
   Controls the url that the global handle registry will redirect to (that's
   why it uses the front door, instead of referencing the API). 
   The default value is for local dev, it's the the node process serving the 
   app-client via running `npm run start`, so that local testing works.
   demo is set to `https://demo.raido-infra.com/raid`.
   */
  @Value("${EnvironmentConfig.raidLandingPrefix:" +
    "http://localhost:7080/handle}")
  public String raidLandingPrefix;
}
