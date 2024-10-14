package au.org.raid.inttest.dto.keycloak;

import au.org.raid.inttest.dto.GroupMemberDto;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Group {
    private String id;
    private String name;
    private List<GroupMemberDto> members;
    private Map<String, List<String>> attributes;
}
