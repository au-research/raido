package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidRelatedObjectRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidRelatedObject.RAID_RELATED_OBJECT;

@Repository
@RequiredArgsConstructor
public class RaidRelatedObjectRepository {
    private final DSLContext dslContext;

    public RaidRelatedObjectRecord create(final RaidRelatedObjectRecord record) {
        return dslContext.insertInto(RAID_RELATED_OBJECT)
                .set(RAID_RELATED_OBJECT.HANDLE, record.getHandle())
                .set(RAID_RELATED_OBJECT.RELATED_OBJECT_ID, record.getRelatedObjectId())
                .set(RAID_RELATED_OBJECT.RELATED_OBJECT_TYPE_ID, record.getRelatedObjectTypeId())
                .returning()
                .fetchOne(r -> new RaidRelatedObjectRecord()
                        .setId(RAID_RELATED_OBJECT.ID.getValue(r))
                        .setRelatedObjectTypeId(RAID_RELATED_OBJECT.RELATED_OBJECT_TYPE_ID.getValue(r))
                        .setRelatedObjectId(RAID_RELATED_OBJECT.RELATED_OBJECT_ID.getValue(r))
                        .setHandle(RAID_RELATED_OBJECT.HANDLE.getValue(r))
                );
    }

    public List<RaidRelatedObjectRecord> findAllByHandle(final String handle) {
        return dslContext.selectFrom(RAID_RELATED_OBJECT)
                .where(RAID_RELATED_OBJECT.HANDLE.eq(handle))
                .fetch();
    }
}