package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RelatedRaidRecord;
import org.springframework.stereotype.Component;

@Component
public class RelatedRaidRecordFactory {
    public RelatedRaidRecord create(final String raidName, final String relatedRaidName, final Integer relatedRaidTypeId) {
        return new RelatedRaidRecord()
                .setRaidName(raidName)
                .setRelatedRaidName(relatedRaidName)
                .setRelatedRaidTypeId(relatedRaidTypeId);
    }
}
