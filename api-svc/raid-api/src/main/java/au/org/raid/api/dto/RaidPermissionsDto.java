package au.org.raid.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RaidPermissionsDto {
    private boolean read;
    private boolean write;
}
