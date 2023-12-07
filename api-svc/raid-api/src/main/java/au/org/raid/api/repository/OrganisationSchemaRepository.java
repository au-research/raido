package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.OrganisationSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.OrganisationSchema.ORGANISATION_SCHEMA;

@Repository
@RequiredArgsConstructor
public class OrganisationSchemaRepository {
    private final DSLContext dslContext;
    public Optional<OrganisationSchemaRecord> findByUri(final String uri) {
        return dslContext.select(ORGANISATION_SCHEMA.fields())
                .from(ORGANISATION_SCHEMA)
                .where(ORGANISATION_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new OrganisationSchemaRecord()
                        .setId(ORGANISATION_SCHEMA.ID.getValue(record))
                        .setUri(ORGANISATION_SCHEMA.URI.getValue(record))
                );
    }
}
