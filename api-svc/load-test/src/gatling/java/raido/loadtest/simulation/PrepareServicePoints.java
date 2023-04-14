package raido.loadtest.simulation;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import raido.apisvc.util.Log;

import java.io.IOException;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.config.GatlingRaidoServerConfig.serverConfig;
import static raido.loadtest.scenario.Anonymous.warmUp;
import static raido.loadtest.scenario.ApiKeyScenario.prepareApiKeys;
import static raido.loadtest.scenario.ServicePointScenario.prepareServicePoints;
import static raido.loadtest.simulation.Dev.runSim;

/** Intended for development testing, not a real load test */
public class PrepareServicePoints extends Simulation {
  public static final String SERVICE_POINTS_PATH = "build/service-points.csv";
  public static final String API_KEYS_PATH = "build/api-keys.csv";

  private static final Log log = to(PrepareServicePoints.class);

  int maxServicePoints = 2;
  
  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);
  
  {
    try {
      
      setUp(
        warmUp().injectOpen(atOnceUsers(1)).
          andThen(
            prepareServicePoints(SERVICE_POINTS_PATH, maxServicePoints).
              injectOpen(atOnceUsers(1))
          ).
          andThen(
            prepareApiKeys(SERVICE_POINTS_PATH, API_KEYS_PATH).
              injectOpen(atOnceUsers(1))
          )
      ).protocols(httpProtocol);
      
    }
    catch( IOException e ){
      throw new RuntimeException(e);
    }

  }

  public static void main(String... args) {
    runSim(PrepareServicePoints.class, true);
  }
}

