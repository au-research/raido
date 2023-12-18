package au.org.raid.api.service;

import au.org.raid.api.factory.TraditionalKnowledgeLabelFactory;
import au.org.raid.api.factory.record.RaidTraditionalKnowledgeLabelRecordFactory;
import au.org.raid.api.repository.RaidTraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelSchemaRepository;
import au.org.raid.db.jooq.tables.records.TraditionalKnowledgeLabelRecord;
import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TraditionalKnowledgeLabelService {
    private final TraditionalKnowledgeLabelSchemaRepository traditionalKnowledgeLabelSchemaRepository;
    private final TraditionalKnowledgeLabelRepository traditionalKnowledgeLabelRepository;
    private final RaidTraditionalKnowledgeLabelRecordFactory raidTraditionalKnowledgeLabelRecordFactory;
    private final RaidTraditionalKnowledgeLabelRepository raidTraditionalKnowledgeLabelRepository;
    private final TraditionalKnowledgeLabelFactory traditionalKnowledgeLabelFactory;

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

    public List<TraditionalKnowledgeLabel> findAllByHandle(final String handle) {
        final var traditionalKnowledgeLabels = new ArrayList<TraditionalKnowledgeLabel>();

        final var records = raidTraditionalKnowledgeLabelRepository.findAllByHandle(handle);

        for (final var record : records) {
            String labelUri = null;
            final var id = record.getTraditionalKnowledgeLabelId();

            if (id != null) {
                final var labelRecord = traditionalKnowledgeLabelRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Traditional knowledge label not found with id %d".formatted(id)));

                labelUri = labelRecord.getUri();
            }

            final var schemaId = record.getTraditionalKnowledgeLabelSchemaId();

            final var schemaRecord = traditionalKnowledgeLabelSchemaRepository.findById(schemaId)
                    .orElseThrow(() -> new RuntimeException("Traditional knowledge label schema not found with id %d".formatted(schemaId)));

            traditionalKnowledgeLabels.add(traditionalKnowledgeLabelFactory.create(labelUri, schemaRecord.getUri()));
        }
        return traditionalKnowledgeLabels;
    }
}
