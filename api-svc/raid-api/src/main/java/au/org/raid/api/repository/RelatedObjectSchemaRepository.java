package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedObjectSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.RelatedObjectSchema.RELATED_OBJECT_SCHEMA;

@Repository
@RequiredArgsConstructor
public class RelatedObjectSchemaRepository {
    private final DSLContext dslContext;

    public Optional<RelatedObjectSchemaRecord> findByUri(final String uri) {
        return dslContext.select(RELATED_OBJECT_SCHEMA.fields())
                .from(RELATED_OBJECT_SCHEMA)
                .where(RELATED_OBJECT_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new RelatedObjectSchemaRecord()
                        .setId(RELATED_OBJECT_SCHEMA.ID.getValue(record))
                        .setUri(RELATED_OBJECT_SCHEMA.URI.getValue(record))
                );
    }
}
