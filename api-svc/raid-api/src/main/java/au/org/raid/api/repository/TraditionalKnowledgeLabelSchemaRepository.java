package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.TraditionalKnowledgeLabelSchemaRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.TraditionalKnowledgeLabelSchema.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA;

@Repository
@RequiredArgsConstructor
public class TraditionalKnowledgeLabelSchemaRepository {
    private final DSLContext dslContext;
    public Optional<TraditionalKnowledgeLabelSchemaRecord> findByUri(final String uri) {
        return dslContext.select(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.fields())
                .from(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA)
                .where(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.URI.eq(uri))
                .fetchOptional(record -> new TraditionalKnowledgeLabelSchemaRecord()
                        .setId(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.ID.getValue(record))
                        .setUri(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.URI.getValue(record))
                );
    }
}
