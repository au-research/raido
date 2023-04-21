package raido.loadtest.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.MintRaidoSchemaV1Request;
import raido.idl.raidv2.model.MintRaidoSchemaV1RequestMintRequest;
import raido.idl.raidv2.model.MintResponse;
import raido.idl.raidv2.model.RaidListItemV2;
import raido.idl.raidv2.model.RaidListRequestV2;
import raido.idl.raidv2.model.RaidoMetadataSchemaV1;
import raido.idl.raidv2.model.ReadRaidResponseV2;
import raido.idl.raidv2.model.ReadRaidV2Request;
import raido.loadtest.util.Gatling.Var;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static io.gatling.javaapi.core.CoreDsl.csv;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static java.time.Duration.ofSeconds;
import static raido.apisvc.util.Log.to;
import static raido.loadtest.config.SimulationConfig.simConfig;
import static raido.loadtest.scenario.ApiKeyScenario.I_API_TOKEN;
import static raido.loadtest.scenario.ServicePointScenario.I_SP_ID;
import static raido.loadtest.util.Gatling.sessionDebug;
import static raido.loadtest.util.Json.formatJson;
import static raido.loadtest.util.Json.parseJson;
import static raido.loadtest.util.RaidoApi.Endpoint.listRaids;
import static raido.loadtest.util.RaidoApi.Endpoint.mintRaid;
import static raido.loadtest.util.RaidoApi.Endpoint.readRaid;
import static raido.loadtest.util.RaidoApi.authzApiHeaders;
import static raido.loadtest.util.RaidoMetadata.createRaidoMetadata;

public class User {
  private static final Log log = to(User.class);
  
  /**
   apiKey file must be a CSV file with columns:
   servicePointId,servicePointName,apiKeyId,apiToken
   
   Usually expected to have been created by the {@link ApiKeyScenario}, but 
   you can drive it off any other CSV file you want, as long as it has those 
   columns.
   */
  public static ScenarioBuilder listCreateViewRaid(
    Path apiKeyPath
  ) {
    File file = apiKeyPath.toFile();
    log.with("apiKeyFile", apiKeyPath).
      with("apiKeyFile.canRead", file.canRead()).
      info("listCreateViewRaid()");

    var raidListVar = new Var<>("raidList",
      new TypeReference<List<RaidListItemV2>>(){}) {};
    var mintedRaidVar = new Var<>("mintedRaid",
      new TypeReference<RaidoMetadataSchemaV1>(){}) {};
    var readRaidVar = new Var<>("readRaid",
      new TypeReference<RaidoMetadataSchemaV1>(){}) {};

    /* Not very realistic - most users would take longer most of the time.
    But sometimes users actually go even faster, e.g. an experienced user 
    that already knows they're definitely going to mint a raid and they 
    have memorised the location of the mint button will only on the "home" 
    page for as long as it takes them to locate and click the button. */
    var listPagePause = simConfig.thinkForSeconds(2);
    var mintPagePause = simConfig.thinkForSeconds(2);
    var editPagePause = simConfig.thinkForSeconds(2);

    /* IMPROVE: scenario should use stable API, not experimental */
    return scenario("SP_USER").
      feed(csv(apiKeyPath.toString()).circular()).
      
      // user logs in to system, which calls API to show latest raids 
      exec(listRaids(I_API_TOKEN, I_SP_ID, raidListVar)).
      // user takes time to read and comprehend the page 
      pause(listPagePause).
      
      // IMPROVE: need to simulate whatever "reference data" loading is done 
      // on mint page by the client
      
      // user takes time to fill out all the data fields   
      pause(mintPagePause).  

      // then they click mint, then client calls the API
      exec(mintRaid(I_API_TOKEN, I_SP_ID, mintedRaidVar)).
      // the users takes time to view the result of the create
      pause(editPagePause).

      // user goes back to home page
      exec(listRaids(I_API_TOKEN, I_SP_ID, raidListVar)).
      pause(listPagePause).
      
      // user decides to view the raid they created again, for some reason
      exec(readRaid(I_API_TOKEN, mintedRaidVar, readRaidVar)).
      pause(editPagePause)
      
      // implement 3 "edit" operations, so the overall scenario is
      // 3 "edit raid" for each "mint raid".  More realistic and will expose
      // issues with edit holding external connections open.
      
    ;

  }

  private static HttpRequestActionBuilder listRaids(
    String apiTokenVar,
    String in_svcPointIdVar,
    Var<List<RaidListItemV2>> out_raidListVar
  ) {
    return authzApiHeaders( apiTokenVar,
      http("list raids for service-point").
        post(listRaids.url).
        body(StringBody(sess->formatJson(new RaidListRequestV2().
          servicePointId(sess.getLong(in_svcPointIdVar))
        ))).
        check(status().is(200)).
        check(out_raidListVar.saveBody())
    );
  }

  private static HttpRequestActionBuilder readRaid(
    String apiTokenVar,
    Var<RaidoMetadataSchemaV1> in_mintedRaidVar,
    Var<RaidoMetadataSchemaV1> out_readRaidVar
  ) {
    return authzApiHeaders( apiTokenVar,
      http("read raid data").
        post(readRaid.url).
        body(StringBody(sess->{
          var id = new IdentifierParser().
            parseUrlWithRuntimeException(
              in_mintedRaidVar.get(sess).getId().getIdentifier() );
          return formatJson(new ReadRaidV2Request().
            handle( id.handle().format() )
          );
        })).
        check(status().is(200)).
        check(
          bodyString().transform((body)->
              parseJson(body, ReadRaidResponseV2.class)
            ).
            transform(result->parseJson( 
              (String) result.getMetadata(), 
              RaidoMetadataSchemaV1.class) ).
            saveAs(out_readRaidVar.name)
        )    
    );
  }

  private static HttpRequestActionBuilder mintRaid(
    String apiTokenVar,
    String in_svcPointIdVar,
    Var<RaidoMetadataSchemaV1> out_mintedRaidVar
  ) {
    return authzApiHeaders( apiTokenVar,
      http("mint raid").
        post(mintRaid.url).
        body(StringBody(sess->{
          long spId = sess.getLong(in_svcPointIdVar);
          String body = formatJson(new MintRaidoSchemaV1Request().
            mintRequest(new MintRaidoSchemaV1RequestMintRequest().
              servicePointId(spId)
            ).
            metadata(createRaidoMetadata())
          );
          log.with("servicePointId", spId).with("body", body).
            debug("minting raid");
          return body;
        })).
        check(status().is(200)).
        check(
          bodyString().transform((body)->
              parseJson(body, MintResponse.class)
            ).
            transform(result->{
              if( !result.getSuccess() ){
                log.with("failures", result.getFailures()).error("mint failed");
                throw new ValidationException(result.getFailures());
              }
              return parseJson( 
                (String) result.getRaid().getMetadata(), 
                RaidoMetadataSchemaV1.class);
            }).
            saveAs(out_mintedRaidVar.name) 
        )
    );
  }
  
  
  /* note: executes for each virtual user, it's just a tool to prove that 
  the data does actually exist in the file at execution time. */
  public static Function<Session, Session> debugCsvContent(
    String absolutePath, String columnName
  ) {
    return sessionDebug(sess->{
      var records = csv(absolutePath).readRecords();
      log.with("size", records.size()).
        with("records", records.stream().map(i->i.get(columnName)).toList()).
        info("csv input");
    });
  }

}
