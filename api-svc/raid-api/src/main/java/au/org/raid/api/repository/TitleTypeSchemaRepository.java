package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.TitleTypeSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.TitleTypeSchema.TITLE_TYPE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class TitleTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<TitleTypeSchemaRecord> findByUri(final String uri) {
        return dslContext.select(TITLE_TYPE_SCHEMA.fields())
                .from(TITLE_TYPE_SCHEMA)
                .where(TITLE_TYPE_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new TitleTypeSchemaRecord()
                        .setId(TITLE_TYPE_SCHEMA.ID.getValue(record))
                        .setUri(TITLE_TYPE_SCHEMA.URI.getValue(record))
                );
    }
}