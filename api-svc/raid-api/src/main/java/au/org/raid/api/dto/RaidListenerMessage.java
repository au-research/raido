package au.org.raid.api.dto;

import au.org.raid.idl.raidv2.model.Contributor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaidListenerMessage {
    private String raidName;
    private Contributor contributor;
    private boolean delete;
}

