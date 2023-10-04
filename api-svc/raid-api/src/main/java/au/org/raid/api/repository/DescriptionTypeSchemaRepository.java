package au.org.raid.api.repository;

import au.org.raid.db.jooq.api_svc.tables.records.DescriptionTypeSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.api_svc.tables.DescriptionTypeSchema.DESCRIPTION_TYPE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class DescriptionTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<DescriptionTypeSchemaRecord> findByUri(final String uri) {
        return dslContext.select(DESCRIPTION_TYPE_SCHEMA.fields()).
                from(DESCRIPTION_TYPE_SCHEMA).
                where(DESCRIPTION_TYPE_SCHEMA.URI.eq(uri)).
                fetchOptional(record -> new DescriptionTypeSchemaRecord()
                        .setId(DESCRIPTION_TYPE_SCHEMA.ID.getValue(record))
                        .setUri(DESCRIPTION_TYPE_SCHEMA.URI.getValue(record))
                );
    }
}