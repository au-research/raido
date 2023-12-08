package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidRelatedObjectCategoryRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidRelatedObjectCategoryRecordFactory {
    public RaidRelatedObjectCategoryRecord create(final Integer raidRelatedObjectId, final Integer relatedObjectCategoryId) {
        return new RaidRelatedObjectCategoryRecord()
                .setRaidRelatedObjectId(raidRelatedObjectId)
                .setRelatedObjectCategoryId(relatedObjectCategoryId);
    }
}
