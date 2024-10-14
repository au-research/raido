package au.org.raid.api.service.keycloak.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminRaidsRequest {
    private String userId;
    private String handle;
}
