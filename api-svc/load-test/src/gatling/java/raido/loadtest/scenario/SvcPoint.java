package raido.loadtest.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.PublicServicePoint;
import raido.idl.raidv2.model.ServicePoint;
import raido.loadtest.util.Gatling;
import raido.loadtest.util.Gatling.Var;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static raido.apisvc.util.Guard.isTrue;
import static raido.apisvc.util.Guard.notNull;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.config.ApiKey.bootstrapApiToken;
import static raido.loadtest.util.Gatling.guard;
import static raido.loadtest.util.Gatling.sessionDebug;
import static raido.loadtest.util.Gatling.setOrRemove;
import static raido.loadtest.util.Json.formatJson;
import static raido.loadtest.util.Json.parseJson;
import static raido.loadtest.util.RaidoApi.Endpoint.listServicePoint;
import static raido.loadtest.util.RaidoApi.Endpoint.servicePoint;
import static raido.loadtest.util.RaidoApi.authzApiHeaders;

// used Svc to avoid class with IDL class
public class SvcPoint {

  public static final Var<List<PublicServicePoint>> existingServicePointsVar =
    new Var<>("existingServicePoints", 
      new TypeReference<List<PublicServicePoint>>(){}) {};
  
  public static final String I_SP_NAME = "iSpName";
  public static final String I_SP_ID = "iSpId";

  private static final Log log = to(SvcPoint.class);

  //  static int maxServicePoints = 40;
  static int maxServicePoints = 3;

  static List<String> servicePointNames = IntStream.
    rangeClosed(1, maxServicePoints).
    mapToObj(i->"loadTest service point " + i).
    toList();

  public static Function<Session, Session> convertSessionStringToType(
    String varName,
    TypeReference<?> type
  ){
    return (Session sess)-> sess.set(
      varName,
      parseJson(sess.getString(varName), type) );
  }
  
  public static ScenarioBuilder prepareServicePoints() {
    log.with("servicePointNames", servicePointNames).
      info("loadServicePoint()");
    String bootstrapApiToken = bootstrapApiToken();

    return scenario("load service points").exitBlockOnFail(
      exec(
        http("list existing service points").
          get(listServicePoint.url).
          check(status().is(200)).
          check(existingServicePointsVar.saveBody())
      ).
      foreach(servicePointNames, I_SP_NAME).on(
        exec(sess->{
          String iSpName = sess.getString(I_SP_NAME);
          log.with(I_SP_NAME, iSpName).
            info("preparing service-points for load testing");
          
          List<PublicServicePoint> existingServicePoints = 
            existingServicePointsVar.get(sess);
          notNull("existingServicePoints variable", existingServicePoints);

          return setOrRemove(sess, I_SP_ID, 
            existingServicePoints.stream().
              filter(jExistingSpName->jExistingSpName.getName().equals(iSpName)).
              map(PublicServicePoint::getId).
              findFirst());
        }).
          doIfOrElse(sess->!sess.contains(I_SP_ID)).
            then(
              exec(sessionDebug(sess-> 
                log.with(I_SP_NAME, sess.getString(I_SP_NAME)).
                  info("create service-point") )).
              exec(http("create service-point").
                post(servicePoint.url).
                headers(authzApiHeaders(bootstrapApiToken)).
                body(StringBody(sess-> formatJson(
                  new ServicePoint().
                    name(sess.getString(I_SP_NAME)).
                    identifierOwner(Gatling.LOAD_TEST_ROR).
                    adminEmail("").techEmail("").enabled(true) ))).
                check(status().is(200)).
                check(
                  bodyString().transform((body) ->
                      parseJson(body, ServicePoint.class)
                    ).
                    validate("created service-point has an id ", 
                      guard(i->notNull(i.getId())) 
                    )
                )
              )
            ).orElse( exec(sessionDebug(sess->
              log.with(I_SP_ID, sess.getString(I_SP_ID)).
                info("service-point exists") ))).
          exec(sessionDebug(sess->
            log.with(I_SP_ID, sess.getString(I_SP_ID)).
              with("sp", sess.getString("servicePoint")).
              info("prepare api-key for service-point") ))
      ));
  }
}

