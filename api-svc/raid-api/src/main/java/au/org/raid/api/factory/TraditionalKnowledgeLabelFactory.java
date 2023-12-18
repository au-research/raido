package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabelBlock;
import org.springframework.stereotype.Component;

@Component
public class TraditionalKnowledgeLabelFactory {
    public TraditionalKnowledgeLabel create(final TraditionalKnowledgeLabelBlock traditionalKnowledgeLabelBlock) {
        if (traditionalKnowledgeLabelBlock == null) {
            return null;
        }

        return new TraditionalKnowledgeLabel()
                .schemaUri(traditionalKnowledgeLabelBlock.getTraditionalKnowledgeLabelSchemeUri());
    }

    public TraditionalKnowledgeLabel create(final String id, final String schemaUri) {
        return new TraditionalKnowledgeLabel()
                .id(id)
                .schemaUri(schemaUri);
    }
}