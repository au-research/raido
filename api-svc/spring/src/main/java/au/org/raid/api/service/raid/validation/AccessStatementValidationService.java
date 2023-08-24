package au.org.raid.api.service.raid.validation;

import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class AccessStatementValidationService {
    private final LanguageValidationService languageValidationService;

    public List<ValidationFailure> validate(final AccessStatement accessStatement) {
        final var failures = new ArrayList<ValidationFailure>();

        if (accessStatement == null) {
            return List.of(new ValidationFailure()
                    .fieldId("access.accessStatement")
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        }

        if (isBlank(accessStatement.getStatement())) {
            failures.add(new ValidationFailure()
                    .fieldId("access.accessStatement.statement")
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        }

        failures.addAll(
                languageValidationService.validate(accessStatement.getLanguage(), "access.accessStatement"));

        return failures;
    }
}
