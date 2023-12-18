package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidRelatedObjectCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidRelatedObjectCategory.RAID_RELATED_OBJECT_CATEGORY;

@Repository
@RequiredArgsConstructor
public class RaidRelatedObjectCategoryRepository {
    private final DSLContext dslContext;

    public RaidRelatedObjectCategoryRecord create(final RaidRelatedObjectCategoryRecord record) {
        return dslContext.insertInto(RAID_RELATED_OBJECT_CATEGORY)
                .set(RAID_RELATED_OBJECT_CATEGORY.RAID_RELATED_OBJECT_ID, record.getRaidRelatedObjectId())
                .set(RAID_RELATED_OBJECT_CATEGORY.RELATED_OBJECT_CATEGORY_ID, record.getRelatedObjectCategoryId())
                .returning()
                .fetchOne();
    }

    public List<RaidRelatedObjectCategoryRecord> findAllByRaidRelatedObjectId(final Integer raidRelatedObjectId) {
        return dslContext.selectFrom(RAID_RELATED_OBJECT_CATEGORY)
                .where(RAID_RELATED_OBJECT_CATEGORY.RAID_RELATED_OBJECT_ID.eq(raidRelatedObjectId))
                .fetch();
    }
}
