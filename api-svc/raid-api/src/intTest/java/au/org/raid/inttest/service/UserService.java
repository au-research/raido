package au.org.raid.inttest.service;

import au.org.raid.inttest.client.keycloak.KeycloakClient;
import au.org.raid.inttest.config.AuthConfig;
import au.org.raid.inttest.dto.UserContext;
import au.org.raid.inttest.dto.keycloak.KeycloakCredentialsFactory;
import au.org.raid.inttest.dto.keycloak.KeycloakUserFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserService {
    private final AuthConfig authConfig;
    private final KeycloakClient keycloakClient;
    private final KeycloakUserFactory userFactory;
    private final KeycloakCredentialsFactory credentialsFactory;
    private final TokenService tokenService;

    public UserContext createUser(final String groupName, final String... roleNames) {
        final var username = UUID.randomUUID().toString();
        final var password = UUID.randomUUID().toString();

        final var groups = keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).listGroups().getBody();

        assert groups != null;

        final var group = groups.stream()
                .filter(g -> g.getName().equals(groupName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Group %s not found".formatted(groupName)));

        final var user = userFactory.create(username, group.getId());

        final var userResponse = keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).createUser(user);
        final var location = userResponse.getHeaders().get("Location").get(0);
        log.info("User created at {}", location);
        final var userId = location.substring(location.lastIndexOf("/") + 1);
        log.info("User id = {}", userId);

        final var credentials = credentialsFactory.createPassword(password);

        keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).resetPassword(userId, credentials);

        Arrays.stream(roleNames)
                .map(roleName ->
                        keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).findRoleByName(roleName).getBody())
                .filter(Objects::nonNull)
                .forEach(role -> keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).addUserToRole(userId, Collections.singletonList(role)));

        final var token = tokenService.getUserToken(username, password);

        return UserContext.builder()
                .token(token)
                .id(userId)
                .username(username)
                .password(password)
                .build();
    }

    public void deleteUser(final String userId) {
        try {
            keycloakClient.keycloakApi(authConfig.getIntegrationTestClient()).deleteUser(userId);
        } catch (Exception e) {
            log.error("Failed to delete user", e);

        }
    }
}
