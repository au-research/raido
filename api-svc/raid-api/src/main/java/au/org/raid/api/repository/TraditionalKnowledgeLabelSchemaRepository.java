package au.org.raid.api.repository;

import au.org.raid.db.jooq.enums.SchemaStatus;
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
        return dslContext.selectFrom(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA)
                .where(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.URI.eq(uri))
                .fetchOptional();
    }

    public Optional<TraditionalKnowledgeLabelSchemaRecord> findActiveByUri(final String uri) {
        return dslContext.selectFrom(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA)
                .where(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.URI.eq(uri))
                .and(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.STATUS.eq(SchemaStatus.active))
                .fetchOptional();
    }

    public Optional<TraditionalKnowledgeLabelSchemaRecord> findById(final Integer id) {
        return dslContext.selectFrom(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA)
                .where(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA.ID.eq(id))
                .fetchOptional();
    }
}
