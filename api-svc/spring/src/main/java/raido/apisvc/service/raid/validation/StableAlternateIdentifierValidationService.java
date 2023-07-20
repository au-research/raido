package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.AlternateIdentifier;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;

@Component
public class StableAlternateIdentifierValidationService {

  public List<ValidationFailure> validateAlternateIdentifiers(List<AlternateIdentifier> alternateIdentifiers) {
    final var failures = new ArrayList<ValidationFailure>();

    if (alternateIdentifiers == null) {
      return failures;
    }

    IntStream.range(0, alternateIdentifiers.size())
      .forEach(i -> {
        final var alternateIdentifier = alternateIdentifiers.get(i);

        if (alternateIdentifier.getId() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("alternateIdentifiers[%d].id", i))
            .errorType(NOT_SET_TYPE)
            .message(FIELD_MUST_BE_SET_MESSAGE)
          );
        }

        if (alternateIdentifier.getType() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("alternateIdentifiers[%d].type", i))
            .errorType(NOT_SET_TYPE)
            .message(FIELD_MUST_BE_SET_MESSAGE)
          );
        }

      });
    return failures;
  }
}
