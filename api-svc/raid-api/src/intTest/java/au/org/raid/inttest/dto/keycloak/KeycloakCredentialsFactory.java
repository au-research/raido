package au.org.raid.inttest.dto.keycloak;

import org.springframework.stereotype.Component;

@Component
public class KeycloakCredentialsFactory {
    public KeycloakCredentials createPassword(final String value) {
        return KeycloakCredentials.builder()
                .temporary(false)
                .type("password")
                .value(value)
                .build();
    }
}
