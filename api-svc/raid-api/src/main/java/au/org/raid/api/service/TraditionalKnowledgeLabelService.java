package au.org.raid.api.service;

import au.org.raid.api.exception.TraditionalKnowledgeLabelNotFoundException;
import au.org.raid.api.exception.TraditionalKnowledgeLabelSchemaNotFoundException;
import au.org.raid.api.factory.TraditionalKnowledgeLabelFactory;
import au.org.raid.api.factory.record.RaidTraditionalKnowledgeLabelRecordFactory;
import au.org.raid.api.repository.RaidTraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelSchemaRepository;
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

    public void create(final List<TraditionalKnowledgeLabel> labels, final String handle) {
        if (labels == null) {
            return;
        }

        for (final var label : labels) {
            final var schemaRecord =
                    traditionalKnowledgeLabelSchemaRepository.findByUri(label.getSchemaUri())
                            .orElseThrow(() ->
                                    new TraditionalKnowledgeLabelSchemaNotFoundException(label.getSchemaUri()));

            Integer id = null;

            if (label.getId() != null) {
                final var record = traditionalKnowledgeLabelRepository
                        .findByUriAndSchemaId(label.getId(), schemaRecord.getId())
                        .orElseThrow(() -> new TraditionalKnowledgeLabelNotFoundException(label.getId()));

                id = record.getId();
            }

            final var raidTraditionalKnowledgeLabelRecord = raidTraditionalKnowledgeLabelRecordFactory.create(
                    handle, id, schemaRecord.getId()
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
                        .orElseThrow(() -> new TraditionalKnowledgeLabelNotFoundException(id));

                labelUri = labelRecord.getUri();
            }

            final var schemaId = record.getTraditionalKnowledgeLabelSchemaId();

            final var schemaRecord = traditionalKnowledgeLabelSchemaRepository.findById(schemaId)
                    .orElseThrow(() -> new TraditionalKnowledgeLabelSchemaNotFoundException(schemaId));

            traditionalKnowledgeLabels.add(traditionalKnowledgeLabelFactory.create(labelUri, schemaRecord.getUri()));
        }
        return traditionalKnowledgeLabels;
    }

    public void update(final List<TraditionalKnowledgeLabel> traditionalKnowledgeLabels, final String handle) {
        raidTraditionalKnowledgeLabelRepository.deleteAllByHandle(handle);
        create(traditionalKnowledgeLabels, handle);

    }
}
