package raido.inttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger.Level;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.util.Log;
import raido.idl.raidv1.api.RaidV1Api;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.inttest.config.IntTestProps;
import raido.inttest.config.IntegrationTestConfig;
import raido.inttest.service.auth.TestAuthTokenService;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static raido.apisvc.util.Log.to;

@SpringJUnitConfig(IntegrationTestConfig.class)
public abstract class IntegrationTestCase {
  private static final Log log = to(IntegrationTestCase.class);

  @Autowired protected RestTemplate rest;
  @Autowired protected IntTestProps props;
  @Autowired protected DSLContext db;
  @Autowired protected TestAuthTokenService authTokenSvc;
  @Autowired protected Contract feignContract;
  @Autowired protected ObjectMapper mapper;

  protected String raidV1TestToken;
  protected String raidApiAdminTestToken;
  
  @RegisterExtension
  protected static JettyTestServer jettyTestServer =
    new JettyTestServer();

  /**
   IMPROVE: do this in a beforeAll, so the time isn't counted as
   part of the first tests execution time.  But need to figure out how to
   obtain the test's spring context in a static context.
   */
  @BeforeEach
  public void setupTestToken(){
    if( raidV1TestToken != null ){
      return;
    }
    
    raidV1TestToken = authTokenSvc.initRaidV1TestToken();
    raidApiAdminTestToken = authTokenSvc.initRaidV2ApiAdminTestToken();
  }

  /**
   Once we figure out how to use the token statically, would like to figure
   out how to integrate this into Spring better.  Would like to inject
   the client as autowired, but not sure how the interceptor would work to
   get the auth token.
   */
  public RaidV1Api raidV1Client(){
    return Feign.builder().
      client(new OkHttpClient()).
      encoder(new JacksonEncoder(mapper)).
      decoder(new JacksonDecoder(mapper)).
      contract(feignContract).
      requestInterceptor(request->
        request.header(AUTHORIZATION, "Bearer " + raidV1TestToken) ).
      logger(new Slf4jLogger(RaidV1Api.class)).
      logLevel(Level.FULL).
      target(RaidV1Api.class, props.getRaidoServerUrl() + RAID_V1_API);
  }

  public BasicRaidExperimentalApi basicRaidExperimentalClient(){
    return Feign.builder().
      client(new OkHttpClient()).
      encoder(new JacksonEncoder(mapper)).
      decoder(new JacksonDecoder(mapper)).
      contract(feignContract).
      requestInterceptor(request->
        request.header(AUTHORIZATION, "Bearer " + raidApiAdminTestToken) ).
      logger(new Slf4jLogger(BasicRaidExperimentalApi.class)).
      logLevel(Level.FULL).
      target(BasicRaidExperimentalApi.class, props.getRaidoServerUrl());
  }

  public PublicExperimentalApi publicExperimentalClient(){
    return Feign.builder().
      client(new OkHttpClient()).
      encoder(new JacksonEncoder(mapper)).
      decoder(new JacksonDecoder(mapper)).
      contract(feignContract).
      logger(new Slf4jLogger(PublicExperimentalApi.class)).
      logLevel(Level.FULL).
      target(PublicExperimentalApi.class, props.getRaidoServerUrl());
  }

  public String raidoApiServerUrl(String url){
    //noinspection HttpUrlsUsage
    return String.format("http://%s%s", props.raidoApiServer, url);
  }

  protected String getTestPrefix(TestInfo testInfo) {
    return testInfo.getTestClass().orElseThrow().getSimpleName();
  }
}
