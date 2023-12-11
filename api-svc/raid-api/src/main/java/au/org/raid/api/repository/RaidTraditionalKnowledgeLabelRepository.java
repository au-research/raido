package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidTraditionalKnowledgeLabelRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidTraditionalKnowledgeLabel.RAID_TRADITIONAL_KNOWLEDGE_LABEL;

@Repository
@RequiredArgsConstructor
public class RaidTraditionalKnowledgeLabelRepository {
    private final DSLContext dslContext;

    public RaidTraditionalKnowledgeLabelRecord create(final RaidTraditionalKnowledgeLabelRecord record) {
        return dslContext.insertInto(RAID_TRADITIONAL_KNOWLEDGE_LABEL)
                .set(RAID_TRADITIONAL_KNOWLEDGE_LABEL.RAID_NAME, record.getRaidName())
                .set(RAID_TRADITIONAL_KNOWLEDGE_LABEL.TRADITIONAL_KNOWLEDGE_LABEL_ID, record.getTraditionalKnowledgeLabelId())
                .set(RAID_TRADITIONAL_KNOWLEDGE_LABEL.TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_ID, record.getTraditionalKnowledgeLabelSchemaId())
                .returning()
                .fetchOne();
    }
}
