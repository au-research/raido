package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedObjectCategorySchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.RelatedObjectCategorySchema.RELATED_OBJECT_CATEGORY_SCHEMA;

@Repository
@RequiredArgsConstructor
public class RelatedObjectCategorySchemaRepository {
    private final DSLContext dslContext;

    public Optional<RelatedObjectCategorySchemaRecord> findByUri(final String uri) {
        return dslContext.select(RELATED_OBJECT_CATEGORY_SCHEMA.fields())
                .from(RELATED_OBJECT_CATEGORY_SCHEMA)
                .where(RELATED_OBJECT_CATEGORY_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new RelatedObjectCategorySchemaRecord()
                        .setId(RELATED_OBJECT_CATEGORY_SCHEMA.ID.getValue(record))
                        .setUri(RELATED_OBJECT_CATEGORY_SCHEMA.URI.getValue(record))
                );
    }
}