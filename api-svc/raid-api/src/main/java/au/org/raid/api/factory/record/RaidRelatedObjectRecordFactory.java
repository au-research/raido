package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidRelatedObjectRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidRelatedObjectRecordFactory {
    public RaidRelatedObjectRecord create(final String handle, final Integer relatedObjectId, final Integer relatedObjectTypeId) {
        return new RaidRelatedObjectRecord()
                .setHandle(handle)
                .setRelatedObjectId(relatedObjectId)
                .setRelatedObjectTypeId(relatedObjectTypeId);
    }
}
