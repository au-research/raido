package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.DescriptionTypeSchemeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.DescriptionTypeScheme.DESCRIPTION_TYPE_SCHEME;

@Repository
@RequiredArgsConstructor
public class DescriptionTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<DescriptionTypeSchemeRecord> findByUri(final String uri) {
        return dslContext.select(DESCRIPTION_TYPE_SCHEME.fields()).
                from(DESCRIPTION_TYPE_SCHEME).
                where(DESCRIPTION_TYPE_SCHEME.URI.eq(uri)).
                fetchOptional(record -> new DescriptionTypeSchemeRecord()
                        .setId(DESCRIPTION_TYPE_SCHEME.ID.getValue(record))
                        .setUri(DESCRIPTION_TYPE_SCHEME.URI.getValue(record))
                );
    }
}