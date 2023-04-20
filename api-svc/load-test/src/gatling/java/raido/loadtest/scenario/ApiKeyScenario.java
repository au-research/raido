package raido.loadtest.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.ApiKey;
import raido.idl.raidv2.model.GenerateApiTokenRequest;
import raido.idl.raidv2.model.GenerateApiTokenResponse;
import raido.loadtest.util.Gatling.Var;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.exec;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static java.lang.Long.parseLong;
import static java.util.List.of;
import static jodd.util.StringUtil.join;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.blankToDefault;
import static raido.db.jooq.api_svc.enums.IdProvider.RAIDO_API;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
import static raido.loadtest.config.ApiKey.bootstrapApiToken;
import static raido.loadtest.scenario.ServicePointScenario.I_SP_ID;
import static raido.loadtest.scenario.ServicePointScenario.I_SP_NAME;
import static raido.loadtest.util.Gatling.guard;
import static raido.loadtest.util.Gatling.sessionDebug;
import static raido.loadtest.util.Gatling.setOrRemove;
import static raido.loadtest.util.Json.formatJson;
import static raido.loadtest.util.Json.parseJson;
import static raido.loadtest.util.RaidoApi.API_KEY_SUBJECT;
import static raido.loadtest.util.RaidoApi.Endpoint.apiKey;
import static raido.loadtest.util.RaidoApi.Endpoint.generateToken;
import static raido.loadtest.util.RaidoApi.Endpoint.listApiKey;
import static raido.loadtest.util.RaidoApi.authzApiHeaders;

public class ApiKeyScenario {
  public static final String I_API_KEY_ID = "apiKeyId";
  public static final String I_API_TOKEN = "apiToken";

  private static final Log log = to(ApiKeyScenario.class);

  public static ScenarioBuilder prepareApiKeys(
    String servicePointFile,
    String apiKeyFile
  ) throws IOException {

    var loadTestSpListVar = new Var<>("loadTestServicePointList",
      new TypeReference<List<Map<String, Object>>>() {}) {};
    
    var iServicePointVar = new Var<>("servicePointRecord",
      new TypeReference<Map<String, Object>>() {}) {};
    
    var iListApiKeyVar = new Var<>("apiKeyList",
      new TypeReference<List<ApiKey>>() {}) {};

    var inFile = Paths.get(servicePointFile);
    var outFile = new File(apiKeyFile);

    // logged during scenario injection, not execution
    log.with("servicePointFile", inFile.toFile().getAbsolutePath()).
      with("apiKeyFile", outFile.getAbsoluteFile()).
      info("prepareApiKeys()");

    var apiKeyWriter = new PrintWriter(new FileWriter(outFile), true);
    //header row
    apiKeyWriter.println(join(of(
      I_SP_ID, I_SP_NAME, I_API_KEY_ID, I_API_TOKEN
    ), ","));
    apiKeyWriter.flush();

    String bootstrapApiToken = bootstrapApiToken();

    return scenario("prepare api-keys").exitBlockOnFail(
      exec(readServicePointsFromCsv(servicePointFile, loadTestSpListVar)).
      foreach(loadTestSpListVar::get, iServicePointVar.name).on(
        exec(listApiKeys(
          bootstrapApiToken, iServicePointVar, iListApiKeyVar)
        ).
        exec(
          searchForApiKeyOnServicePoint(
            I_API_KEY_ID, iListApiKeyVar, iServicePointVar, I_SP_ID
          )
        ).
        doIfOrElse(sess->!sess.contains(I_API_KEY_ID)).
          then(
            exec(createApiKey(bootstrapApiToken, iServicePointVar))
          ).
          orElse(exec(sessionDebug(sess->
            log.with(I_API_KEY_ID, sess.getString(I_API_KEY_ID)).
              info("api-key exists")))
          ).
        exec(
          generateApiToken(bootstrapApiToken)
        ).
        exec(sess->{
          var iRecord = iServicePointVar.get(sess);
          apiKeyWriter.println(join(of(
            blankToDefault(iRecord.get(I_SP_ID).toString(), ""),
            blankToDefault(iRecord.get(I_SP_NAME).toString(), ""),
            blankToDefault(sess.getString(I_API_KEY_ID), ""),
            blankToDefault(sess.getString(I_API_TOKEN), "")
          ), ","));
          apiKeyWriter.flush();
          return sess;
        })
      )
    );
  }

