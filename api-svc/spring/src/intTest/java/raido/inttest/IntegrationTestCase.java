package raido.inttest;

import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;
import raido.inttest.config.IntTestProps;
import raido.inttest.config.IntegrationTestConfig;
import raido.apisvc.util.Log;
import raido.inttest.service.auth.TestAuthTokenService;

import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.createEntityWithBearer;

@SpringJUnitConfig(IntegrationTestConfig.class)
public abstract class IntegrationTestCase {
  private static final Log log = to(IntegrationTestCase.class);

  @Autowired protected RestTemplate rest;
  @Autowired protected IntTestProps props;
  @Autowired protected DSLContext db;
  @Autowired protected TestAuthTokenService authTokenSvc;

  protected String raidV1TestToken;
  
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
    
    raidV1TestToken = authTokenSvc.initTestToken();
  }

  public <T> T get(String authnToken, String url, Class<T> resultType){
    HttpEntity<T> entity = createEntityWithBearer(authnToken);

    var epResponse = rest.exchange(
      raidoApiServerUrl(url),
      HttpMethod.GET, entity, resultType);
    
    return epResponse.getBody();
  }

  public <TResult> TResult anonGet(String url, Class<TResult> resultType){
    HttpEntity<TResult> entity = new HttpEntity<>(new HttpHeaders());

    var epResponse = rest.exchange(
      raidoApiServerUrl(url),
      HttpMethod.GET, entity, resultType);
    
    return epResponse.getBody();
  }

  public <TRequest, TResult> TResult post(
    String authnToken, String url, 
    TRequest request, Class<TResult> resultType
  ){
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(authnToken);
    HttpEntity<TRequest> entity = new HttpEntity<>(request, headers);

    var epResponse = rest.exchange(
      raidoApiServerUrl(url),
      HttpMethod.POST, entity, resultType);
    
    return epResponse.getBody();
  }

  public <TRequest, TResult> TResult anonPost(
    String url, 
    TRequest request, 
    Class<TResult> resultType
  ){
    HttpEntity<TRequest> entity = new HttpEntity<>(request, new HttpHeaders());
    var epResponse = rest.exchange(
      raidoApiServerUrl(url),
      HttpMethod.POST, entity, resultType);
    
    return epResponse.getBody();
  }

  public String raidoApiServerUrl(String url){
    //noinspection HttpUrlsUsage
    return String.format("http://%s%s", props.raidoApiServer, url);
  }

  protected String getTestPrefix(TestInfo testInfo) {
    return testInfo.getTestClass().orElseThrow().getSimpleName();
  }
}
