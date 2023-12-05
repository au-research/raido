package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.LanguageRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.Language.LANGUAGE;

@Repository
@RequiredArgsConstructor
public class LanguageRepository {
    private final DSLContext dslContext;

    public Optional<LanguageRecord> findByIdAndSchemaId(final String code, final int schemaId) {
        return dslContext.select(LANGUAGE.fields())
                .from(LANGUAGE)
                .where(LANGUAGE.CODE.eq(code))
                .and(LANGUAGE.SCHEMA_ID.eq(schemaId))
                .fetchOptional(record -> new LanguageRecord()
                        .setSchemaId(LANGUAGE.SCHEMA_ID.getValue(record))
                        .setId(LANGUAGE.ID.getValue(record))
                );
    }
}
