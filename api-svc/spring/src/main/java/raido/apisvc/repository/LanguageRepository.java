package raido.apisvc.repository;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import raido.db.jooq.api_svc.tables.records.LanguageRecord;

import java.util.Optional;

import static raido.db.jooq.api_svc.tables.Language.LANGUAGE;

@Repository
@RequiredArgsConstructor
public class LanguageRepository {
    private final DSLContext dslContext;

    public Optional<LanguageRecord> findByIdAndSchemeId(final String id, final int schemeId) {
        return dslContext.select(LANGUAGE.fields())
                .from(LANGUAGE)
                .where(LANGUAGE.ID.eq(id))
                .and(LANGUAGE.SCHEME_ID.eq(schemeId))
                .fetchOptional(record -> new LanguageRecord()
                        .setSchemeId(LANGUAGE.SCHEME_ID.getValue(record))
                        .setId(LANGUAGE.ID.getValue(record))
                );
    }
}
