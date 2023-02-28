package raido.inttest.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException.MethodNotAllowed;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import raido.apisvc.util.Log;
import raido.apisvc.util.test.BddUtil;
import raido.inttest.IntegrationTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;
import static raido.apisvc.endpoint.anonymous.PublicEndpoint.STATUS_PATH;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.PUBLIC;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.ROOT_PATH;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.anonGet;
import static raido.apisvc.util.RestUtil.anonPost;

/**
 Test result of issuing GET and POST requests against various URLS:
 - the root url - /
 - non-api urls - /does-not-exist
 - api endpoint urls - /public/does-not-exist
 */
public class HttpStatusMappingTest extends IntegrationTestCase {
  private static final Log log = to(HttpStatusMappingTest.class);

  // tests will fail if someone defines this as a valid endpoint path
  public static final String NON_EXISTENT_API_PATH = PUBLIC + "/does-not-exist";
  public static final String NON_EXISTENT_NON_API_PATH = "/does-not-exist";
  
  public static final String AUTHN_READ_RAID = "/v2/experimental/read-raid/v2";
  
  @Test
  public void getAnonymousExistentPublicApiEndpointShouldWork() {
    BddUtil.EXPECT(getName());
    assertThat(
      anonGet(rest, raidoApiServerUrl(STATUS_PATH), Result.class).status
    ).isEqualTo("UP");
  }
  
  @Test
  public void getAnonymousExistentAuthnApiEndpointShouldFail() {
    BddUtil.EXPECT(getName());
    assertThatThrownBy(()->{
      anonGet(rest, raidoApiServerUrl(AUTHN_READ_RAID+"/prefix/suffix"),
        Void.class );
    }).
      isInstanceOf(Unauthorized.class).
      hasMessageContaining("401 Unauthorized");
  }
  
  @Test
  public void getBadlyEncodedTokenExistentAuthnApiEndpointShouldFail() {
    BddUtil.EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("xxx.yyy.zzz");
    HttpEntity<Result> entity = new HttpEntity<>(headers);

    assertThatThrownBy(()->{
      rest.exchange(raidoApiServerUrl(AUTHN_READ_RAID+"/prefix/suffix"), 
        GET, entity, Void.class);
    }).
      isInstanceOf(Unauthorized.class).
      hasMessageContaining("401 Unauthorized");
  }
  
  @Test
  public void getAnonymousNonExistentApiEndpointShould404() {
    BddUtil.EXPECT(getName());
    /* being under "/public/..." is what makes it an "API endpoint", matching 
     WebSecurityConfig requestMatcher.  But since there is no matching endpoint
     declared for this path, we should get a 404. */
    assertThatThrownBy(()->{
      anonGet(rest, raidoApiServerUrl(NON_EXISTENT_API_PATH), String.class);
    }).
      isInstanceOf(NotFound.class).
      /* should do a full text match to assert no info leakage, 
      messing with regex to deal with the timestamp was taking too long */
      hasMessageContaining("404 Not Found");
  }

  /** This test is more in the nature of codifying what the server DOES in this
  scenario.
  Not really sure it SHOULD fail with "401 unauth", doesn't make much sense. */
  @Test
  public void getAnonPublicEndpointWithBadTokenDoesFail() {
    BddUtil.EXPECT(getName());
    
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("xxx.yyy.zzz");
    HttpEntity<Result> entity = new HttpEntity<>(headers);

    assertThatThrownBy(()->{
      rest.exchange(raidoApiServerUrl(STATUS_PATH), GET,
        entity, Result.class);
    }).
      isInstanceOf(Unauthorized.class).
      hasMessageContaining("401 Unauthorized");
  }

  @Test
  public void getNonExistentNonApiShould404() {
    BddUtil.EXPECT(getName());

    assertThatThrownBy(()->{
      anonGet(rest, raidoApiServerUrl(NON_EXISTENT_NON_API_PATH), String.class);
    }).
      isInstanceOf(NotFound.class).
      /* should do a full text match to assert no info leakage, 
      messing with regex to deal with the timestamp was taking too long */
        hasMessageContaining("404 Not Found");
  }

  @Test
  public void browserViewRootShouldRedirectToWebsite() {
    BddUtil.EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, TEXT_HTML_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    var res = rest.exchange(raidoApiServerUrl(ROOT_PATH), GET, 
      entity, String.class);
    assertThat(res.getStatusCode().is3xxRedirection()).isTrue();
    assertThat(res.getHeaders().getLocation().toString()).
      isEqualTo(env.rootPathRedirect);
  }

  @Test
  public void browserViewHandleShouldRedirectToLandingPage() {
    BddUtil.EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, TEXT_HTML_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    var res = rest.exchange(
      raidoApiServerUrl(ROOT_PATH) + "102.100.100/suffix", 
      GET, entity, Void.class);
    assertThat(res.getStatusCode().is3xxRedirection()).isTrue();
    assertThat(res.getHeaders().getLocation().toString()).
      isEqualTo(env.raidoLandingPage+"/102.100.100/suffix");
  }

  @Test
  public void apiGetRootShould404() {
    BddUtil.EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, APPLICATION_JSON_VALUE);
    headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    assertThatThrownBy(()->{
      rest.exchange(raidoApiServerUrl(ROOT_PATH), GET, entity, String.class);
    }).
      isInstanceOf(NotFound.class).
      hasMessageContaining("404 Not Found");
  }

  @Test
  public void postNonExistentRootShould405() {
    BddUtil.EXPECT(getName());

    HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());

    assertThatThrownBy(()->{
      rest.exchange(raidoApiServerUrl(ROOT_PATH), POST, entity, String.class);
    }).
      isInstanceOf(MethodNotAllowed.class).
        hasMessageContaining("405 Method Not Allowed");
  }

  @Test
  public void postNonExistentNonApiShouldFail() {
    BddUtil.EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    assertThatThrownBy(()->{
      rest.exchange(
        raidoApiServerUrl(NON_EXISTENT_NON_API_PATH), POST, 
        entity, String.class );
    }).
      isInstanceOf(NotFound.class).
      hasMessageContaining("404 Not Found");
  }

  @Test
  public void postNonExistentNonApiNoAcceptHeaderShouldFail() {
    BddUtil.EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> entity = new HttpEntity<>(headers);

    assertThatThrownBy(()->{
      rest.exchange(
        raidoApiServerUrl(NON_EXISTENT_NON_API_PATH), POST, 
        entity, String.class );
    }).
      isInstanceOf(NotFound.class).
      hasMessageContaining("404 Not Found");
  }

  @Test
  public void postNonExistentApiEndpointShould405() {
    BddUtil.EXPECT(getName());
    assertThatThrownBy(()->{
      anonPost(rest, raidoApiServerUrl(NON_EXISTENT_API_PATH),
        String.class, String.class );
    }).
      isInstanceOf(MethodNotAllowed.class).
      hasMessageContaining("405 Method Not Allowed");
  }

  static final class Result {
    public String status;
  }
}

