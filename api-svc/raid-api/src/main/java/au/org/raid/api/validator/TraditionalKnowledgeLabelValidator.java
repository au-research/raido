package au.org.raid.api.validator;

import au.org.raid.api.repository.TraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelSchemaRepository;
import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class TraditionalKnowledgeLabelValidator {
    private final TraditionalKnowledgeLabelRepository labelRepository;
    private final TraditionalKnowledgeLabelSchemaRepository labelSchemaRepository;

    public List<ValidationFailure> validate(
            final List<TraditionalKnowledgeLabel> traditionalKnowledgeLabels) {

        final var failures = new ArrayList<ValidationFailure>();

        if (traditionalKnowledgeLabels == null) {
            return failures;
        }

        IntStream.range(0, traditionalKnowledgeLabels.size()).forEach(
                i -> {
                    final var label = traditionalKnowledgeLabels.get(i);

                    if (isBlank(label.getSchemaUri())) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("traditionalKnowledgeLabel[%d].schemaUri", i))
                                .errorType(NOT_SET_TYPE)
                                .message(NOT_SET_MESSAGE));
                    } else {
                        final var schema = labelSchemaRepository.findActiveByUri(label.getSchemaUri());

                        if (schema.isEmpty()) {
                            failures.add(new ValidationFailure()
                                    .errorType(INVALID_VALUE_TYPE)
                                    .fieldId(String.format("traditionalKnowledgeLabel[%d].schemaUri", i))
                                    .message(INVALID_SCHEMA));
                        } else if (!isBlank(label.getId())) {
                            final var schemaId = schema.get().getId();
                            if (labelRepository.findByUriAndSchemaId(label.getId(), schemaId).isEmpty()) {
                                failures.add(new ValidationFailure()
                                        .errorType(INVALID_VALUE_TYPE)
                                        .fieldId(String.format("traditionalKnowledgeLabel[%d].id", i))
                                        .message(INVALID_ID_FOR_SCHEMA));
                            }
                        }
                    }
                });

        return failures;
    }
}
