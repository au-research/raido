package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.TraditionalKnowledgeLabelBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class TraditionalKnowledgeLabelValidatorService {
  private static final List<String> VALID_SCHEME_URIS = List.of(
    "https://localcontexts.org/labels/traditional-knowledge-labels/",
    "https://localcontexts.org/labels/biocultural-labels/"
  );

  public List<ValidationFailure> validateTraditionalKnowledgeLabels(
    final List<TraditionalKnowledgeLabelBlock> traditionalKnowledgeLabels) {

    final var failures = new ArrayList<ValidationFailure>();

    if (traditionalKnowledgeLabels == null) {
      return failures;
    }

    IntStream.range(0, traditionalKnowledgeLabels.size()).forEach(
      i -> {
        final var label = traditionalKnowledgeLabels.get(i);

        if (label.getTraditionalKnowledgeLabelSchemeUri() == null) {
          failures.add(new ValidationFailure()
            .errorType("required")
            .fieldId(String.format("traditionalKnowledgeLabels[%d].traditionalKnowledgeLabelSchemeUri", i))
            .message("This is a required field."));
        } else if (!VALID_SCHEME_URIS.contains(label.getTraditionalKnowledgeLabelSchemeUri())) {
          failures.add(new ValidationFailure()
            .errorType("invalid")
            .fieldId(String.format("traditionalKnowledgeLabels[%d].traditionalKnowledgeLabelSchemeUri", i))
            .message("URI is not a valid traditional knowledge scheme."));
        }
      });

    return failures;
  }
}
