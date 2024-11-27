package au.org.raid.inttest.dto.keycloak;

import au.org.raid.inttest.dto.GroupMemberDto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group {
    private String id;
    private String name;
    private List<GroupMemberDto> members;
    private Map<String, List<String>> attributes;
    private String path;
}
