package raido.loadtest.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.PublicServicePoint;
import raido.idl.raidv2.model.ServicePoint;
import raido.loadtest.util.Gatling.Var;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static java.util.List.of;
import static jodd.util.StringUtil.join;
import static raido.apisvc.util.Guard.notNull;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.first;
import static raido.apisvc.util.StringUtil.blankToDefault;
import static raido.loadtest.config.ApiKey.bootstrapApiToken;
import static raido.loadtest.util.Gatling.guard;
import static raido.loadtest.util.Gatling.sessionDebug;
import static raido.loadtest.util.Gatling.setOrRemove;
import static raido.loadtest.util.Json.formatJson;
import static raido.loadtest.util.Json.parseJson;
import static raido.loadtest.util.RaidoApi.Endpoint.publicListServicePoint;
import static raido.loadtest.util.RaidoApi.Endpoint.servicePoint;
import static raido.loadtest.util.RaidoApi.ROR_ID;
import static raido.loadtest.util.RaidoApi.authzApiHeaders;

public class ServicePointScenario {
  public static final String I_SP_NAME = "servicePointName";
  public static final String I_SP_ID = "servicePointId";

  private static final Log log = to(ServicePointScenario.class);

  public static ScenarioBuilder prepareServicePoints(
    Path servicePointFilePath,
    int maxServicePoints
  ) throws IOException {
    var outFile = servicePointFilePath.toFile();

    var existingServicePointsVar = new Var<>("existingServicePoints",
      new TypeReference<List<PublicServicePoint>>(){}) {};
    
    List<String> servicePointNames = IntStream.
      rangeClosed(1, maxServicePoints).
      mapToObj(i->"loadTest service point " + i).
      toList();
    
    // logged during scenario creation, not execution
    log.with("servicePointFile", outFile).
      info("prepareServicePoints()");

    var servicePointWriter = 
      new PrintWriter(new FileWriter(outFile), true);
    //header row
    servicePointWriter.println(join(of(I_SP_ID, I_SP_NAME), ","));
    servicePointWriter.flush();
    
    String bootstrapApiToken = bootstrapApiToken();

    return scenario("prepare service-points").exitBlockOnFail(
      exec(sessionDebug(sess->
        log.with("size", servicePointNames.size()).
          with("maxServicePoints", maxServicePoints).
          with("servicePointNames(first 5)", first(servicePointNames, 5)).
          info("prepareServicePoints()"))
      ).
      exec(
        http("list existing service points").
          get(publicListServicePoint.url).
          check(status().is(200)).
          check(existingServicePointsVar.saveBody())
      ).
      foreach(servicePointNames, I_SP_NAME).on(
        exec(searchForExistingServicePoint(existingServicePointsVar)).
        doIfOrElse(sess->!sess.contains(I_SP_ID)).
          then(
            exec(createServicePoint(bootstrapApiToken))
          ).
          orElse( exec(sessionDebug(sess->
            log.with(I_SP_ID, sess.getString(I_SP_ID)).
              info("service-point exists") 
          ))).
        exec(sess->{
          servicePointWriter.println(join(of(
            blankToDefault(sess.getString(I_SP_ID), ""), 
            blankToDefault(sess.getString(I_SP_NAME), "") 
          ), "," ));
          servicePointWriter.flush();
          return sess;
        })
      )
    );
  }

  private static HttpRequestActionBuilder createServicePoint(
    String bootstrapApiToken
  ) {
    return http("create service-point").
      post(servicePoint.url).
      headers(authzApiHeaders(bootstrapApiToken)).
      body(StringBody(sess->{
        log.with(I_SP_NAME, sess.getString(I_SP_NAME)).
          info("create service-point");
        return formatJson(
          new ServicePoint().
            name(sess.getString(I_SP_NAME)).
            identifierOwner(ROR_ID).
            adminEmail("").techEmail("").
            appWritesEnabled(true).
            enabled(true));
      })).
      check(status().is(200)).
      check(
        bodyString().transform((body)->
            parseJson(body, ServicePoint.class)
          ).
          transform(ServicePoint::getId).
          validate(
            "created service-point has an id",
            guard(Guard::notNull)).
          saveAs(I_SP_ID)
      );
  }

  private static Function<Session, Session> searchForExistingServicePoint(
    Var<List<PublicServicePoint>> existingServicePointsVar
  ) {
    return sess->{
      String iSpName = sess.getString(I_SP_NAME);
      log.with(I_SP_NAME, iSpName).
        info("preparing service-point for load testing");

      List<PublicServicePoint> existingServicePoints =
        existingServicePointsVar.get(sess);
      notNull("existingServicePoints variable", existingServicePoints);

      return setOrRemove(sess, I_SP_ID,
        existingServicePoints.stream().
          filter(jExistingSpName->jExistingSpName.getName().equals(iSpName)).
          map(PublicServicePoint::getId).
          findFirst());
    };
  }

}

