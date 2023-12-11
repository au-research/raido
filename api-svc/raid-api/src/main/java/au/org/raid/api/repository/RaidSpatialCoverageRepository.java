package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidSpatialCoverageRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidSpatialCoverage.RAID_SPATIAL_COVERAGE;

@Repository
@RequiredArgsConstructor
public class RaidSpatialCoverageRepository {
    private final DSLContext dslContext;

    public RaidSpatialCoverageRecord create(final RaidSpatialCoverageRecord record) {
        return dslContext.insertInto(RAID_SPATIAL_COVERAGE)
                .set(RAID_SPATIAL_COVERAGE.ID, record.getId())
                .set(RAID_SPATIAL_COVERAGE.RAID_NAME, record.getRaidName())
                .set(RAID_SPATIAL_COVERAGE.LANGUAGE_ID, record.getLanguageId())
                .set(RAID_SPATIAL_COVERAGE.PLACE, record.getPlace())
                .set(RAID_SPATIAL_COVERAGE.SCHEMA_ID, record.getSchemaId())
                .returning()
                .fetchOne();
    }
}
