package au.org.raid.api.service;

import au.org.raid.api.factory.record.RaidTraditionalKnowledgeLabelRecordFactory;
import au.org.raid.api.repository.RaidTraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelSchemaRepository;
import au.org.raid.db.jooq.tables.records.TraditionalKnowledgeLabelRecord;
import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TraditionalKnowledgeLabelService {
    private final TraditionalKnowledgeLabelSchemaRepository traditionalKnowledgeLabelSchemaRepository;
    private final TraditionalKnowledgeLabelRepository traditionalKnowledgeLabelRepository;
    private final RaidTraditionalKnowledgeLabelRecordFactory raidTraditionalKnowledgeLabelRecordFactory;
    private final RaidTraditionalKnowledgeLabelRepository raidTraditionalKnowledgeLabelRepository;

    public void create(final List<TraditionalKnowledgeLabel> traditionalKnowledgeLabels, final String handle) {
        if (traditionalKnowledgeLabels == null) {
            return;
        }

        for (final var traditionalKnowledgeLabel : traditionalKnowledgeLabels) {
            final var traditionalKnowledgeLabelSchemaRecord =
                    traditionalKnowledgeLabelSchemaRepository.findByUri(traditionalKnowledgeLabel.getSchemaUri())
                            .orElseThrow(() -> new RuntimeException(
                                    "Traditional knowledge label schema not found %s".formatted(
                                            traditionalKnowledgeLabel.getSchemaUri())));

            final var traditionalKnowledgeLabelId = traditionalKnowledgeLabelRepository
                    .findByUriAndSchemaId(traditionalKnowledgeLabel.getId(), traditionalKnowledgeLabelSchemaRecord.getId())
                    .map(TraditionalKnowledgeLabelRecord::getId)
                    .orElse(null);

            final var raidTraditionalKnowledgeLabelRecord = raidTraditionalKnowledgeLabelRecordFactory.create(
                    handle, traditionalKnowledgeLabelId, traditionalKnowledgeLabelSchemaRecord.getId()
            );

            raidTraditionalKnowledgeLabelRepository.create(raidTraditionalKnowledgeLabelRecord);
        }
    }
}
