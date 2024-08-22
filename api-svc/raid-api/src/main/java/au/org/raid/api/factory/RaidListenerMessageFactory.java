package au.org.raid.api.factory;

import au.org.raid.api.dto.RaidListenerMessage;
import au.org.raid.idl.raidv2.model.Contributor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RaidListenerMessageFactory {
    public RaidListenerMessage create(final String raidName, final String email, final String id, final List<Contributor> contributors, final boolean delete) {
        return RaidListenerMessage.builder()
                .raidName(raidName)
                .email(email)
                .id(id)
                .contributors(contributors)
                .delete(delete)
                .build();
    }
}
