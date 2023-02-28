package raido.inttest.endpoint.anonymous;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException;
import raido.idl.raidv2.model.PublicRaidMetadataSchemaV1;
import raido.idl.raidv2.model.PublicReadRaidResponseV3;
import raido.inttest.IntegrationTestCase;
import raido.inttest.util.IdFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.spring.config.RaidWebSecurityConfig.ROOT_PATH;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.apisvc.util.test.BddUtil.THEN;
import static raido.apisvc.util.test.BddUtil.WHEN;
import static raido.inttest.util.MinimalRaidTestData.createMinimalSchemaV1;
import static raido.inttest.util.MinimalRaidTestData.createMintRequest;

public class GetHandleTest extends IntegrationTestCase {
  @Test
  void apiGetExistingRaidHandleShouldSucceed() {
    var raidApi = super.basicRaidExperimentalClient();
    String title = getName() + IdFactory.generateUniqueId();

    
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
    
    assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
    var metadata = (PublicRaidMetadataSchemaV1) res.getBody().getMetadata();
    assertThat(metadata.getTitles().get(0).getTitle()).isEqualTo(title);
  }

  @Test
  void apiGetNonExistentRaidHandleShould404() {
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
  void apiGetInvalidPrefixRaidHandleShould404() {
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
