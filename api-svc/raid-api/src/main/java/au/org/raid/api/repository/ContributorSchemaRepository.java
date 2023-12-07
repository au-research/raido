package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.ContributorSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.ContributorSchema.CONTRIBUTOR_SCHEMA;

@Repository
@RequiredArgsConstructor
public class ContributorSchemaRepository {
    private final DSLContext dslContext;

    public Optional<ContributorSchemaRecord> findByUri(final String uri) {
        return dslContext.select(CONTRIBUTOR_SCHEMA.fields())
                .from(CONTRIBUTOR_SCHEMA)
                .where(CONTRIBUTOR_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new ContributorSchemaRecord()
                        .setId(CONTRIBUTOR_SCHEMA.ID.getValue(record))
                        .setUri(CONTRIBUTOR_SCHEMA.URI.getValue(record))
                );
    }
}
