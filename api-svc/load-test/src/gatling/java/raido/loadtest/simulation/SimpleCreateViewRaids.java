package raido.loadtest.simulation;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import raido.apisvc.util.Log;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.config.GatlingRaidoServerConfig.serverConfig;
import static raido.loadtest.config.SimulationConfig.simConfig;
import static raido.loadtest.scenario.Anonymous.warmUp;
import static raido.loadtest.scenario.User.listCreateViewRaid;
import static raido.loadtest.simulation.Dev.runSim;
import static raido.loadtest.simulation.PrepareServicePoints.API_KEYS_PATH;

/** Intended for development testing, not a real load test */
public class SimpleCreateViewRaids extends Simulation {

  private static final Log log = to(SimpleCreateViewRaids.class);

  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);
  
  
  {
    setUp(
      warmUp().injectOpen(atOnceUsers(1)).
        andThen(
          listCreateViewRaid(API_KEYS_PATH).
//            injectOpen(atOnceUsers(1))
          injectOpen(
            rampUsers(simConfig.userCount).
              during(simConfig.rampUpSeconds),
            constantUsersPerSec(simConfig.userCount).
              during(simConfig.steadyStateSeconds)
          )
        )
    ).protocols(httpProtocol);
      
  }

  public static void main(String... args) {
    runSim(SimpleCreateViewRaids.class, true);
  }
  
}

