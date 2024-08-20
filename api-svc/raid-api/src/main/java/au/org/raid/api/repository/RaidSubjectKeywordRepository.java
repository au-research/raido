package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidSubjectKeywordRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    public List<RaidSubjectKeywordRecord> findAllByRaidSubjectId(final Integer raidSubjectId) {
        return dslContext.selectFrom(RAID_SUBJECT_KEYWORD)
                .where(RAID_SUBJECT_KEYWORD.RAID_SUBJECT_ID.eq(raidSubjectId))
                .fetch();
    }

    public void deleteByRaidSubjectId(final Integer raidSubjectId) {
        dslContext.deleteFrom(RAID_SUBJECT_KEYWORD)
                .where(RAID_SUBJECT_KEYWORD.RAID_SUBJECT_ID.eq(raidSubjectId))
                .execute();
    }
}
