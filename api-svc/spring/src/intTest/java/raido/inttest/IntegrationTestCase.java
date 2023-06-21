package raido.inttest;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger.Level;
import feign.Response;
import feign.codec.ErrorDecoder;
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
import raido.apisvc.service.stub.util.IdFactory;
import raido.apisvc.spring.config.environment.EnvironmentProps;
import raido.apisvc.util.Log;
import raido.apisvc.util.Nullable;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.idl.raidv1.api.RaidV1Api;
import raido.idl.raidv2.api.AdminExperimentalApi;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.api.PublicExperimentalApi;
import raido.idl.raidv2.model.*;
import raido.inttest.config.IntTestProps;
import raido.inttest.config.IntegrationTestConfig;
import raido.inttest.service.auth.BootstrapAuthTokenService;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.enums.IdProvider.RAIDO_API;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.inttest.config.IntegrationTestConfig.REST_TEMPLATE_VALUES_ONLY_ENCODING;

@SpringJUnitConfig(
  name="SpringJUnitConfigContext",
  value=IntegrationTestConfig.class )
public abstract class IntegrationTestCase {
  // be careful, 25 char max column length
  public static final String INT_TEST_ROR = "https://ror.org/038sjwq14";

  private static final Log log = to(IntegrationTestCase.class);
  protected static final IdFactory idFactory = new IdFactory("inttest");

  @Autowired protected RestTemplate rest;
  @Autowired @Qualifier(REST_TEMPLATE_VALUES_ONLY_ENCODING) 
  protected RestTemplate valuesEncodingRest;
  @Autowired protected IntTestProps props;
  @Autowired protected DSLContext db;
  @Autowired protected BootstrapAuthTokenService bootstrapTokenSvc;
  @Autowired protected Contract feignContract;
  @Autowired protected ObjectMapper mapper;
  @Autowired protected EnvironmentProps env;

  protected String raidV1TestToken;
  protected String operatorToken;
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
    
    raidV1TestToken = bootstrapTokenSvc.initRaidV1TestToken();
    operatorToken = bootstrapTokenSvc.bootstrapToken(
      RAIDO_SP_ID, "intTestOperatorApiToken", OPERATOR);
    
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

  /**
   Acts "as" the bootstrap operator token.
   */
  public BasicRaidExperimentalApi basicRaidExperimentalClient(){
    return basicRaidExperimentalClient(operatorToken);
  }

  /** Uses the bootstrapped `operatorToken` to create a new api-key with the 
   given params, then returns a newly generated token. */
  public GenerateApiTokenResponse createApiKeyUser(
    long servicePointId,
    String subject,
    UserRole role
  ) {
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
    return token;
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

  public PublicServicePoint findPublicServicePoint(String name){
    return publicExperimentalClient().publicListServicePoint().stream().
      filter(i->i.getName().contains(name)).
      findFirst().orElseThrow();
  }
  
  public ServicePoint createServicePoint(@Nullable String name){
    
    var spName = name != null ? name : 
      "%s-%s".formatted(
        this.getClass().getSimpleName(),
        idFactory.generateUniqueId() ); 

    var adminApiAsOp = adminExperimentalClientAs(operatorToken);
    
    return adminApiAsOp.updateServicePoint(new ServicePoint().
      identifierOwner(INT_TEST_ROR).
      name(spName).
      adminEmail("").
      techEmail("").
      enabled(true) );
  }
}
