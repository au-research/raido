package raido.inttest.endpoint;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException.MethodNotAllowed;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import raido.apisvc.endpoint.anonymous.CatchAllController;
import raido.apisvc.util.Log;
import raido.apisvc.util.test.BddUtil;
import raido.inttest.IntegrationTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.PUBLIC;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.anonGet;
import static raido.apisvc.util.RestUtil.anonPost;

public class NonExistentMappingTest extends IntegrationTestCase {
  private static final Log log = to(NonExistentMappingTest.class);

  public static final String ROOT_PATH = "/";
  
  public static final String STATUS_PATH = PUBLIC + "/status";
  
  // tests will fail if someone defines this as a valid endpoint path
  public static final String NON_EXISTENT_PATH = PUBLIC + "/does-not-exist";

  @Test
  public void getAnonymousExistentApiEndpointShouldWork() {
    BddUtil.EXPECT("that an existent endpoint is callable");
    assertThat(
      anonGet(rest, raidoApiServerUrl(STATUS_PATH), Result.class).status
    ).isEqualTo("UP");
  }
  
  @Test
  public void getAnonymousNonExistentApiEndpointShould404() {
    BddUtil.EXPECT("that GET on a non-existent endpoint returns 404");
    /* being under "/public/..." is what makes it an "API endpoint", matching 
     WebSecurityConfig requestMatcher.  But since there is no matching endpoint
     declared for this path, we should get a 404. */
    assertThatThrownBy(()->{
      anonGet(rest, raidoApiServerUrl(NON_EXISTENT_PATH), String.class);
    }).
      isInstanceOf(NotFound.class).
      /* should do a full text match to assert no info leakage, 
      messing with regex to deal with the timestamp was taking too long */
      hasMessageContaining("404 Not Found");
  }

  /** This test is more in the nature of codifying what the server DOES in this
  scenario.
  Not really sure it SHOULD work with an invalid token. */
  @Test
  public void getAnonEndpointWithBadTokenDoesWork() {
    BddUtil.EXPECT("GET API endpoint with invalid token is callable");
    
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth("xxx.yyy.zzz");

    assertThat(
      anonGet(rest, raidoApiServerUrl(STATUS_PATH), Result.class).status
    ).isEqualTo("UP");
  }

  @Test
  public void postNonExistentApiEndpointShould404() {
    BddUtil.EXPECT("that POST on a non-existent endpoint returns 404");
    assertThatThrownBy(()->{
      anonPost(rest, raidoApiServerUrl(NON_EXISTENT_PATH), 
        String.class, String.class );
    }).
      isInstanceOf(NotFound.class).
      hasMessageContaining("404 Not Found");
  }

  @Test
  public void getNonExistentNonApiShouldRedirect() {
    BddUtil.EXPECT("that an non-existent, non-api endpoint returns 302");

    HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());
    var res = rest.exchange(raidoApiServerUrl(ROOT_PATH), GET, 
      entity, String.class);
    assertThat(res.getStatusCode().is3xxRedirection()).isTrue();
    assertThat(res.getHeaders().getLocation().toString()).
      isEqualTo(CatchAllController.RAID_WEBSITE);
  }

  @Test
  public void postNonExistentNonApiIsNotAllowed() {
    BddUtil.EXPECT("");

    HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());

    assertThatThrownBy(()->{
      rest.exchange(raidoApiServerUrl(ROOT_PATH), POST, entity, String.class);
    }).
      isInstanceOf(MethodNotAllowed.class).
        hasMessageContaining("405 Method Not Allowed");
  }

  static final class Result {
    public String status;
  }
}

