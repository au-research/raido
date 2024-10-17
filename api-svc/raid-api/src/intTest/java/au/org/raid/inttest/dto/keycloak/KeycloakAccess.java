package au.org.raid.inttest.dto.keycloak;

import lombok.Data;

@Data
public class KeycloakAccess {
    private boolean manageGroupMembership;
    private boolean view;
    private boolean mapRoles;
    private boolean impersonate;
    private boolean manage;
}
