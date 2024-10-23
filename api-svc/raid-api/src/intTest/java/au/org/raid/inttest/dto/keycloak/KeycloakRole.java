package au.org.raid.inttest.dto.keycloak;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class KeycloakRole {
    private String id;
    private String name;
    private String description;
    private boolean composite;
    private boolean clientRole;
    private String containerId;
    private Map<String, List<String>> attributes;
}
