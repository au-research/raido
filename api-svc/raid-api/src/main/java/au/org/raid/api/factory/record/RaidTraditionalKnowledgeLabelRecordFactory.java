package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidTraditionalKnowledgeLabelRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidTraditionalKnowledgeLabelRecordFactory {
    public RaidTraditionalKnowledgeLabelRecord create(final String handle, final Integer labelId, final Integer schemaId) {
        return new RaidTraditionalKnowledgeLabelRecord()
                .setHandle(handle)
                .setTraditionalKnowledgeLabelId(labelId)
                .setTraditionalKnowledgeLabelSchemaId(schemaId);
    }
}
