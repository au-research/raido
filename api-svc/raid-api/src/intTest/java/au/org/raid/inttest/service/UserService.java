package au.org.raid.inttest.service;

import au.org.raid.inttest.client.keycloak.KeycloakClient;
import au.org.raid.inttest.config.AuthConfig;
import au.org.raid.inttest.dto.keycloak.KeycloakCredentialsFactory;
import au.org.raid.inttest.dto.keycloak.KeycloakUserFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class UserService {
    private final AuthConfig authConfig;
    private final KeycloakClient keycloakClient;
    private final KeycloakUserFactory userFactory;
    private final KeycloakCredentialsFactory credentialsFactory;

    public String createUser(final String username, final String password, final String groupName, final String... roleNames) {

        final var groups = keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).listGroups().getBody();

        assert groups != null;

        final var group = groups.stream()
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group %s not found".formatted(groupName)));

        final var user = userFactory.create(username, group.getId());

        final var userResponse = keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).createUser(user);
        final var location = userResponse.getHeaders().get("Location").get(0);
        final var userId = location.substring(location.lastIndexOf("/") + 1);

        final var credentials = credentialsFactory.createPassword(password);

        keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).resetPassword(userId, credentials);

        Arrays.stream(roleNames)
                .map(roleName ->
                        keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).findRoleByName(roleName).getBody())
                .filter(Objects::nonNull)
                .forEach(role -> keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).addUserToRole(userId, role));

        return userId;
    }

    public void deleteUser(final String userId) {
        keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).deleteUser(userId);
    }
}
