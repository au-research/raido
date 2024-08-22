package au.org.raid.api.dto;

import au.org.raid.idl.raidv2.model.Contributor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaidListenerMessage {
    private String raidName;
    private String email;
    private String id;
    private boolean delete;
    private List<Contributor> contributors;
}

