package raido.loadtest.scenario;

import io.gatling.javaapi.core.ScenarioBuilder;
import raido.apisvc.util.Log;
import raido.loadtest.util.RaidoApi.Endpoint;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.util.RaidoApi.Endpoint.publicListServicePoint;
import static raido.loadtest.util.RaidoApi.Endpoint.publicStatus;

public class Anonymous {
  private static final Log log = to(Anonymous.class);
  public static final String STATUS_RESULT = "statusResult";

  public static ScenarioBuilder warmUp() {
    return scenario("Warm up").
      /* Intent:
       - confirm the load-test is even going to run
       - warm up load-test side network tools (DNS request, other network stuff)
       - warm up api-svc side network tools (anything Spring/Jetty does lazy)
       
       Note that any warmups done via a network request like this only work on 
       a single api-svc node - only the node that handles the request gets 
       warmed up.
       Example: in a multi-node scenario, one node would get the 
       /public/Status request, and probably a different node would get the 
       DB warmup request - leaving the node cluster in an even *less* consistent
       state 😵
       */
      exec(http("check %s is reachable".formatted(publicStatus.url)).
        get(publicStatus.url).
        // we don't check for status here, because we do it in the next exec()
        check(status().saveAs(STATUS_RESULT))
      ).
      // fail early if the load-test isn't going to work anyway
      exec(sess->{
        if( !sess.contains(STATUS_RESULT) ){
          log.with("reason", "session not found").
            with("url", publicStatus.url).
            error("warmup scenario cannot reach the system, exiting");
          System.exit(1);
        }
        
        var mode = sess.getInt(STATUS_RESULT);
        log.with(STATUS_RESULT, mode).info("warmup");

        if( mode != 200){
          log.with("reason", "non-200 result").
            with("url", publicStatus.url).
            error("warmup scenario cannot reach the system, exiting");
        }

        return sess;
      }).
      /* warm up the api-svc database stuff (jooq, connection-pool, DNS and
       other networking stuff for connecting to DB) */
      exec(http("check %s is reachable".formatted(publicListServicePoint.url)).
        get(publicListServicePoint.url).
        // we don't check for status here, because we do it in the next exec()
          check(status().is(200))
      );
      /* IMPROVE: warm up other stuff like:
      - network stuff for connections to APIDs, ORCID, ROR, etc.
        (could/should be done in the StartupListener on server-side).         
       */
  }
}
