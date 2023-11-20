package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.ContributorPositionSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.ContributorPositionSchema.CONTRIBUTOR_POSITION_SCHEMA;

@Repository
@RequiredArgsConstructor
public class ContributorPositionSchemaRepository {
    private final DSLContext dslContext;

    public Optional<ContributorPositionSchemaRecord> findByUri(final String uri) {
        return dslContext.select(CONTRIBUTOR_POSITION_SCHEMA.fields())
                .from(CONTRIBUTOR_POSITION_SCHEMA)
                .where(CONTRIBUTOR_POSITION_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new ContributorPositionSchemaRecord()
                        .setId(CONTRIBUTOR_POSITION_SCHEMA.ID.getValue(record))
                        .setUri(CONTRIBUTOR_POSITION_SCHEMA.URI.getValue(record))
                );
    }
}