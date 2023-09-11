package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.SubjectKeyword;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class SubjectKeywordValidator {
    private final LanguageValidator languageValidator;

    public List<ValidationFailure> validate(final SubjectKeyword keyword, final int subjectIndex, final int keywordIndex) {
        final var failures = new ArrayList<ValidationFailure>();

        if (isBlank(keyword.getText())) {
            failures.add(new ValidationFailure()
                    .fieldId("subjects[%d].keywords[%d].keyword".formatted(subjectIndex, keywordIndex))
                    .errorType(NOT_SET_TYPE)
                    .message(NOT_SET_MESSAGE));
        }

        failures.addAll(languageValidator.validate(keyword.getLanguage(), "subjects[%d].keywords[%d]".formatted(subjectIndex, keywordIndex)));

        return failures;
    }
}
