package raido.apisvc.service.raid.validation;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.AlternateIdentifierBlock;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class AlternateIdentifierValidationService {

  public List<ValidationFailure> validateAlternateIdentifiers(List<AlternateIdentifierBlock> alternateIdentifiers) {
    final var failures = new ArrayList<ValidationFailure>();

    if (alternateIdentifiers == null) {
      return failures;
    }

    IntStream.range(0, alternateIdentifiers.size())
      .forEach(i -> {
        final var alternateIdentifier = alternateIdentifiers.get(i);

        if (alternateIdentifier.getAlternateIdentifier() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("alternateIdentifiers[%d].alternateIdentifier", i))
            .errorType("required")
            .message("This is a required field.")
          );
        }

        if (alternateIdentifier.getAlternateIdentifierType() == null) {
          failures.add(new ValidationFailure()
            .fieldId(String.format("alternateIdentifiers[%d].alternateIdentifierType", i))
            .errorType("required")
            .message("This is a required field.")
          );
        }

      });
    return failures;
  }
}
