package au.org.raid.inttest.service;

import au.org.raid.inttest.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenService {

    @Value("${raid.test.auth.client-id}")
    private String clientId;

    @Value("${raid.test.auth.grant-type}")
    private String grantType = "password";

    @Value("${raid.test.auth.token-uri}")
    private String tokenUri;

    private final RestTemplate restTemplate;

    public String getUserToken(final String username, final String password) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final var body = "client_id=%s&username=%s&password=%s&grant_type=%s".formatted(clientId, username, password, grantType);

        log.debug("Requesting token with body {}", body);

        final var httpEntity = new HttpEntity<>(body, headers);

        final var tokenResponse = restTemplate.postForEntity(tokenUri, httpEntity, TokenResponse.class);
        log.debug("Token request returned {} for user {}", tokenResponse.getStatusCode(), username);

        return Objects.requireNonNull(tokenResponse.getBody()).getAccessToken();
    }

    public String getClientToken(final String clientId, final String clientSecret) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final var body = "client_id=%s&client_secret=%s&grant_type=client_credentials".formatted(clientId, clientSecret);

        log.debug("Requesting token with body {}", body);

        final var httpEntity = new HttpEntity<>(body, headers);

        final var tokenResponse = restTemplate.postForEntity(tokenUri, httpEntity, TokenResponse.class);
        log.debug("Token request returned {} for user {}", tokenResponse.getStatusCode(), clientId);

        return Objects.requireNonNull(tokenResponse.getBody()).getAccessToken();
    }
}
