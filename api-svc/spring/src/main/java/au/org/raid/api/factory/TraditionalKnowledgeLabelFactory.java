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
            .schemeUri(traditionalKnowledgeLabelBlock.getTraditionalKnowledgeLabelSchemeUri());
    }
}