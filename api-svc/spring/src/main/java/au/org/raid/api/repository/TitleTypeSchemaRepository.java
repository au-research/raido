package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.TitleTypeSchemeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.TitleTypeScheme.TITLE_TYPE_SCHEME;

@Repository
@RequiredArgsConstructor
public class TitleTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<TitleTypeSchemeRecord> findByUri(final String uri) {
        return dslContext.select(TITLE_TYPE_SCHEME.fields())
                .from(TITLE_TYPE_SCHEME)
                .where(TITLE_TYPE_SCHEME.URI.eq(uri))
                .fetchOptional(record -> new TitleTypeSchemeRecord()
                        .setId(TITLE_TYPE_SCHEME.ID.getValue(record))
                        .setUri(TITLE_TYPE_SCHEME.URI.getValue(record))
                );
    }
}