package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;

@Component
public class AlternateIdentifierValidator {

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
                                .message(NOT_SET_MESSAGE)
                        );
                    }

                    if (alternateIdentifier.getType() == null) {
                        failures.add(new ValidationFailure()
                                .fieldId(String.format("alternateIdentifiers[%d].type", i))
                                .errorType(NOT_SET_TYPE)
                                .message(NOT_SET_MESSAGE)
                        );
                    }

                });
        return failures;
    }
}
