package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.ContributorPositionSchemeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.ContributorPositionScheme.CONTRIBUTOR_POSITION_SCHEME;

@Repository
@RequiredArgsConstructor
public class ContributorPositionSchemaRepository {
    private final DSLContext dslContext;

    public Optional<ContributorPositionSchemeRecord> findByUri(final String uri) {
        return dslContext.select(CONTRIBUTOR_POSITION_SCHEME.fields())
                .from(CONTRIBUTOR_POSITION_SCHEME)
                .where(CONTRIBUTOR_POSITION_SCHEME.URI.eq(uri))
                .fetchOptional(record -> new ContributorPositionSchemeRecord()
                        .setId(CONTRIBUTOR_POSITION_SCHEME.ID.getValue(record))
                        .setUri(CONTRIBUTOR_POSITION_SCHEME.URI.getValue(record))
                );
    }
}