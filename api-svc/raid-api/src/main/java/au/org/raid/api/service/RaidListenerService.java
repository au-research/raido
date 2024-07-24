package au.org.raid.api.service;

import au.org.raid.api.config.properties.RaidListenerProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RaidListenerService {
    private final RestTemplate restTemplate;
    private final RaidListenerProperties properties;

    public void post(final String email) {
        final var headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        final var httpEntity = new HttpEntity<Map<String, String>>(new HashMap<>(Map.of("email", email)), headers);

        restTemplate.exchange(properties.getUri(), HttpMethod.POST, httpEntity, Void.class);
    }
}
