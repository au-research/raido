package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.OrganisationRoleRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.OrganisationRole.ORGANISATION_ROLE;

@Repository
@RequiredArgsConstructor
public class OrganisationRoleRepository {
    private final DSLContext dslContext;

    public Optional<OrganisationRoleRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(ORGANISATION_ROLE.fields())
                .from(ORGANISATION_ROLE)
                .where(ORGANISATION_ROLE.URI.eq(uri)
                        .and(ORGANISATION_ROLE.SCHEMA_ID.eq(schemaId)))
                .fetchOptional(record -> new OrganisationRoleRecord()
                        .setSchemaId(ORGANISATION_ROLE.SCHEMA_ID.getValue(record))
                        .setUri(ORGANISATION_ROLE.URI.getValue(record))
                );
    }
}