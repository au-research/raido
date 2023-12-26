package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.RelatedRaid;
import au.org.raid.idl.raidv2.model.RelatedRaidType;
import org.springframework.stereotype.Component;

@Component
public class RelatedRaidFactory {
    public RelatedRaid create(final String id, final RelatedRaidType type) {
        return new RelatedRaid()
                .id(id)
                .type(type);
    }
}