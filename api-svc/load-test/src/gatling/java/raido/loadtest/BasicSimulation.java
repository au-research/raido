package raido.loadtest;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;

public class BasicSimulation extends Simulation { 

  RaidoConfig config = new RaidoConfig();
  
  HttpProtocolBuilder httpProtocol = http.
    baseUrl(config.apiSvcUrl);
  
  ScenarioBuilder scn = scenario("BasicSimulation").
    exec(http("status").
      get("/public/status")).
    pause(5); 

  {
    setUp( 
      scn.injectOpen(atOnceUsers(1))
    ).protocols(httpProtocol); 
  }
}

