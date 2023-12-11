package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidSubjectKeywordRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidSubjectKeyword.RAID_SUBJECT_KEYWORD;

@Repository
@RequiredArgsConstructor
public class RaidSubjectKeywordRepository {
    private final DSLContext dslContext;

    public RaidSubjectKeywordRecord create(final RaidSubjectKeywordRecord record) {
        return dslContext.insertInto(RAID_SUBJECT_KEYWORD)
                .set(RAID_SUBJECT_KEYWORD.KEYWORD, record.getKeyword())
                .set(RAID_SUBJECT_KEYWORD.RAID_SUBJECT_ID, record.getRaidSubjectId())
                .set(RAID_SUBJECT_KEYWORD.LANGUAGE_ID, record.getLanguageId())
                .returning()
                .fetchOne();
    }
}
