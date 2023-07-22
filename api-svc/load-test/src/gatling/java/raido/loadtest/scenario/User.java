package raido.loadtest.scenario;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Session;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.*;
import raido.loadtest.util.Gatling.Var;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;
import static raido.apisvc.util.Log.to;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.CONTACT_PERSON;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.OTHER_PARTICIPANT;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.SOFTWARE;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.SUPERVISION;
import static raido.idl.raidv2.model.DescriptionType1.ALTERNATIVE_DESCRIPTION;
import static raido.loadtest.config.SimulationConfig.simConfig;
import static raido.loadtest.scenario.ApiKeyScenario.I_API_TOKEN;
import static raido.loadtest.scenario.ServicePointScenario.I_SP_ID;
import static raido.loadtest.util.Gatling.sessionDebug;
import static raido.loadtest.util.Json.formatJson;
import static raido.loadtest.util.Json.parseJson;
import static raido.loadtest.util.RaidoApi.Endpoint.*;
import static raido.loadtest.util.RaidoApi.authzApiHeaders;
import static raido.loadtest.util.RaidoMetadata.*;

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

    var today = LocalDate.now();

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
    var viewMintedRaidPause = simConfig.thinkForSeconds(2);
    // they're *really* fast typists 🤔
    var editRaidPause = simConfig.thinkForSeconds(2);

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
      pause(viewMintedRaidPause).

      // user goes back to home page
      exec(listRaids(I_API_TOKEN, I_SP_ID, raidListVar)).
      pause(listPagePause).

      /* user decides to edit the raid they just created.
      We can't just pick the "most recent" raid - there might be other users 
      who have minted a raid while the user was thinking. 
      IMPROVE: For the moment the test is just going to cheat by using what was 
      returned by the mint operation.  Really, we should scan the recent raid 
      list looking for a raid with the title that we minted. */
      exec(readRaid(I_API_TOKEN, mintedRaidVar, readRaidVar)).
      pause(editRaidPause).
      exec(sess->{
        RaidoMetadataSchemaV1 readRaid = readRaidVar.get(sess);
        /* note: this is editing the raid in place from the session, so we 
         have to refresh from the read endpoint to really know what's been stored
         in the system.
         Not sure if this is going to cause problems with Gatling, doing this 
         *really* undermines to whole concept of immutable session state. */
        readRaid.getDescriptions().add( new DescriptionBlock().
          type(ALTERNATIVE_DESCRIPTION).
          description("add contributor 2") );
        /* IMPROVE: use a different orcid so we don't trip over any 
         optimisations or validations that get added to the system. */
        readRaid.getContributors().add(createContributor(
          RAID_PRODUCT_MANAGER, CONTACT_PERSON, SUPERVISION, today ));
        return sess;
      }).
      exec(updateRaid("add contributor 2", I_API_TOKEN, readRaidVar)).
      pause(viewMintedRaidPause).

      // user goes back to home page, but then decides to add third contributor
      exec(listRaids(I_API_TOKEN, I_SP_ID, raidListVar)).
      pause(listPagePause).
      exec(readRaid(I_API_TOKEN, mintedRaidVar, readRaidVar)).
      pause(editRaidPause).
      exec(sess->{
        RaidoMetadataSchemaV1 readRaid = readRaidVar.get(sess);
        readRaid.getDescriptions().add( new DescriptionBlock().
          type(ALTERNATIVE_DESCRIPTION).
          description("add contributor 3") );
        readRaid.getContributors().add(createContributor(
          RAID_PRODUCT_MANAGER, OTHER_PARTICIPANT, SOFTWARE, today ));
        return sess;
      }).
      exec(updateRaid("add contributor 3", I_API_TOKEN, readRaidVar)).
      pause(viewMintedRaidPause)
    ;

  }

  private static HttpRequestActionBuilder updateRaid(
    String description,
    String apiTokenVar,
    Var<RaidoMetadataSchemaV1> in_updateRaidVar
  ) {
    return authzApiHeaders( apiTokenVar,
      http("update raid - " + description).
        post(updateRaid.url).
        body(StringBody(sess->{
          RaidoMetadataSchemaV1 raidMetadata = in_updateRaidVar.get(sess);
          return formatJson(Map.of("metadata", raidMetadata));
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
            })
        )
    );
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