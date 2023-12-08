package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedObjectCategoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.RelatedObjectCategory.RELATED_OBJECT_CATEGORY;

@Repository
@RequiredArgsConstructor
public class RelatedObjectCategoryRepository {
    private final DSLContext dslContext;

    public Optional<RelatedObjectCategoryRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(RELATED_OBJECT_CATEGORY.fields())
                .from(RELATED_OBJECT_CATEGORY)
                .where(RELATED_OBJECT_CATEGORY.URI.eq(uri)
                        .and(RELATED_OBJECT_CATEGORY.SCHEMA_ID.eq(schemaId)))
                .fetchOptional(record -> new RelatedObjectCategoryRecord()
                        .setSchemaId(RELATED_OBJECT_CATEGORY.ID.getValue(record))
                        .setSchemaId(RELATED_OBJECT_CATEGORY.SCHEMA_ID.getValue(record))
                        .setUri(RELATED_OBJECT_CATEGORY.URI.getValue(record))
                );
    }
}