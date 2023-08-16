package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.TraditionalKnowledgeLabel;
import raido.idl.raidv2.model.TraditionalKnowledgeLabelBlock;

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