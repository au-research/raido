package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.SpatialCoverageSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.SpatialCoverageSchema.SPATIAL_COVERAGE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class SpatialCoverageSchemaRepository {
    private final DSLContext dslContext;

    public Optional<SpatialCoverageSchemaRecord> findByUri(final String uri) {
        return dslContext.select(SPATIAL_COVERAGE_SCHEMA.fields())
                .from(SPATIAL_COVERAGE_SCHEMA)
                .where(SPATIAL_COVERAGE_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new SpatialCoverageSchemaRecord()
                        .setId(SPATIAL_COVERAGE_SCHEMA.ID.getValue(record))
                        .setUri(SPATIAL_COVERAGE_SCHEMA.URI.getValue(record))
                );
    }
}
