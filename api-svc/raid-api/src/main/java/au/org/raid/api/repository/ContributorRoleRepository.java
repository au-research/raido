package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.ContributorRoleRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.ContributorRole.CONTRIBUTOR_ROLE;

@Repository
@RequiredArgsConstructor
public class ContributorRoleRepository {
    private final DSLContext dslContext;

    public Optional<ContributorRoleRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(CONTRIBUTOR_ROLE.fields())
                .from(CONTRIBUTOR_ROLE)
                .where(CONTRIBUTOR_ROLE.URI.eq(uri)
                        .and(CONTRIBUTOR_ROLE.SCHEMA_ID.eq(schemaId)))
                .fetchOptional(record -> new ContributorRoleRecord()
                        .setSchemaId(CONTRIBUTOR_ROLE.SCHEMA_ID.getValue(record))
                        .setUri(CONTRIBUTOR_ROLE.URI.getValue(record))
                );
    }
}