package au.org.raid.iam.provider.raid;

import lombok.Data;

@Data
public class RaidUserPermissionsRequest {
    private String userId;
    private String handle;
}
