package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.ContributorPositionRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.ContributorPosition.CONTRIBUTOR_POSITION;

@Repository
@RequiredArgsConstructor
public class ContributorPositionRepository {
    private final DSLContext dslContext;

    public Optional<ContributorPositionRecord> findByUriAndSchemaId(final String uri, final int schemeId) {
        return dslContext.select(CONTRIBUTOR_POSITION.fields())
                .from(CONTRIBUTOR_POSITION)
                .where(CONTRIBUTOR_POSITION.URI.eq(uri)
                        .and(CONTRIBUTOR_POSITION.SCHEMA_ID.eq(schemeId)))
                .fetchOptional(record -> new ContributorPositionRecord()
                        .setSchemaId(CONTRIBUTOR_POSITION.SCHEMA_ID.getValue(record))
                        .setUri(CONTRIBUTOR_POSITION.URI.getValue(record))
                );
    }
}