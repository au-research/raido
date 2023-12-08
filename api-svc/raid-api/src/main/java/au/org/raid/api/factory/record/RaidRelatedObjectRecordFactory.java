package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidRelatedObjectRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidRelatedObjectRecordFactory {

    public RaidRelatedObjectRecord create(final String raidName, final Integer relatedObjectId, final Integer relatedObjectTypeId) {
        return new RaidRelatedObjectRecord()
                .setRaidName(raidName)
                .setRelatedObjectId(relatedObjectId)
                .setRelatedObjectTypeId(relatedObjectTypeId);
    }
}
