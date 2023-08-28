package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class TraditionalKnowledgeLabelValidator {
    private static final List<String> VALID_SCHEME_URIS = List.of(
            "https://localcontexts.org/labels/traditional-knowledge-labels/",
            "https://localcontexts.org/labels/biocultural-labels/"
    );

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
                                .fieldId(String.format("traditionalKnowledgeLabels[%d].schemaUri", i))
                                .errorType(NOT_SET_TYPE)
                                .message(NOT_SET_MESSAGE));
                    } else if (!VALID_SCHEME_URIS.contains(label.getSchemaUri())) {
                        failures.add(new ValidationFailure()
                                .errorType(INVALID_VALUE_TYPE)
                                .fieldId(String.format("traditionalKnowledgeLabels[%d].schemaUri", i))
                                .message("URI is not a valid traditional knowledge scheme"));
                    }
                });

        return failures;
    }
}
