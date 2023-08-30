package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class TraditionalKnowledgeLabelValidatorTest {
    private final TraditionalKnowledgeLabelValidator validatorService =
            new TraditionalKnowledgeLabelValidator();

    @Test
    @DisplayName("Validation passes if traditionalKnowledgeLabels is null")
    void noFailuresIfTraditionalKnowledgeLabelsIsNull() {
        final var failures = validatorService.validateTraditionalKnowledgeLabels(null);
        assertThat(failures, empty());
    }

    @Test
    @DisplayName("Validation passes if traditionalKnowledgeLabels is empty")
    void noFailuresIfTraditionalKnowledgeLabelsIsEmpty() {
        final var failures = validatorService.validateTraditionalKnowledgeLabels(Collections.emptyList());
        assertThat(failures, empty());
    }

    @Test
    @DisplayName("Validation passes if traditionalKnowledgeLabels is valid")
    void noFailuresIfTraditionalKnowledgeLabelsIsValid() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemeUri("https://localcontexts.org/labels/traditional-knowledge-labels/");

        final var failures = validatorService.validateTraditionalKnowledgeLabels(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, is(empty()));
    }

    @Test
    @DisplayName("Validation fails if schemeUri is null")
    void addsFailureIfTraditionalKnowledgeLabelsIsNull() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemeUri(null);

        final var failures = validatorService.validateTraditionalKnowledgeLabels(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("traditionalKnowledgeLabels[0].schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if schemeUri is empty string")
    void addsFailureIfTraditionalKnowledgeLabelsIsEmptyString() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemeUri("");

        final var failures = validatorService.validateTraditionalKnowledgeLabels(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("traditionalKnowledgeLabels[0].schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if schemeUri is invalid")
    void addsFailureIfTraditionalKnowledgeLabelsIsInvalid() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemeUri("https://localcontexts.or/labels/traditional-knowledge-labels/");

        final var failures = validatorService.validateTraditionalKnowledgeLabels(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("traditionalKnowledgeLabels[0].schemeUri")
                        .errorType("invalidValue")
                        .message("URI is not a valid traditional knowledge scheme")
        ));
    }
}