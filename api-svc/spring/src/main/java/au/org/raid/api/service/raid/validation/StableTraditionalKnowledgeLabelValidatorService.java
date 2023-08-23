package au.org.raid.api.service.raid.validation;

import org.springframework.stereotype.Component;
import au.org.raid.idl.raidv2.model.TraditionalKnowledgeLabel;
import au.org.raid.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class StableTraditionalKnowledgeLabelValidatorService {
  private static final List<String> VALID_SCHEME_URIS = List.of(
    "https://localcontexts.org/labels/traditional-knowledge-labels/",
    "https://localcontexts.org/labels/biocultural-labels/"
  );

  public List<ValidationFailure> validateTraditionalKnowledgeLabels(
    final List<TraditionalKnowledgeLabel> traditionalKnowledgeLabels) {

    final var failures = new ArrayList<ValidationFailure>();

    if (traditionalKnowledgeLabels == null) {
      return failures;
    }

    IntStream.range(0, traditionalKnowledgeLabels.size()).forEach(
      i -> {
        final var label = traditionalKnowledgeLabels.get(i);

        if (isBlank(label.getSchemeUri())) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("traditionalKnowledgeLabels[%d].schemeUri", i))
            .errorType(NOT_SET_TYPE)
            .message(FIELD_MUST_BE_SET_MESSAGE));
        } else if (!VALID_SCHEME_URIS.contains(label.getSchemeUri())) {
          failures.add(new ValidationFailure()
            .errorType(INVALID_VALUE_TYPE)
            .fieldId(String.format("traditionalKnowledgeLabels[%d].schemeUri", i))
            .message("URI is not a valid traditional knowledge scheme"));
        }
      });

    return failures;
  }
}
