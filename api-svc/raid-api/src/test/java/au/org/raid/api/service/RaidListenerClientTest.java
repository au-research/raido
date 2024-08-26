package au.org.raid.api.service;

import au.org.raid.api.config.properties.RaidListenerProperties;
import au.org.raid.api.dto.RaidListenerMessage;
import au.org.raid.api.factory.HttpEntityFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaidListenerClientTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RaidListenerProperties properties;
    @Mock
    private HttpEntityFactory httpEntityFactory;
    @InjectMocks
    private RaidListenerClient raidListenerClient;

    @Test
    @DisplayName("post method sends POST request to raid listener")
    void post() {
        final var message = new RaidListenerMessage();
        final var httpEntity = mock(HttpEntity.class);
        final var uri = "_uri";

        when(properties.getUri()).thenReturn(uri);
        when(httpEntityFactory.create(message)).thenReturn(httpEntity);

        raidListenerClient.post(message);

        verify(restTemplate).exchange(uri, HttpMethod.POST, httpEntity, Void.class);
    }
}