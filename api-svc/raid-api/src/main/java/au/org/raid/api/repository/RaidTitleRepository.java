package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidTitleRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidTitle.RAID_TITLE;

@Repository
@RequiredArgsConstructor
public class RaidTitleRepository {
    private final DSLContext dslContext;
    public RaidTitleRecord create(final RaidTitleRecord record) {
        return dslContext.insertInto(RAID_TITLE)
                .set(RAID_TITLE.RAID_NAME, record.getRaidName())
                .set(RAID_TITLE.TITLE_TYPE_ID, record.getTitleTypeId())
                .set(RAID_TITLE.START_DATE, record.getStartDate())
                .set(RAID_TITLE.END_DATE, record.getEndDate())
                .set(RAID_TITLE.TEXT, record.getText())
                .returning()
                .fetchOne();
    }
}
