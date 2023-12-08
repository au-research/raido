package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidRelatedObjectRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidRelatedObject.RAID_RELATED_OBJECT;

@Repository
@RequiredArgsConstructor
public class RaidRelatedObjectRepository {
    private final DSLContext dslContext;

    public RaidRelatedObjectRecord create(final RaidRelatedObjectRecord record) {
        return dslContext.insertInto(RAID_RELATED_OBJECT)
                .set(RAID_RELATED_OBJECT.RAID_NAME, record.getRaidName())
                .set(RAID_RELATED_OBJECT.RELATED_OBJECT_ID, record.getRelatedObjectId())
                .set(RAID_RELATED_OBJECT.RELATED_OBJECT_TYPE_ID, record.getRelatedObjectTypeId())
                .returning()
                .fetchOne();
    }
}
