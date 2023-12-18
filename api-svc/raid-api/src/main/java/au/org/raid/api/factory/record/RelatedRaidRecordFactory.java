package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RelatedRaidRecord;
import org.springframework.stereotype.Component;

@Component
public class RelatedRaidRecordFactory {
    public RelatedRaidRecord create(final String handle, final String relatedRaidHandle, final Integer relatedRaidTypeId) {
        return new RelatedRaidRecord()
                .setHandle(handle)
                .setRelatedRaidHandle(relatedRaidHandle)
                .setRelatedRaidTypeId(relatedRaidTypeId);
    }
}
