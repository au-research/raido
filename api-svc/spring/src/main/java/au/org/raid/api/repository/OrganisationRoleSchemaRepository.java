package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.OrganisationRoleSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.OrganisationRoleSchema.ORGANISATION_ROLE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class OrganisationRoleSchemaRepository {
    private final DSLContext dslContext;

    public Optional<OrganisationRoleSchemaRecord> findByUri(final String uri) {
        return dslContext.select(ORGANISATION_ROLE_SCHEMA.fields())
                .from(ORGANISATION_ROLE_SCHEMA)
                .where(ORGANISATION_ROLE_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new OrganisationRoleSchemaRecord()
                        .setId(ORGANISATION_ROLE_SCHEMA.ID.getValue(record))
                        .setUri(ORGANISATION_ROLE_SCHEMA.URI.getValue(record))
                );
    }
}