package raido.apisvc.service.raid.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.AccessStatement;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static raido.apisvc.util.StringUtil.isBlank;

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
