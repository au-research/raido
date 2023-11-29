package au.org.raid.api.validator;

import au.org.raid.api.repository.TraditionalKnowledgeLabelRepository;
import au.org.raid.api.repository.TraditionalKnowledgeLabelSchemaRepository;
import au.org.raid.db.jooq.tables.records.TraditionalKnowledgeLabelRecord;
import au.org.raid.db.jooq.tables.records.TraditionalKnowledgeLabelSchemaRecord;
import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraditionalKnowledgeLabelValidatorTest {
    private static final int SCHEMA_ID = 1;
    private static final String SCHEMA_URI = "https://localcontexts.org/labels/traditional-knowledge-labels/";
    private static final String LABEL_ID = "https://localcontexts.org/label/tk-attribution/";

    private static final TraditionalKnowledgeLabelRecord TRADITIONAL_KNOWLEDGE_LABEL_RECORD =
            new TraditionalKnowledgeLabelRecord()
                    .setSchemaId(SCHEMA_ID)
                    .setUri(LABEL_ID);

    private static final TraditionalKnowledgeLabelSchemaRecord TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_RECORD =
            new TraditionalKnowledgeLabelSchemaRecord()
                    .setId(SCHEMA_ID)
                    .setUri(SCHEMA_URI);
    @Mock
    private TraditionalKnowledgeLabelRepository labelRepository;
    @Mock
    private TraditionalKnowledgeLabelSchemaRepository labelSchemaRepository;
    @InjectMocks
    private TraditionalKnowledgeLabelValidator validatorService;

    @Test
    @DisplayName("Validation passes if traditionalKnowledgeLabels is null")
    void noFailuresIfTraditionalKnowledgeLabelsIsNull() {
        final var failures = validatorService.validate(null);
        assertThat(failures, empty());
    }

    @Test
    @DisplayName("Validation passes if traditionalKnowledgeLabels is empty")
    void noFailuresIfTraditionalKnowledgeLabelsIsEmpty() {
        final var failures = validatorService.validate(Collections.emptyList());
        assertThat(failures, empty());
    }

    @Test
    @DisplayName("Validation passes if traditionalKnowledgeLabel is valid without label id")
    void validTraditionalKnowledgeLabelWithoutLabelId() {
        final var schemaUri = "https://localcontexts.org/labels/traditional-knowledge-labels/";

        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemaUri(SCHEMA_URI);

        when(labelSchemaRepository.findByUri(schemaUri))
                .thenReturn(Optional.of(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_RECORD));

        final var failures = validatorService.validate(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, is(empty()));
    }

    @Test
    @DisplayName("Validation passes if traditionalKnowledgeLabel is valid")
    void noFailuresIfTraditionalKnowledgeLabelsIsValid() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .id(LABEL_ID)
                .schemaUri(SCHEMA_URI);

        when(labelSchemaRepository.findByUri(SCHEMA_URI))
                .thenReturn(Optional.of(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_RECORD));

        when(labelRepository.findByUriAndSchemaId(LABEL_ID, SCHEMA_ID))
                .thenReturn(Optional.of(TRADITIONAL_KNOWLEDGE_LABEL_RECORD));

        final var failures = validatorService.validate(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, is(empty()));
    }

    @Test
    @DisplayName("Validation fails if Label id is invalid")
    void invalidLabelId() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .id(LABEL_ID)
                .schemaUri(SCHEMA_URI);

        when(labelSchemaRepository.findByUri(SCHEMA_URI))
                .thenReturn(Optional.of(TRADITIONAL_KNOWLEDGE_LABEL_SCHEMA_RECORD));

        when(labelRepository.findByUriAndSchemaId(LABEL_ID, SCHEMA_ID))
                .thenReturn(Optional.empty());

        final var failures = validatorService.validate(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("traditionalKnowledgeLabel[0].id")
                        .errorType("invalidValue")
                        .message("id does not exist within the given schema")
        )));
    }


    @Test
    @DisplayName("Validation fails if schemaUri is null")
    void addsFailureIfTraditionalKnowledgeLabelsIsNull() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemaUri(null);

        final var failures = validatorService.validate(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("traditionalKnowledgeLabel[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if schemaUri is empty string")
    void addsFailureIfTraditionalKnowledgeLabelsIsEmptyString() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemaUri("");

        final var failures = validatorService.validate(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("traditionalKnowledgeLabel[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if schemaUri is invalid")
    void addsFailureIfTraditionalKnowledgeLabelsIsInvalid() {
        final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabel()
                .schemaUri("https://localcontexts.or/labels/traditional-knowledge-labels/");

        final var failures = validatorService.validate(
                List.of(traditionalKnowledgeLabel)
        );

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("traditionalKnowledgeLabel[0].schemaUri")
                        .errorType("invalidValue")
                        .message("schema is unknown/unsupported")
        ));
    }
}