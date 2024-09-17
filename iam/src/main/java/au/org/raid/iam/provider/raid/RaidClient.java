package au.org.raid.iam.provider.raid;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.http.impl.client.HttpClients;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.crypto.AsymmetricSignatureSignerContext;
import org.keycloak.crypto.KeyWrapper;
import org.keycloak.jose.jws.JWSBuilder;
import org.keycloak.representations.AccessToken;

@RequiredArgsConstructor
public class RaidClient {
    private static final String URL_FORMAT = "http://host.docker.internal:8080/raid/%s/permissions";
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public RaidPermissionsDto getPermissions(final String handle, final AccessToken token, final KeyWrapper key) {
        try (final var httpClient = HttpClients.createDefault()) {
            final var http = SimpleHttp.doGet(URL_FORMAT.formatted(handle), httpClient);

            String jwt = new JWSBuilder()
                    .type("JWT")
                    .jsonContent(token)
                    .sign(new AsymmetricSignatureSignerContext(key));

            http.auth(jwt);
            try (final var response = http.asResponse()) {
                return objectMapper.readValue(response.asString(), RaidPermissionsDto.class);
            }
        }
    }
}
