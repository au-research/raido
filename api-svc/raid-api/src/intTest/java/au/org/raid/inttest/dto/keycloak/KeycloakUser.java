package au.org.raid.inttest.dto.keycloak;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class KeycloakUser {
    private String id;
    private String username;
    private boolean emailVerified;
    private Map<String, List<String>> attributes;
    private long createdTimestamp;
    private boolean enabled;
    private boolean totp;
    private List<Object> disableableCredentialTypes;
    private List<Object> requiredActions;
    private long notBefore;
    private KeycloakAccess access;

}
