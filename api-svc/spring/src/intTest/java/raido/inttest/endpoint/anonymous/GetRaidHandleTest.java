package raido.inttest.endpoint.anonymous;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import raido.apisvc.util.Log;
import raido.apisvc.util.test.BddUtil;
import raido.idl.raidv2.model.PublicRaidMetadataSchemaV1;
import raido.idl.raidv2.model.PublicReadRaidResponseV3;
import raido.inttest.IntegrationTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.ROOT_PATH;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.urlEncode;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.apisvc.util.test.BddUtil.THEN;
import static raido.apisvc.util.test.BddUtil.WHEN;
import static raido.inttest.util.MinimalRaidTestData.createMinimalSchemaV1;
import static raido.inttest.util.MinimalRaidTestData.createMintRequest;

public class GetRaidHandleTest extends IntegrationTestCase {
  private static final Log log = to(GetRaidHandleTest.class);
  
  @Test
  void apiGetExistingShouldSucceedWithAcceptJson() {
    var raidApi = super.basicRaidExperimentalClient();
    String title = getName() + idFactory.generateUniqueId();

    
    WHEN("a raid is minted");
    var mintResult = raidApi.mintRaidoSchemaV1(
      createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID) );

    
    THEN("API GET of root mapping with handle should return data");
    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, APPLICATION_JSON_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    var res = rest.exchange(
      raidoApiServerUrl(ROOT_PATH) + "/" + mintResult.getRaid().getHandle(),
      GET, entity, PublicReadRaidResponseV3.class);
    
    assertThat(res.getStatusCode().is2xxSuccessful()).
      overridingErrorMessage("GET call should have succeeded directly").
      isTrue();
    var metadata = (PublicRaidMetadataSchemaV1) res.getBody().getMetadata();
    assertThat(metadata.getTitles().get(0).getTitle()).isEqualTo(title);
  }

  /**
   This test was added because when doing a curl from the command line and 
   specifying only the `contentType`, curl adds an "accept all" header
   so the headers are actually:
   Accept: *\/*
   Content-Type: application/json
   
   (note the backslash added to the `Accept` header because the value is the 
   terminating sequence marker for javadoc)
   
   I believe the asserted functionality is correct, because the `Content-Type`
   header doesn't even make sense with a GET request (it doesn't have a body).
   */
  @Test
  void apiGetExistingShouldRedirectWithAcceptAll() {
    var raidApi = super.basicRaidExperimentalClient();
    String title = getName() + idFactory.generateUniqueId();


    WHEN("a raid is minted");
    var mintResult = raidApi.mintRaidoSchemaV1(
      createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID));


    THEN("API GET of root mapping with handle should return data");
    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, "*/*");
    headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    var res = rest.exchange(
      raidoApiServerUrl(ROOT_PATH) + "/" + mintResult.getRaid().getHandle(),
      GET, entity, PublicReadRaidResponseV3.class);

    assertThat(res.getStatusCode().is3xxRedirection()).
      overridingErrorMessage("GET call should have redirected").
      isTrue();
  }

  @Test
  void apiGetExistingWithEncodingShouldSucceed() {
    var raidApi = super.basicRaidExperimentalClient();
    String title = getName() + idFactory.generateUniqueId();

    
    WHEN("a raid is minted");
    var mintResult = raidApi.mintRaidoSchemaV1(
      createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID) );

    
    THEN("API GET of root mapping with an encoded handle should return data");
    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, APPLICATION_JSON_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);
    
    String encodedHandle = urlEncode(mintResult.getRaid().getHandle());
    
    var res = valuesEncodingRest.exchange(
      raidoApiServerUrl(ROOT_PATH) + "/" + encodedHandle,
      GET, entity, PublicReadRaidResponseV3.class);
    
    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
    var metadata = (PublicRaidMetadataSchemaV1) res.getBody().getMetadata();
    assertThat(metadata.getTitles().get(0).getTitle()).isEqualTo(title);
  }

  @Test
  public void browserViewExistingWithNoAcceptHeaderShouldRedirectToWebsite() {
    BddUtil.EXPECT(getName());

    var raidApi = super.basicRaidExperimentalClient();
    String title = getName() + idFactory.generateUniqueId();

    WHEN("a raid is minted");
    var mintResult = raidApi.mintRaidoSchemaV1(
      createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID) );
    
    HttpHeaders headers = new HttpHeaders();
    /* RestTemplate appears to default to 
     accept=application/xml, application/json, application/*+json */
    headers.set(ACCEPT, "");
    HttpEntity<String> entity = new HttpEntity<>(headers);


    THEN("GET handle and no Accept header should redirect to landing page");
    var res = rest.exchange(
      raidoApiServerUrl(ROOT_PATH)+ mintResult.getRaid().getHandle(),
      GET, entity, Void.class);
    assertThat(res.getStatusCode().is3xxRedirection()).
      overridingErrorMessage("missing accept header should have redirected").
      isTrue();
    assertThat(res.getHeaders().getLocation().toString()).
      isEqualTo(env.raidoLandingPage+"/"+mintResult.getRaid().getHandle());
  }


  @Test
  void apiGetNonExistentShould404() {
    EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, APPLICATION_JSON_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    
    assertThatThrownBy(()->{
      rest.exchange(
        raidoApiServerUrl(ROOT_PATH) + "/102.100.100/42.42",
        GET, entity, PublicReadRaidResponseV3.class);
    }).
      isInstanceOf(HttpClientErrorException.NotFound.class).
      hasMessageContaining("404 Not Found");
  }
  
  @Test
  void apiGetInvalidPrefixShould404() {
    EXPECT(getName());

    HttpHeaders headers = new HttpHeaders();
    headers.set(ACCEPT, APPLICATION_JSON_VALUE);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    assertThatThrownBy(()->{
      rest.exchange(
        raidoApiServerUrl(ROOT_PATH) + "/42.42.42/42.42",
        GET, entity, PublicReadRaidResponseV3.class);
    }).
      isInstanceOf(HttpClientErrorException.NotFound.class).
      hasMessageContaining("404 Not Found");
  }
  
}
