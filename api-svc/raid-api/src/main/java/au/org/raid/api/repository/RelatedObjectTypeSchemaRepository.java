package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedObjectTypeSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.RelatedObjectTypeSchema.RELATED_OBJECT_TYPE_SCHEMA;

@Repository
@RequiredArgsConstructor
public class RelatedObjectTypeSchemaRepository {
    private final DSLContext dslContext;

    public Optional<RelatedObjectTypeSchemaRecord> findByUri(final String uri) {
        return dslContext.select(RELATED_OBJECT_TYPE_SCHEMA.fields())
                .from(RELATED_OBJECT_TYPE_SCHEMA)
                .where(RELATED_OBJECT_TYPE_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new RelatedObjectTypeSchemaRecord()
                        .setId(RELATED_OBJECT_TYPE_SCHEMA.ID.getValue(record))
                        .setUri(RELATED_OBJECT_TYPE_SCHEMA.URI.getValue(record))
                );
    }
}