package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.TraditionalKnowledgeLabelBlock;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

class TraditionalKnowledgeLabelValidatorServiceTest {
  private final TraditionalKnowledgeLabelValidatorService validatorService =
    new TraditionalKnowledgeLabelValidatorService();

  @Test
  void noFailuresIfTraditionalKnowledgeLabelsIsNull() {
    final var failures = validatorService.validateTraditionalKnowledgeLabels(null);
    assertThat(failures, is(empty()));
  }

  @Test
  void noFailuresIfTraditionalKnowledgeLabelsIsEmpty() {
    final var failures = validatorService.validateTraditionalKnowledgeLabels(Collections.emptyList());
    assertThat(failures, is(empty()));
  }

  @Test
  void noFailuresIfTraditionalKnowledgeLabelsIsValid() {
    final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabelBlock()
      .traditionalKnowledgeLabelSchemeUri("https://localcontexts.org/labels/traditional-knowledge-labels/");

    final var failures = validatorService.validateTraditionalKnowledgeLabels(
      Collections.singletonList(traditionalKnowledgeLabel)
    );

    assertThat(failures, is(empty()));
  }

  @Test
  void addsFailureIfTraditionalKnowledgeLabelsIsNull() {
    final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabelBlock()
      .traditionalKnowledgeLabelSchemeUri(null);

    final var failures = validatorService.validateTraditionalKnowledgeLabels(
      Collections.singletonList(traditionalKnowledgeLabel)
    );

    final var failure = failures.get(0);
    assertThat(failures.size(), is(1));

    assertThat(failure.getFieldId(), is("traditionalKnowledgeLabels[0].traditionalKnowledgeLabelSchemeUri"));
    assertThat(failure.getErrorType(), is("required"));
    assertThat(failure.getMessage(), is("This is a required field."));
  }

  @Test
  void addsFailureIfTraditionalKnowledgeLabelsIsInvalid() {
    final var traditionalKnowledgeLabel = new TraditionalKnowledgeLabelBlock()
      .traditionalKnowledgeLabelSchemeUri("https://localcontexts.or/labels/traditional-knowledge-labels/");

    final var failures = validatorService.validateTraditionalKnowledgeLabels(
      Collections.singletonList(traditionalKnowledgeLabel)
    );

    final var failure = failures.get(0);
    assertThat(failures.size(), is(1));

    assertThat(failure.getFieldId(), is("traditionalKnowledgeLabels[0].traditionalKnowledgeLabelSchemeUri"));
    assertThat(failure.getErrorType(), is("invalid"));
    assertThat(failure.getMessage(), is("URI is not a valid traditional knowledge scheme."));
  }
}