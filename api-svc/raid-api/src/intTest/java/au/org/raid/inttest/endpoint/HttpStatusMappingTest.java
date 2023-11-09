package au.org.raid.inttest.endpoint;

import au.org.raid.inttest.IntegrationTestCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.HttpClientErrorException.MethodNotAllowed;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import static au.org.raid.api.endpoint.anonymous.PublicEndpoint.STATUS_PATH;
import static au.org.raid.api.spring.config.RaidWebSecurityConfig.PUBLIC;
import static au.org.raid.api.spring.config.RaidWebSecurityConfig.ROOT_PATH;
import static au.org.raid.api.util.RestUtil.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_HTML_VALUE;

/**
 * Test result of issuing GET and POST requests against various URLS:
 * - the root url - /
 * - non-api urls - /does-not-exist
 * - api endpoint urls - /public/does-not-exist
 */
@Disabled
public class HttpStatusMappingTest extends IntegrationTestCase {
    // tests will fail if someone defines this as a valid endpoint path
    public static final String NON_EXISTENT_API_PATH = PUBLIC + "/does-not-exist";
    public static final String NON_EXISTENT_NON_API_PATH = "/does-not-exist";
    public static final String AUTHN_READ_RAID = "/v2/experimental/read-raid/v2";
    public static final String EXAMPLE_HANDLE = "102.100.100/suffix";

    @Test
    public void getAnonymousExistentPublicApiEndpointShouldWork() {
        assertThat(
                anonGet(rest, raidoApiServerUrl(STATUS_PATH), Result.class).status
        ).isEqualTo("UP");
    }

    @Test
    public void getAnonymousExistentAuthnApiEndpointShouldFail() {
        assertThatThrownBy(() -> {
            anonGet(rest, raidoApiServerUrl(AUTHN_READ_RAID + "/" + EXAMPLE_HANDLE),
                    Void.class);
        })
                .isInstanceOf(Unauthorized.class);
    }

    @Test
    public void getBadlyEncodedTokenExistentAuthnApiEndpointShouldFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("xxx.yyy.zzz");
        HttpEntity<Result> entity = new HttpEntity<>(headers);

        assertThatThrownBy(() -> {
            rest.exchange(raidoApiServerUrl(AUTHN_READ_RAID + "/" + EXAMPLE_HANDLE),
                    GET, entity, Void.class);
        }).isInstanceOf(Unauthorized.class);
    }

    @Test
    public void getAnonymousNonExistentApiEndpointShould404() {
    /* being under "/public/..." is what makes it an "API endpoint", matching
     WebSecurityConfig requestMatcher.  But since there is no matching endpoint
     declared for this path, we should get a 404. */
        assertThatThrownBy(() -> {
            anonGet(rest, raidoApiServerUrl(NON_EXISTENT_API_PATH), String.class);
        })
                .isInstanceOf(NotFound.class);
    }

    /**
     * This test is more in the nature of codifying what the server DOES in this
     * scenario.
     * Not really sure it SHOULD fail with "401 unauth", doesn't make much sense.
     */
    @Test
    public void getAnonPublicEndpointWithBadTokenDoesFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth("xxx.yyy.zzz");
        HttpEntity<Result> entity = new HttpEntity<>(headers);

        assertThatThrownBy(() -> {
            rest.exchange(raidoApiServerUrl(STATUS_PATH), GET,
                    entity, Result.class);
        })
                .isInstanceOf(Unauthorized.class);

    }

    @Test
    public void getNonExistentNonApiShould404() {
        assertThatThrownBy(() -> {
            anonGet(rest, raidoApiServerUrl(NON_EXISTENT_NON_API_PATH), String.class);
        }).
                isInstanceOf(NotFound.class);
    }

    @Test
    @Disabled
    public void browserViewRootShouldRedirectToWebsite() {
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
    @Disabled
    public void browserViewHandleShouldRedirectToLandingPage() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, TEXT_HTML_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        var res = rest.exchange(
                raidoApiServerUrl(ROOT_PATH) + "/" + EXAMPLE_HANDLE,
                GET, entity, Void.class);
        assertThat(res.getStatusCode().is3xxRedirection()).isTrue();
        assertThat(res.getHeaders().getLocation().toString()).
                isEqualTo(env.raidoLandingPage + "/" + EXAMPLE_HANDLE);
    }

    @Test
    @Disabled
    public void browserViewEncodedHandleShouldRedirectToLandingPage() {
        var encodedHandle = urlEncode(EXAMPLE_HANDLE);

        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, TEXT_HTML_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        var res = valuesEncodingRest.exchange(
                raidoApiServerUrl(ROOT_PATH) + encodedHandle,
                GET, entity, Void.class);
        assertThat(res.getStatusCode().is3xxRedirection()).isTrue();
        assertThat(res.getHeaders().getLocation().toString()).
                isEqualTo(env.raidoLandingPage + "/" + EXAMPLE_HANDLE);
    }

    @Test
    public void apiGetRootShould404() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ACCEPT, APPLICATION_JSON_VALUE);
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        assertThatThrownBy(() -> {
            rest.exchange(raidoApiServerUrl(ROOT_PATH), GET, entity, String.class);
        }).
                isInstanceOf(NotFound.class);
    }

    @Test
    @Disabled
    public void postNonExistentRootShould405() {
        HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());

        assertThatThrownBy(() -> {
            rest.exchange(raidoApiServerUrl(ROOT_PATH), POST, entity, String.class);
        }).
                isInstanceOf(MethodNotAllowed.class).
                hasMessageContaining("405 Method Not Allowed");
    }

    @Test
    @Disabled
    public void postNonExistentNonApiShouldFail() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(CONTENT_TYPE, APPLICATION_JSON_VALUE);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        assertThatThrownBy(() -> {
            rest.exchange(
                    raidoApiServerUrl(NON_EXISTENT_NON_API_PATH), POST,
                    entity, String.class);
        }).
                isInstanceOf(MethodNotAllowed.class);
    }

    @Test
    @Disabled
    public void postNonExistentNonApiNoAcceptHeaderShouldFail() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        assertThatThrownBy(() -> {
            rest.exchange(
                    raidoApiServerUrl(NON_EXISTENT_NON_API_PATH), POST,
                    entity, String.class);
        }).
                isInstanceOf(MethodNotAllowed.class);
    }

    @Test
    @Disabled
    public void postNonExistentApiEndpointShould405() {
        assertThatThrownBy(() -> {
            anonPost(rest, raidoApiServerUrl(NON_EXISTENT_API_PATH),
                    String.class, String.class);
        }).
                isInstanceOf(MethodNotAllowed.class).
                hasMessageContaining("405 Method Not Allowed");
    }

    static final class Result {
        public String status;
    }
}

