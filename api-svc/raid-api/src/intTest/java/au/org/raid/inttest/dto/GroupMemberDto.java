package au.org.raid.inttest.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class GroupMemberDto {
    private String id;
    private List<String> roles;
    private Map<String, List<String>> attributes;
}
