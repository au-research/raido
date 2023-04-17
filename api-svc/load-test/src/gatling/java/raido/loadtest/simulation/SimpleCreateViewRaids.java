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
            /* "X users added *per-second* for Y seconds" 
            Example: Set to 1, and steadyState at 20 seconds, that's 20 raids 
            minted, 1 per second. 
            After each raid is minted (which itself takes 4 seconds), then all 
            the think time and other queries execute in parallel for each user 
            after that.
            So you actually end up with at least 3 raids being minted 
            simultaneously, offset by one second each.
            Overall, given minting time and think time, you end up with about
            14 simultaneous "virtual users" "using" the system.
            A total of 85 Requests Per Second, an avg 2.5 RPS, with a peak at 
            about 4 RPS (because of initial rampUp phase).  
            */
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

