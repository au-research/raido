package au.org.raid.api.factory;

import au.org.raid.api.dto.RaidListenerMessage;
import au.org.raid.idl.raidv2.model.Contributor;
import org.springframework.stereotype.Component;

@Component
public class RaidListenerMessageFactory {
    public RaidListenerMessage create(final String raidName, final Contributor contributor) {
        return RaidListenerMessage.builder()
                .raidName(raidName)
                .contributor(contributor)
                .build();
    }
}
