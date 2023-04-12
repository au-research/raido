package raido.loadtest.scenario;

import io.gatling.javaapi.core.ScenarioBuilder;
import raido.apisvc.util.Log;
import raido.apisvc.util.StringUtil;
import raido.loadtest.config.RaidoServerConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.listFeeder;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static java.util.Collections.singletonMap;
import static raido.apisvc.util.Log.to;

public class ServicePoint {
  public static final String SP_NAME = "servicePointName";
  public static final String SP_ID = "servicePointId";
  
  private static final Log log = to(ServicePoint.class);
  
  static int maxServicePoints = 2;

  static List<Map<String, Object>> servicePoints = IntStream.
    rangeClosed(1, maxServicePoints).
    mapToObj(i->"loadTest service point " + i).
    map(i-> singletonMap(SP_NAME, (Object) i)).
    toList();



  public static ScenarioBuilder loadServicePoint(RaidoServerConfig config) {
    return scenario("load service point").
      feed(listFeeder(servicePoints).circular()).
      exec(session->{
        log.with(SP_NAME, session.get(SP_NAME)).
          info("user servicePoint");
        return session;
      }).
      doIf(session->{
        String spId = session.getString(SP_ID);
        var exists = StringUtil.hasValue(spId);
        log.with("exists", exists).info("servicePoint id");

        return !exists;
      }).
      then(exec(http("create service point").get("/"))).
      exec(session->{
        http("load service point").
          get("/session=%s".formatted(session.get(SP_ID)));
        return session;
      });
  } 
  
  static void dostuff(){

  }

  // not sure how to use, might not be a very good idea
//  static Iterator<Map<String, Object>> infiniteServicePoints = IntStream.
//    iterate(0, i->i + 1).
//    mapToObj( i -> "loadTest service point " + i ).
//    map(i-> singletonMap(SP_NAME, (Object) i)).
//    iterator();


}
