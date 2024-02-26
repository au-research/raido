package au.org.raid.inttest.service;

import au.org.raid.inttest.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TokenService {

    @Value("${raid.test.auth.client-id}")
    private String clientId;

    @Value("${raid.test.auth.user}")
    private String username;

    @Value("${raid.test.auth.password}")
    private String password;

    @Value("${raid.test.auth.grant-type}")
    private String grantType = "password";

    @Value("${raid.test.auth.token-uri}")
    private String tokenUri;

    private final RestTemplate restTemplate;

    public String getToken(final String username, final String password) {
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        final var body = "client_id=%s&username=%s&password=%s&grant_type=%s".formatted(clientId, username, password, grantType);

        final var httpEntity = new HttpEntity<>(body, headers);

        final var tokenResponse = restTemplate.postForEntity(tokenUri, httpEntity, TokenResponse.class);

        return Objects.requireNonNull(tokenResponse.getBody()).getAccessToken();
    }
}
