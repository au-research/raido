package au.org.raid.api.service;

import au.org.raid.api.config.properties.RaidListenerProperties;
import au.org.raid.api.dto.RaidListenerMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class RaidListenerService {
    private final RestTemplate restTemplate;
    private final RaidListenerProperties properties;

    public void post(final RaidListenerMessage message) {
        final var headers = new HttpHeaders();
        headers.set("Content-type", "application/json");

        final var httpEntity = new HttpEntity<>(message, headers);

        restTemplate.exchange(properties.getUri(), HttpMethod.POST, httpEntity, Void.class);
    }
}
