package raido.inttest.endpoint.raidv1;

import feign.FeignException.BadRequest;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;
import raido.apisvc.util.Log;
import raido.apisvc.util.RestUtil;
import raido.idl.raidv1.api.RaidV1Api;
import raido.idl.raidv1.model.RaidCreateModel;
import raido.idl.raidv1.model.RaidCreateModelMeta;
import raido.idl.raidv1.model.RaidModel;
import raido.idl.raidv2.model.PublicClosedMetadataSchemaV1;
import raido.idl.raidv2.model.PublicReadRaidResponseV3;
import raido.inttest.IntegrationTestCase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.anonPost;
import static raido.apisvc.util.RestUtil.urlEncode;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.apisvc.util.test.BddUtil.GIVEN;
import static raido.inttest.config.IntegrationTestConfig.restTemplateWithEncodingMode;

public class LegacyRaidV1MintTest extends IntegrationTestCase {
  public static final String INT_TEST_CONTENT_PATH = "https://raido.int.test";

  private static final Log log = to(LegacyRaidV1MintTest.class);

  public static String getForbiddenMessage(String path) {
    return """
      403 Forbidden: "{<EOL>"message":"Access Denied",<EOL>"url":"%s",<EOL>"status":"403"<EOL>}"
      """.formatted(path).trim();
  }

  @Test void happyDayMintAndGet(){
    var create = createSimpleRaid("happyDayMintAndGet inttest");
    RaidV1Api raidV1 = super.raidV1Client();

    EXPECT("mint V1 legacy raid with RDM-style content should succeed");
    var mintResult = raidV1.raidPost(create);
    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getHandle()).isNotBlank();

    EXPECT("should be able to read the minted raid via V3 public endpoint");
    PublicReadRaidResponseV3 pubReadV3 = raidoApi.getPublicExperimintal().
      publicReadRaidV3(mintResult.getHandle());
    assertThat(pubReadV3).isNotNull();
    var pubMetaV3 = (PublicClosedMetadataSchemaV1) pubReadV3.getMetadata();
    assertThat(pubMetaV3.getId().getIdentifier()).
      isEqualTo(mintResult.getHandle());
    
    EXPECT("should be able to read the minted raid via V1 endpoint");
    var getResult = raidV1.handleRaidIdGet(mintResult.getHandle(), false);
    assertThat(getResult).isNotNull();
    assertThat(getResult.getHandle()).isEqualTo(mintResult.getHandle());
  }

  private static RaidCreateModel createSimpleRaid(String name) {
    return new RaidCreateModel().
      meta(new RaidCreateModelMeta().name(name)).
      contentPath(INT_TEST_CONTENT_PATH);
  }

  @Test void getHandleWithEncodedSlashShouldSucceed(){
    GIVEN("raid exists");
    var raid = super.raidV1Client().raidPost(
      createSimpleRaid("getHandleWithEncodedSlashShouldSucceed inttest"));

    // dunno how to get feign to do the encoding thing - this'll do
    var rest = restTemplateWithEncodingMode();
    var encodedHandle = urlEncode(raid.getHandle());

    EXPECT("get handle with encoded path should succeed");
    var getResult = RestUtil.get(rest, raidV1TestToken,
      raidoApiServerUrl("/v1/handle/" + encodedHandle),
      RaidModel.class);
    assertThat(getResult.getHandle()).isEqualTo(raid.getHandle());
  }

  /* previously, this test expected a 403 error, but after upgrading 
  spring-security and changing over to their `oauth2ResourceServer` stuff,
  this started getting a 401, which I believe is the more correct code. */
  @Test
  void shoudRejectAnonCalltoMint() {
    EXPECT("minting a raid without authenticating should fail");
    assertThatThrownBy(()->
      anonPost(rest, raidoApiServerUrl("/v1/raid"), "{}", Object.class)
    ).isInstanceOf(HttpClientErrorException.Unauthorized.class).
      /* full text match to ensure there's no info leakage */
      hasMessage("401 Unauthorized: [no body]");
  }

  @Test
  void mintShouldRejectMissingContentPath() {
    EXPECT("minting a raid without a contentPath should fail");
    assertThatThrownBy(()->
      super.raidV1Client().raidPost(
        createSimpleRaid("inttest").contentPath(null) )
    ).isInstanceOf(BadRequest.class).
      hasMessageContaining("no 'contentPath'");
  }
  
  @Test
  void mintShouldRejectMissingMeta() {
    EXPECT("minting a raid without a meta should fail");
    assertThatThrownBy(()->
      super.raidV1Client().raidPost(
        createSimpleRaid("inttest").meta(null) )
    ).isInstanceOf(BadRequest.class).
      hasMessageContaining("no 'meta'");
  }
  
  @Test
  void mintShouldRejectMissingName() {
    EXPECT("minting a raid without a name should fail");
    assertThatThrownBy(()->
      super.raidV1Client().raidPost(
        createSimpleRaid(null) )
    ).isInstanceOf(BadRequest.class).
      hasMessageContaining("no 'meta.name'");
  }
  

}

