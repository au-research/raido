package raido.loadtest.scenario;

import io.gatling.javaapi.core.ScenarioBuilder;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.apisvc.endpoint.anonymous.PublicEndpoint.STATUS_PATH;

public class Anonymous {
  public static ScenarioBuilder publicStatus() {
    return scenario("BasicSimulation").
      exec(http("status").
        get(STATUS_PATH)).
      pause(5);
  }
}
