package au.org.raid.inttest.dto.keycloak;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KeycloakUserFactory {
    public KeycloakUser create(final String username, final String groupId) {
        final Map<String, List<String>> attributes = new HashMap<>();

        attributes.put("activeGroupId", Collections.singletonList(groupId));

        return KeycloakUser.builder()
                .attributes(attributes)
                .username(username)
                .enabled(true)
                .build();
    }
}
