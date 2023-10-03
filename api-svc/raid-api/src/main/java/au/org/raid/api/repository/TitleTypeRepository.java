package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.TitleTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.TitleType.TITLE_TYPE;

@Repository
@RequiredArgsConstructor
public class TitleTypeRepository {
    private final DSLContext dslContext;

    public Optional<TitleTypeRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(TITLE_TYPE.fields())
                .from(TITLE_TYPE)
                .where(TITLE_TYPE.URI.eq(uri)
                        .and(TITLE_TYPE.SCHEMA_ID.eq(schemaId)))
                .fetchOptional(record -> new TitleTypeRecord()
                        .setSchemaId(TITLE_TYPE.SCHEMA_ID.getValue(record))
                        .setUri(TITLE_TYPE.URI.getValue(record))
                );
    }
}