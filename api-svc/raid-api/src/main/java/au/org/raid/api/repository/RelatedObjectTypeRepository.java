package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RelatedObjectTypeRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.RelatedObjectType.RELATED_OBJECT_TYPE;

@Repository
@RequiredArgsConstructor
public class RelatedObjectTypeRepository {
    private final DSLContext dslContext;

    public Optional<RelatedObjectTypeRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(RELATED_OBJECT_TYPE.fields())
                .from(RELATED_OBJECT_TYPE)
                .where(RELATED_OBJECT_TYPE.URI.eq(uri).and(RELATED_OBJECT_TYPE.SCHEMA_ID.eq(schemaId))).
                fetchOptional(record -> new RelatedObjectTypeRecord()
                        .setSchemaId(RELATED_OBJECT_TYPE.SCHEMA_ID.getValue(record))
                        .setUri(RELATED_OBJECT_TYPE.URI.getValue(record))
                        .setName(RELATED_OBJECT_TYPE.NAME.getValue(record))
                        .setDescription(RELATED_OBJECT_TYPE.DESCRIPTION.getValue(record))
                );
    }
}