package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.LanguageRepository;
import au.org.raid.api.repository.LanguageSchemeRepository;
import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.*;
import static au.org.raid.api.util.StringUtil.isBlank;

@Component
@RequiredArgsConstructor
public class LanguageValidationService {
    private final LanguageSchemeRepository languageSchemeRepository;
    private final LanguageRepository languageRepository;

    public List<ValidationFailure> validate(final Language language, final String parent) {
        final var failures = new ArrayList<ValidationFailure>();

        if (language == null) {
            return failures;
        }

        if (isBlank(language.getId())) {
            failures.add(new ValidationFailure()
                    .fieldId("%s.language.id".formatted(parent))
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        }
        if (isBlank(language.getSchemeUri())) {
            failures.add(new ValidationFailure()
                    .fieldId("%s.language.schemeUri".formatted(parent))
                    .errorType(NOT_SET_TYPE)
                    .message(FIELD_MUST_BE_SET_MESSAGE)
            );
        } else {
            final var languageScheme = languageSchemeRepository.findByUri(language.getSchemeUri());

            if (languageScheme.isEmpty()) {
                failures.add(new ValidationFailure()
                        .fieldId("%s.language.schemeUri".formatted(parent))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(INVALID_SCHEME)
                );
            } else if (!isBlank(language.getId()) &&
                    languageRepository.findByIdAndSchemeId(language.getId(), languageScheme.get().getId()).isEmpty()) {
                failures.add(new ValidationFailure()
                        .fieldId("%s.language.id".formatted(parent))
                        .errorType(INVALID_VALUE_TYPE)
                        .message(INVALID_ID_FOR_SCHEME)
                );


            }
        }

        return failures;
    }
}
