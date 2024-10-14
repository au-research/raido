package au.org.raid.inttest.dto.keycloak;

import lombok.Data;

@Data
public class Grant {
    private String userId;
    private String groupId;
}