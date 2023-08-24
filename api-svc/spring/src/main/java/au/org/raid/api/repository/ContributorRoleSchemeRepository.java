package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.ContributorRoleSchemeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.ContributorRoleScheme.CONTRIBUTOR_ROLE_SCHEME;

@Repository
@RequiredArgsConstructor
public class ContributorRoleSchemeRepository {
    private final DSLContext dslContext;

    public Optional<ContributorRoleSchemeRecord> findByUri(final String uri) {
        return dslContext.select(CONTRIBUTOR_ROLE_SCHEME.fields())
                .from(CONTRIBUTOR_ROLE_SCHEME)
                .where(CONTRIBUTOR_ROLE_SCHEME.URI.eq(uri))
                .fetchOptional(record -> new ContributorRoleSchemeRecord()
                        .setId(CONTRIBUTOR_ROLE_SCHEME.ID.getValue(record))
                        .setUri(CONTRIBUTOR_ROLE_SCHEME.URI.getValue(record))
                );
    }
}