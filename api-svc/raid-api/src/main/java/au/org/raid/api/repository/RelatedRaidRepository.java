package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedRaidRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RelatedRaid.RELATED_RAID;

@Repository
@RequiredArgsConstructor
public class RelatedRaidRepository {
    private final DSLContext dslContext;

    public RelatedRaidRecord create(final RelatedRaidRecord record) {
        return dslContext.insertInto(RELATED_RAID)
                .set(RELATED_RAID.RAID_NAME, record.getRaidName())
                .set(RELATED_RAID.RELATED_RAID_NAME, record.getRelatedRaidName())
                .set(RELATED_RAID.RELATED_RAID_TYPE_ID, record.getRelatedRaidTypeId())
                .returning()
                .fetchOne();
    }
}
