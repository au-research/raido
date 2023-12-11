package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidSubjectRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidSubject.RAID_SUBJECT;

@Repository
@RequiredArgsConstructor
public class RaidSubjectRepository {
    private final DSLContext dslContext;

    public RaidSubjectRecord create(final RaidSubjectRecord record) {
        return dslContext.insertInto(RAID_SUBJECT)
                .set(RAID_SUBJECT.RAID_NAME, record.getRaidName())
                .set(RAID_SUBJECT.SUBJECT_TYPE_ID, record.getSubjectTypeId())
                .returning()
                .fetchOne();
    }
}
