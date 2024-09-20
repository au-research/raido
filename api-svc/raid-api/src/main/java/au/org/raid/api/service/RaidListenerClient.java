package au.org.raid.api.service;

import au.org.raid.api.config.properties.RaidListenerProperties;
import au.org.raid.api.dto.RaidListenerMessage;
import au.org.raid.api.factory.HttpEntityFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Component
@RequiredArgsConstructor
public class RaidListenerClient {
    private final RestTemplate restTemplate;
    private final RaidListenerProperties properties;
    private final HttpEntityFactory httpEntityFactory;

    public void post(final RaidListenerMessage message) {
        final var httpEntity = httpEntityFactory.create(message);

        final var response = restTemplate.exchange(properties.getUri(), HttpMethod.POST, httpEntity, Void.class);
        log.debug("Response from Raid Listener: {} {}", response.getStatusCode(), response.getBody());
    }
}
