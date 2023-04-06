package raido.loadtest.simulation;

import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import raido.apisvc.util.Log;
import raido.loadtest.scenario.Anonymous;

import static io.gatling.javaapi.core.OpenInjectionStep.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.config.RaidoServerConfig.serverConfig;

/** Will include only "read-only" scenarios. */
public class Read extends Simulation {
  private static final Log log = to(Read.class);
  
  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);
  
  {
    setUp(
      Anonymous.publicStatus().injectOpen(atOnceUsers(1))
//      Anonymous.publicStatus().injectOpen(
//        rampUsers(simConfig.users).during(simConfig.rampUp),
//        constantUsersPerSec(simConfig.users).during(simConfig.steadyState)
//      )
    ).protocols(httpProtocol);
  }
  
}

