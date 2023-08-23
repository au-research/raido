package raido.loadtest.simulation;

import au.org.raid.api.util.Log;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.io.IOException;

import static au.org.raid.api.util.Log.to;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.loadtest.config.GatlingRaidoServerConfig.serverConfig;
import static raido.loadtest.config.SimulationConfig.simConfig;
import static raido.loadtest.scenario.Anonymous.warmUp;
import static raido.loadtest.scenario.ApiKeyScenario.prepareApiKeys;
import static raido.loadtest.scenario.ServicePointScenario.prepareServicePoints;
import static raido.loadtest.simulation.Dev.runSim;

/** Intended for development testing, not a real load test */
public class PrepareServicePoints extends Simulation {
  public static final String API_KEYS_FILENAME = "api-keys.csv";

  private static final Log log = to(PrepareServicePoints.class);

  int maxServicePoints = 40;
  
  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);

  {
    var servicePointsPath = simConfig.getDataPath("service-points.csv");
    var apiKeysPath = simConfig.getDataPath(API_KEYS_FILENAME);
    
    try {
      
      setUp(
        warmUp().injectOpen(atOnceUsers(1)).
          andThen(
            prepareServicePoints(servicePointsPath, maxServicePoints).
              injectOpen(atOnceUsers(1))
          ).
          andThen(
            prepareApiKeys(servicePointsPath, apiKeysPath).
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

