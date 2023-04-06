package raido.loadtest.simulation;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import raido.loadtest.scenario.Anonymous;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.loadtest.config.RaidoServerConfig.serverConfig;

/** Intended for development testing, not a real load test */
public class Dev extends Simulation { 

  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);
  
  {
    setUp(
      Anonymous.publicStatus().injectOpen(atOnceUsers(1))
//      Anonymous.publicStatus().injectOpen(
//        rampUsers(10).during(simConfig.rampUp),
//        constantUsersPerSec(10).during(simConfig.steadyState) 
//      )
    ).protocols(httpProtocol); 
  }
  
  /**
  Not intended for normal use.
  Intended for running from IDEA, in case of needing to debug. 
   */
  public static void main(String... args){
    var props = new GatlingPropertiesBuilder().
      simulationClass(Dev.class.getName());

    Gatling.fromMap(props.build());
  }
}

