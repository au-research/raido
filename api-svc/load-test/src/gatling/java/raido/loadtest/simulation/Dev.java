package raido.loadtest.simulation;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingConfiguration;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.core.scenario.SimulationParams;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import raido.apisvc.util.Log;

import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.config.RaidoServerConfig.serverConfig;
import static raido.loadtest.scenario.Anonymous.warmUp;
import static raido.loadtest.scenario.SvcPoint.prepareServicePoints;

/** Intended for development testing, not a real load test */
public class Dev extends Simulation {
  private static final Log log = to(Dev.class);
  
  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);
  
  {
    log.info("initializer{}");
    
    setUp(
      warmUp().injectOpen(atOnceUsers(1)).andThen(
        prepareServicePoints().injectOpen(atOnceUsers(1))
      )
    ).protocols(httpProtocol);

  }

  @Override
  public SetUp setUp(List<PopulationBuilder> populationBuilders) {
    log.info("setup()");
    
    return super.setUp(populationBuilders);
  }


  /**
   Not intended for normal use.
   Intended for running from na IDE, in case of wanting to debug something.
   */
  public static void main(String... args) {
    var props = new GatlingPropertiesBuilder().
      simulationClass(Dev.class.getName()).
      runDescription("run from Dev.main()").
      /* By default, Gatling will dump a `results` directory in the 
      current working dir.  When running a `main()` method from an IDE, most 
      will default the CWD to either the repo root, or the sub-project root.  
      We don't want Gatling dumping stuff in a random `results` dir that 
      somebody will commit, so we tuck it under a `build` dir, which is already 
      being .gitignore'd at both the repo root and project levels. */
      resultsDirectory("build/main-load-test-results").
      /* don't need them for a debug session - breakpoints would make the timing
      results nonsensical anyway - use the gradle task if you want reports */
      noReports();

    var result = Gatling.fromMap(props.build());
    if( result != 0 ){
      log.with("result", result).warn("Gatling returned non-zero");
      System.exit(result);
    }
  }

  @Override
  public void before() {
    log.info("before()");
    super.before();
  }

  @Override
  public void after() {
    log.info("after()");
    super.after();
  }

  @Override
  public SimulationParams params(GatlingConfiguration configuration) {
    log.info("params");
    return super.params(configuration);
  }
  
}