  private static Function<Session, Session> readServicePointsFromCsv(
    String servicePointFile,
    Var<List<Map<String, Object>>> loadTestSpListVar
  ) {
    return sess->{
      List<Map<String, Object>> records =
        csv(servicePointFile).readRecords();
      log.with("size", records.size()).info("service-points from csv");
      return loadTestSpListVar.set(sess, records);
    };
  }

  private static HttpRequestActionBuilder listApiKeys(
    String bootstrapApiToken,
    Var<Map<String, Object>> iServicePointVar,
    Var<List<ApiKey>> iListApiKeyVar
  ) {
    return http("list api-keys for service-point").
      get(sess->{
        var iRecord = iServicePointVar.get(sess);
        Long iSpId = parseLong(iRecord.get(I_SP_ID).toString());
        log.with(I_SP_ID, iSpId).
          with(I_SP_NAME, iRecord.get(I_SP_NAME)).
          info("prepare api-key for service-point");

        return listApiKey.url.formatted(iSpId);
      }).
      headers(authzApiHeaders(bootstrapApiToken)).
      check(status().is(200)).
      check(iListApiKeyVar.saveBody());
  }

  private static Function<Session, Session> searchForApiKeyOnServicePoint(
    String iApiKeyId,
    Var<List<ApiKey>> iListApiKeyVar,
    Var<Map<String, Object>> iServicePointVar,
    String servicePointVar
  ) {
    return sess->{
      var iApiKeys = iListApiKeyVar.get(sess);
      var iRecord = iServicePointVar.get(sess);
      var iSpId = parseLong(iRecord.get(servicePointVar).toString());
      log.with("servicePoint", iRecord).
        with("api-keys.size", iApiKeys.size()).
        info("search for existing api-key");
      return setOrRemove(sess, iApiKeyId,
        iApiKeys.stream().
          filter(jExistingApiKeyName->
            jExistingApiKeyName.getSubject().equals(
              formatApiKeySubject(iSpId))).
          map(ApiKey::getId).
          findFirst());
    };
  }

  private static HttpRequestActionBuilder createApiKey(
    String bootstrapApiToken,
    Var<Map<String, Object>> iServicePointVar
  ) {
    return http("create api-key for service-point").
      post(apiKey.url).
      headers(authzApiHeaders(bootstrapApiToken)).
      body(StringBody(sess->{
        var iRecord = iServicePointVar.get(sess);
        var iSpId = parseLong(iRecord.get(I_SP_ID).toString());
        log.with(iServicePointVar.name, iRecord).
          info("create api-key");
        return formatJson(
          new ApiKey().
            servicePointId(iSpId).
            subject(formatApiKeySubject(iSpId)).
            idProvider(RAIDO_API.getLiteral()).
            role(SP_ADMIN.getLiteral()).
            enabled(true));
      })).
      check(status().is(200)).
      check(
        bodyString().transform((body)->
            parseJson(body, ApiKey.class)
          ).
          transform(ApiKey::getId).
          validate(
            "created api-key has an id",
            guard(Guard::notNull)).
          saveAs(I_API_KEY_ID)
      );
  }

  private static HttpRequestActionBuilder generateApiToken(
    String bootstrapApiToken
  ) {
    return http("generate api-token for service-point").
      post(generateToken.url).
      headers(authzApiHeaders(bootstrapApiToken)).
      body(StringBody(sess->{
        var iApiKeyId = sess.getLong(I_API_KEY_ID);
        log.with(I_API_KEY_ID, iApiKeyId).
          info("generate api-token");
        return formatJson(
          new GenerateApiTokenRequest().
            apiKeyId(iApiKeyId));
      })).
      check(status().is(200)).
      check(
        bodyString().transform((body)->
            parseJson(body, GenerateApiTokenResponse.class)
          ).
          transform(GenerateApiTokenResponse::getApiToken).
          validate(
            "generated api-token is not null",
            guard(Guard::notNull)).
          saveAs(I_API_TOKEN)
      );
  }

  /*
  This is necessary because I messed up and put a unique index on subject in 
  the DB making these globally unique - tech debt, gotta go back and fix that. 
   */
  public static String formatApiKeySubject(long servicePointId) {
    return API_KEY_SUBJECT + "-" + servicePointId;
  }
}


/*
  public static final Var<List<LoadTestData>> loadTestVar =
    new Var<>("loadTestData",
      new TypeReference<List<LoadTestData>>(){}) {};

record LoadTestData(
  Long servicePointId,
  String servicePointName,
  Long apiKeyId,
  String apiToken
){}

 */
  
