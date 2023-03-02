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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.idl.raidv1.api.RaidV1Api;
import raido.idl.raidv2.api.AdminExperimentalApi;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.ApiKey;
import raido.idl.raidv2.model.GenerateApiTokenRequest;
import raido.idl.raidv2.model.ServicePoint;
import raido.inttest.config.IntTestProps;
import raido.inttest.config.IntegrationTestConfig;
import raido.inttest.service.auth.TestAuthTokenService;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.areEqual;
import static raido.db.jooq.api_svc.enums.IdProvider.RAIDO_API;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.db.jooq.api_svc.enums.UserRole.SP_ADMIN;
import static raido.inttest.config.IntegrationTestConfig.REST_TEMPLATE_VALUES_ONLY_ENCODING;

@SpringJUnitConfig(
  name="SpringJUnitConfigContext",
  value=IntegrationTestConfig.class )
public abstract class IntegrationTestCase {
  private static final Log log = to(IntegrationTestCase.class);

  @Autowired protected RestTemplate rest;
  @Autowired @Qualifier(REST_TEMPLATE_VALUES_ONLY_ENCODING) 
  protected RestTemplate valuesEncodingRest;
  @Autowired protected IntTestProps props;
  @Autowired protected DSLContext db;
  @Autowired protected TestAuthTokenService authTokenSvc;
  @Autowired protected Contract feignContract;
  @Autowired protected ObjectMapper mapper;
  @Autowired protected EnvironmentProps env;

  protected String raidV1TestToken;
  protected String operatorToken;
  protected String adminToken;
  protected RaidoApiUtil raidoApi;

  private TestInfo testInfo;

  @RegisterExtension
  protected static JettyTestServer jettyTestServer = new JettyTestServer();

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
    operatorToken = authTokenSvc.bootstrapToken(
      RAIDO_SP_ID, "intTestOperatorApiToken", OPERATOR);
    adminToken = authTokenSvc.bootstrapToken(
      RAIDO_SP_ID, "intTestAdminApiToken", SP_ADMIN);
    /* the feign clients passed to this wrapper and bound to the test tokens 
    created above.  When we want to "change" user, need to use a new feign 
    clients bound the new user identity. */
    raidoApi = new RaidoApiUtil(publicExperimentalClient(), mapper);
  }

  @BeforeEach
  public void init(TestInfo testInfo) {
    this.testInfo = testInfo;
  }
  
  public String getName(){
    return testInfo.getDisplayName();
  }
  
  /**
   Once we figure out how to use the token statically, would like to figure
   out how to integrate this into Spring better.  Would like to inject
   the client as autowired, but not sure how the interceptor would work to
   get the auth token, especially as we start wanting to work with multiple
   users in the scope of a single test.
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
    return basicRaidExperimentalClient(operatorToken);
  }

  /** Acts "as" an operator and uses prod endpoints to create an api-key for 
   the given input and generate a token. */
  public BasicRaidExperimentalApi basicRaidExperimentalClientAs(
    long servicePointId,
    String subject,
    UserRole role
  ){
    var adminApi = adminExperimentalClientAs(operatorToken);
    LocalDateTime expiry = LocalDateTime.now().plusDays(30);

    var apiKey = adminApi.updateApiKey(new ApiKey().
      servicePointId(servicePointId).
      idProvider(RAIDO_API.getLiteral()).
      role(role.getLiteral()).
      subject(subject).
      enabled(true).
      tokenCutoff(expiry.atOffset(UTC))
    );
    var token = adminApi.generateApiToken(new GenerateApiTokenRequest().
      apiKeyId(apiKey.getId()) );
    
    return basicRaidExperimentalClient(token.getApiToken());
  }

  public BasicRaidExperimentalApi basicRaidExperimentalClient(String token){
    return Feign.builder().
      client(new OkHttpClient()).
      encoder(new JacksonEncoder(mapper)).
      decoder(new JacksonDecoder(mapper)).
      contract(feignContract).
      requestInterceptor(request->
        request.header(AUTHORIZATION, "Bearer " + token) ).
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

  public AdminExperimentalApi adminExperimentalClientAs(String token){
    return Feign.builder().
      client(new OkHttpClient()).
      encoder(new JacksonEncoder(mapper)).
      decoder(new JacksonDecoder(mapper)).
      contract(feignContract).
      requestInterceptor(request->
        request.header(AUTHORIZATION, "Bearer " + token) ).
      logger(new Slf4jLogger(AdminExperimentalApi.class)).
      logLevel(Level.FULL).
      target(AdminExperimentalApi.class, props.getRaidoServerUrl());
  }

  public String raidoApiServerUrl(String url){
    //noinspection HttpUrlsUsage
    return String.format("http://%s%s", props.raidoApiServer, url);
  }

  protected String getTestPrefix(TestInfo testInfo) {
    return testInfo.getTestClass().orElseThrow().getSimpleName();
  }

  public static ServicePoint findServicePoint(
    AdminExperimentalApi adminApi, String name
  ){
    return adminApi.listServicePoint().stream().
      filter(i->areEqual(i.getName(), name)).
      findFirst().orElseThrow();
  }
}
