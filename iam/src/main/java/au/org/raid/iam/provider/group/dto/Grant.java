package au.org.raid.iam.provider.group.dto;

import lombok.Data;

@Data
public class Grant {
    private String userId;
    private String groupId;
}
