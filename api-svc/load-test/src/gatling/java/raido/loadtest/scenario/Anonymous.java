package raido.loadtest.scenario;

import io.gatling.javaapi.core.ScenarioBuilder;
import raido.apisvc.util.Log;
import raido.loadtest.util.RaidoApi.Endpoint;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.util.RaidoApi.Endpoint.publicStatus;

public class Anonymous {
  private static final Log log = to(Anonymous.class);
  public static final String STATUS_RESULT = "statusResult";

  public static ScenarioBuilder warmUp() {
    return scenario("Warm up").
      exec(http(publicStatus.url).
        get(publicStatus.url).
        // we don't check for status here, because we do it in the next exec()
        check(status().saveAs(STATUS_RESULT))
      ).
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
      });
      /* not sure why pausing, think I copied it from an example.
      OTOH, maybe a good idea to let the api-=svc warm up if it's just 
      starting? */
//      pause(5);
  }
}
