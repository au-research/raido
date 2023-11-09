package au.org.raid.inttest.endpoint.raidv1;

import au.org.raid.api.util.RestUtil;
import au.org.raid.idl.raidv1.api.RaidV1Api;
import au.org.raid.idl.raidv1.model.RaidCreateModel;
import au.org.raid.idl.raidv1.model.RaidCreateModelMeta;
import au.org.raid.idl.raidv1.model.RaidModel;
import au.org.raid.idl.raidv2.model.PublicClosedMetadataSchemaV1;
import au.org.raid.idl.raidv2.model.PublicReadRaidResponseV3;
import au.org.raid.inttest.IntegrationTestCase;
import feign.FeignException.BadRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.HttpClientErrorException;

import static au.org.raid.api.util.RestUtil.anonPost;
import static au.org.raid.api.util.RestUtil.urlEncode;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LegacyRaidV1MintTest extends IntegrationTestCase {
    public static final String INT_TEST_CONTENT_PATH = "https://raido.int.test/content-path";

    public static final String INT_TEST_ID_URL = "http://localhost:8080";

    private static RaidCreateModel createSimpleRaid(String name) {
        return new RaidCreateModel().
                meta(new RaidCreateModelMeta().name(name)).
                contentPath(INT_TEST_CONTENT_PATH);
    }

    @Test
    void happyDayMintAndGet() {
        var create = createSimpleRaid("happyDayMintAndGet intTest");
        RaidV1Api raidV1 = super.raidV1Client();

        var mintResult = raidV1.rAiDPost(create);
        assertThat(mintResult).isNotNull();
        assertThat(mintResult.getHandle()).isNotBlank();

        PublicReadRaidResponseV3 pubReadV3 = raidoApi.getPublicExperimental().
                publicReadRaidV3(mintResult.getHandle()).getBody();
        assertThat(pubReadV3).isNotNull();
        var pubMetaV3 = (PublicClosedMetadataSchemaV1) pubReadV3.getMetadata();
        assertThat(pubMetaV3.getId().getIdentifier()).
                isEqualTo(INT_TEST_ID_URL + "/" + mintResult.getHandle());

        var getResult = raidV1.handleRaidIdGet(mintResult.getHandle(), false);
        assertThat(getResult).isNotNull();
        assertThat(getResult.getHandle()).isEqualTo(mintResult.getHandle());
    }

    @Test
    @Disabled("https://www.baeldung.com/spring-slash-character-in-url")
    void getHandleWithEncodedSlashShouldSucceed() {
        var raid = super.raidV1Client().rAiDPost(
                createSimpleRaid("encodedSlash raidV1 intTest"));

        var encodedHandle = urlEncode(raid.getHandle());

        var getResult = RestUtil.get(valuesEncodingRest, raidV1TestToken,
                raidoApiServerUrl("/v1/handle/" + encodedHandle),
                RaidModel.class);
        assertThat(getResult.getHandle()).isEqualTo(raid.getHandle());
    }

    /* previously, this test expected a 403 error, but after upgrading
    spring-security and changing over to their `oauth2ResourceServer` stuff,
    this started getting a 401, which I believe is the more correct code. */
    @Test
    void shouldRejectAnonCallToMint() {
        assertThatThrownBy(() ->
                anonPost(rest, raidoApiServerUrl("/v1/raid"), "{}", Object.class)
        ).isInstanceOf(HttpClientErrorException.Unauthorized.class);
    }

    @Test
    void mintShouldRejectMissingContentPath() {
        assertThatThrownBy(() ->
                super.raidV1Client().rAiDPost(
                        createSimpleRaid("intTest").contentPath(null))
        ).isInstanceOf(BadRequest.class).
                hasMessageContaining("no 'contentPath'");
    }

    @Test
    void mintShouldRejectMissingMeta() {
        assertThatThrownBy(() ->
                super.raidV1Client().rAiDPost(
                        createSimpleRaid("intTest").meta(null))
        ).isInstanceOf(BadRequest.class).
                hasMessageContaining("no 'meta'");
    }

    @Test
    void mintShouldRejectMissingName() {
        assertThatThrownBy(() ->
                super.raidV1Client().rAiDPost(
                        createSimpleRaid(null))
        ).isInstanceOf(BadRequest.class).
                hasMessageContaining("no 'meta.name'");
    }


}

