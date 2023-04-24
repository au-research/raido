package raido.loadtest.simulation;

import io.gatling.app.Gatling;
import io.gatling.core.config.GatlingConfiguration;
import io.gatling.core.config.GatlingPropertiesBuilder;
import io.gatling.core.scenario.SimulationParams;
import io.gatling.javaapi.core.PopulationBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import raido.apisvc.util.Log;

import java.io.IOException;
import java.util.List;

import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.config.GatlingRaidoServerConfig.serverConfig;
import static raido.loadtest.config.SimulationConfig.simConfig;
import static raido.loadtest.scenario.Anonymous.warmUp;
import static raido.loadtest.scenario.ApiKeyScenario.prepareApiKeys;
import static raido.loadtest.scenario.ServicePointScenario.prepareServicePoints;
import static raido.loadtest.scenario.User.listCreateViewRaid;

/** Intended for development testing, not a real load test */
public class Dev extends Simulation {
  private static final Log log = to(Dev.class);

  int maxServicePoints = 2;
  
  HttpProtocolBuilder httpProtocol = http.
    baseUrl(serverConfig.apiSvcUrl);
  
  {
    log.info("initializer{}");

    try {
      var spFile = simConfig.getDataPath("service-points.csv");
      var apiKeyFile = simConfig.getDataPath("api-keys.csv");
      
      setUp(
        warmUp().injectOpen(atOnceUsers(1)).
          andThen(
            prepareServicePoints(spFile, maxServicePoints).
              injectOpen(atOnceUsers(1))
          ).
          andThen(
            prepareApiKeys(spFile, apiKeyFile).
              injectOpen(atOnceUsers(1))
          ).
          andThen(
            /* this doesn't work, the CSV file will have no rows at injection 
            time because the prepare scenarios have not yet executed, but it
            seems feeders only read the data at injection time */
            listCreateViewRaid(apiKeyFile).
//            spUser("build/sto-api-keys.csv").
              injectOpen(atOnceUsers(3))
          )
      ).protocols(httpProtocol);
      
    }
    catch( IOException e ){
      throw new RuntimeException(e);
    }

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
    runSim(Dev.class, true);
  }

  public static void runSim(
    Class<? extends Simulation> simClass, 
    boolean reports
  ) {
    var props = new GatlingPropertiesBuilder().
      simulationClass(simClass.getName()).
      runDescription("run from main() via Dev.runSim()").
      /* By default, Gatling will dump a `results` directory in the 
      current working dir.  When running a `main()` method from an IDE, most 
      will default the CWD to either the repo root, or the sub-project root.  
      We don't want Gatling dumping stuff in a random `results` dir that 
      somebody will commit, so we tuck it under a `build` dir, which is already 
      being .gitignore'd at both the repo root and project levels. */
      resultsDirectory("build/main-load-test-results");
      
      if( !reports ){
        props = props.noReports();
      }

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

