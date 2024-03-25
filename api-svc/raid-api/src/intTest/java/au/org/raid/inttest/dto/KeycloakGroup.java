package au.org.raid.inttest.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class KeycloakGroup {
    private String id;
    private String name;
    private List<GroupMemberDto> members;
    private Map<String, List<String>> attributes;
}
