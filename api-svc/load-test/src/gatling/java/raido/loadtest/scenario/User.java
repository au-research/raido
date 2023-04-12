package raido.loadtest.scenario;

import io.gatling.javaapi.core.ScenarioBuilder;
import raido.apisvc.endpoint.raidv2.AuthzUtil;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.inttest.util.IdFactory;
import raido.loadtest.config.ApiKey;

import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static raido.apisvc.endpoint.anonymous.PublicEndpoint.STATUS_PATH;
import static raido.loadtest.config.RaidoServerConfig.serverConfig;

public class User {

  public static ScenarioBuilder createUser() {
    return scenario("BasicSimulation").
      exec(http("status").
        get(STATUS_PATH)).
      pause(5);

//    val getAccessToken = exec(http("Get access token")
//      .post(Configuration.tokenPath)
//      .body(StringBody(
//        s"""{
//          "client_id": "${Configuration.clientId}",
//          "client_secret": "${Configuration.clientSecret}",
//          "audience": "https://some-domain-name.com/user",
//          "grant_type": "client_credentials",
//          "scope": "user:admin"
//        }"""
//    ))
//    .asJson
//      .headers(Map("Content-Type" -> "application/json"))
//    .check(status.is(200))
//      .check(jsonPath("$.access_token").saveAs("access_token")))
//    .exec {
//      session =>
//      val fw = new BufferedWriter(new FileWriter("access_token.txt", true))
//      try {
//        fw.write(session("access_token").as[String] + "\r\n")
//      }
//      finally fw.close()
//      session
//    }    
  }
  
  // todo:STO start here
  public void createUserThing(){
    var bootstrapApiToken = new ApiKey(serverConfig).bootstrapToken(
      AuthzUtil.RAIDO_SP_ID,
      "load-test-%s".formatted(IdFactory.generateUniqueId()),
      UserRole.SP_ADMIN );
    
    // then create a load testing user 
    // might need a new endpoint since we're not gonna go through an authz-request
    // actually, just an api-key?
    // if we're doing a load test, then we need to go through the full signup
    // process, including sending emails and authorizing - otherwise what's 
    // the point?
    // to be
    
    // then generate an API token for that user
    
    // then list recent raids for that service point
    
    // then browse a few raids
  }
}
