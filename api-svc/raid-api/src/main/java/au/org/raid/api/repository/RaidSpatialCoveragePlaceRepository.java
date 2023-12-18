package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidSpatialCoveragePlaceRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidSpatialCoveragePlace.RAID_SPATIAL_COVERAGE_PLACE;

@Repository
@RequiredArgsConstructor
public class RaidSpatialCoveragePlaceRepository {
    private final DSLContext dslContext;

    public List<RaidSpatialCoveragePlaceRecord> findAllByRaidSpatialCoverageId(final Integer id) {
        return dslContext.selectFrom(RAID_SPATIAL_COVERAGE_PLACE)
                .where(RAID_SPATIAL_COVERAGE_PLACE.RAID_SPATIAL_COVERAGE_ID.eq(id))
                .fetch();
    }

    public RaidSpatialCoveragePlaceRecord create(final RaidSpatialCoveragePlaceRecord record) {
        return dslContext.insertInto(RAID_SPATIAL_COVERAGE_PLACE)
                .set(RAID_SPATIAL_COVERAGE_PLACE.PLACE, record.getPlace())
                .set(RAID_SPATIAL_COVERAGE_PLACE.RAID_SPATIAL_COVERAGE_ID, record.getRaidSpatialCoverageId())
                .set(RAID_SPATIAL_COVERAGE_PLACE.LANGUAGE_ID, record.getLanguageId())
                .returning()
                .fetchOne();
    }
}
