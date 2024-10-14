package au.org.raid.inttest.dto.keycloak;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RaidUserPermissionsRequest {
    private String userId;
    private String handle;
}
