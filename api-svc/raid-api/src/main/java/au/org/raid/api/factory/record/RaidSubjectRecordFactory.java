package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidSubjectRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidSubjectRecordFactory {
    public RaidSubjectRecord create(final String raidName, final String subjectTypeId) {
        return new RaidSubjectRecord()
                .setRaidName(raidName)
                .setSubjectTypeId(subjectTypeId);
    }
}
