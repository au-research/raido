package raido.loadtest.simulation;

import au.org.raid.api.util.Log;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static au.org.raid.api.util.Log.to;
import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.loadtest.config.GatlingRaidoServerConfig.serverConfig;
import static raido.loadtest.config.SimulationConfig.simConfig;
import static raido.loadtest.scenario.Anonymous.warmUp;
import static raido.loadtest.scenario.User.listCreateViewRaid;
import static raido.loadtest.simulation.Dev.runSim;
import static raido.loadtest.simulation.PrepareServicePoints.API_KEYS_FILENAME;

/** Intended for development testing, not a real load test */
public class SimpleCreateViewRaids extends Simulation {

  private static final Log log = to(SimpleCreateViewRaids.class);

  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);
  
  
  {
    setUp(
      warmUp().injectOpen(atOnceUsers(1)).
        andThen(
          listCreateViewRaid(simConfig.getDataPath(API_KEYS_FILENAME)).
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
              during(simConfig.steadyStateSeconds).
              /* distribute the new users randomly across the whole second, 
              otherwise gatling will inject all userCount users at exactly the 
              same instant. */
              randomized()

            /* Should throw a `.randomized()` in here too, otherwise all the 
            users get chunked into the test at exactly the same time, which 
            makes the test easier to reason about, but less realistic. */
        )
      )
    ).protocols(httpProtocol);
      
  }

  public static void main(String... args) {
    runSim(SimpleCreateViewRaids.class, true);
  }
  
}

