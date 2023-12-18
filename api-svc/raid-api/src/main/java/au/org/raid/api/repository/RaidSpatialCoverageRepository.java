package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidSpatialCoverageRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidSpatialCoverage.RAID_SPATIAL_COVERAGE;

@Repository
@RequiredArgsConstructor
public class RaidSpatialCoverageRepository {
    private final DSLContext dslContext;

    public RaidSpatialCoverageRecord create(final RaidSpatialCoverageRecord record) {
        return dslContext.insertInto(RAID_SPATIAL_COVERAGE)
                .set(RAID_SPATIAL_COVERAGE.URI, record.getUri())
                .set(RAID_SPATIAL_COVERAGE.HANDLE, record.getHandle())
                .set(RAID_SPATIAL_COVERAGE.SCHEMA_ID, record.getSchemaId())
                .returning()
                .fetchOne();
    }

    public List<RaidSpatialCoverageRecord> findAllByHandle(final String handle) {
        return dslContext.selectFrom(RAID_SPATIAL_COVERAGE)
                .where(RAID_SPATIAL_COVERAGE.HANDLE.eq(handle))
                .fetch();
    }
}
