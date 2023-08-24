package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.ror.RorService;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
public class StableRorValidationService {
    // see https://ror.readme.io/docs/ror-identifier-pattern
    private static final String ROR_REGEX = "^https:\\/\\/ror\\.org\\/0[a-hj-km-np-tv-z|0-9]{6}[0-9]{2}$";
    private final RorService rorService;

    public StableRorValidationService(final RorService rorService) {
        this.rorService = rorService;
    }

    public List<ValidationFailure> validate(final String ror, final int organisationIndex) {
        final var failures = new ArrayList<ValidationFailure>();

        if (isBlank(ror)) {
            failures.add(new ValidationFailure()
                    .fieldId("organisations[%d].id".formatted(organisationIndex))
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE));
        } else if (!Pattern.matches(ROR_REGEX, ror)) {
            failures.add(new ValidationFailure()
                    .fieldId("organisations[%d].id".formatted(organisationIndex))
                    .errorType(INVALID_VALUE_TYPE)
                    .message(INVALID_VALUE_MESSAGE)
            );
        } else if (!rorService.validateRorExists(ror).isEmpty()) {
            failures.add(new ValidationFailure()
                    .fieldId("organisations[%d].id".formatted(organisationIndex))
                    .errorType(INVALID_VALUE_TYPE)
                    .message("The organisation ROR does not exist")
            );
        }

        return failures;
    }
}
