package au.org.raid.iam.provider.raid;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RaidPermissionsDto {
    private String userId;
    private String handle;
}
