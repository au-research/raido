package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.TraditionalKnowledgeLabelRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static au.org.raid.db.jooq.tables.TraditionalKnowledgeLabel.TRADITIONAL_KNOWLEDGE_LABEL;

@Repository
@RequiredArgsConstructor
public class TraditionalKnowledgeLabelRepository {
    private final DSLContext dslContext;

    public Optional<TraditionalKnowledgeLabelRecord> findByUriAndSchemaId(final String uri, final int schemaId) {
        return dslContext.select(TRADITIONAL_KNOWLEDGE_LABEL.fields())
                .from(TRADITIONAL_KNOWLEDGE_LABEL)
                .where(TRADITIONAL_KNOWLEDGE_LABEL.URI.eq(uri)
                        .and(TRADITIONAL_KNOWLEDGE_LABEL.SCHEMA_ID.eq(schemaId)))
                .fetchOptional(record -> new TraditionalKnowledgeLabelRecord()
                        .setSchemaId(TRADITIONAL_KNOWLEDGE_LABEL.SCHEMA_ID.getValue(record))
                        .setUri(TRADITIONAL_KNOWLEDGE_LABEL.URI.getValue(record))
                );
    }
}
