package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidSubjectKeywordRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidSubjectKeywordRecordFactory {
    public RaidSubjectKeywordRecord create(final int raidSubjectId, final String keyword, final Integer languageId) {
        return new RaidSubjectKeywordRecord()
                .setRaidSubjectId(raidSubjectId)
                .setKeyword(keyword)
                .setLanguageId(languageId);
    }
}
