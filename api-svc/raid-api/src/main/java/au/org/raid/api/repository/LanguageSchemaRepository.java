package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.LanguageSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.LanguageSchema.LANGUAGE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class LanguageSchemaRepository {
    private final DSLContext dslContext;

    public Optional<LanguageSchemaRecord> findByUri(final String uri) {
        return dslContext.select(LANGUAGE_SCHEMA.fields()).
                from(LANGUAGE_SCHEMA).
                where(LANGUAGE_SCHEMA.URI.eq(uri)).
                fetchOptional(record -> new LanguageSchemaRecord()
                        .setId(LANGUAGE_SCHEMA.ID.getValue(record))
                        .setUri(LANGUAGE_SCHEMA.URI.getValue(record))
                );
    }

}
