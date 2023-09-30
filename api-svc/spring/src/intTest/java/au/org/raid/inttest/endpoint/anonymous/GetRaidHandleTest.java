package au.org.raid.inttest.endpoint.anonymous;

import au.org.raid.api.test.util.BddUtil;
import au.org.raid.api.util.Log;
import au.org.raid.idl.raidv2.model.PublicRaidMetadataSchemaV1;
import au.org.raid.idl.raidv2.model.PublicReadRaidResponseV3;
import au.org.raid.inttest.IntegrationTestCase;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.api.spring.config.RaidWebSecurityConfig.ROOT_PATH;
import static au.org.raid.api.test.util.BddUtil.*;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.inttest.util.MinimalRaidTestData.createMinimalSchemaV1;
import static au.org.raid.inttest.util.MinimalRaidTestData.createMintRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class GetRaidHandleTest extends IntegrationTestCase {
    @Test
    void apiGetExistingShouldSucceedWithAcceptJson() {
        var raidApi = super.basicRaidExperimentalClient();
        String title = getName() + idFactory.generateUniqueId();


        WHEN("a raid is minted");
        var mintResult = raidApi.mintRaidoSchemaV1(
                createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID)).getBody();


        THEN("API GET of root mapping with handle should return data");
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var res = rest.exchange(
                raidoApiServerUrl(ROOT_PATH) + "/" + mintResult.getRaid().getHandle(),
                HttpMethod.GET, entity, PublicReadRaidResponseV3.class);

        assertThat(res.getStatusCode().is2xxSuccessful()).
                overridingErrorMessage("GET call should have succeeded directly").
                isTrue();
        var metadata = (PublicRaidMetadataSchemaV1) res.getBody().getMetadata();
        assertThat(metadata.getTitles().get(0).getTitle()).isEqualTo(title);
    }

    /**
     * This test was added because when doing a curl from the command line and
     * specifying only the `contentType`, curl adds an "accept all" header
     * so the headers are actually:
     * Accept: *\/*
     * Content-Type: application/json
     * <p>
     * (note the backslash added to the `Accept` header because the value is the
     * terminating sequence marker for javadoc)
     * <p>
     * I believe the asserted functionality is correct, because the `Content-Type`
     * header doesn't even make sense with a GET request (it doesn't have a body).
     */
    @Test
    void apiGetExistingShouldRedirectWithAcceptAll() {
        var raidApi = super.basicRaidExperimentalClient();
        String title = getName() + idFactory.generateUniqueId();


        WHEN("a raid is minted");
        var mintResult = raidApi.mintRaidoSchemaV1(
                createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID)).getBody();


        THEN("API GET of root mapping with handle should return data");
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, "*/*");
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        var res = rest.exchange(
                raidoApiServerUrl(ROOT_PATH) + "/" + mintResult.getRaid().getHandle(),
                HttpMethod.GET, entity, PublicReadRaidResponseV3.class);

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
                createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID)).getBody();

        THEN("API GET of root mapping with an encoded handle should return data");
        final var uri = raidoApiServerUrl(ROOT_PATH) + "/" + mintResult.getRaid().getHandle();
        var res = rest.getForEntity(uri, PublicReadRaidResponseV3.class);

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
                createMintRequest(createMinimalSchemaV1(title), RAIDO_SP_ID)).getBody();

        HttpHeaders headers = new HttpHeaders();
    /* RestTemplate appears to default to 
     accept=application/xml, application/json, application/*+json */
        headers.set(ACCEPT, "");
        HttpEntity<String> entity = new HttpEntity<>(headers);


        THEN("GET handle and no Accept header should redirect to landing page");
        var res = rest.exchange(
                raidoApiServerUrl(ROOT_PATH) + mintResult.getRaid().getHandle(),
                HttpMethod.GET, entity, Void.class);
        assertThat(res.getStatusCode().is3xxRedirection()).
                overridingErrorMessage("missing accept header should have redirected").
                isTrue();
        assertThat(res.getHeaders().getLocation().toString()).
                isEqualTo(env.raidoLandingPage + "/" + mintResult.getRaid().getHandle());
    }


    @Test
    void apiGetNonExistentShould404() {
        EXPECT(getName());

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);


        assertThatThrownBy(() -> {
            rest.exchange(
                    raidoApiServerUrl(ROOT_PATH) + "/102.100.100/42.42",
                    HttpMethod.GET, entity, PublicReadRaidResponseV3.class);
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

        assertThatThrownBy(() -> {
            rest.exchange(
                    raidoApiServerUrl(ROOT_PATH) + "/42.42.42/42.42",
                    HttpMethod.GET, entity, PublicReadRaidResponseV3.class);
        }).
                isInstanceOf(HttpClientErrorException.NotFound.class).
                hasMessageContaining("404 Not Found");
    }

}
