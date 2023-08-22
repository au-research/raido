package raido.apisvc.service.raid.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import raido.apisvc.repository.LanguageRepository;
import raido.apisvc.repository.LanguageSchemeRepository;
import raido.idl.raidv2.model.Language;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static raido.apisvc.endpoint.message.ValidationMessage.*;
import static raido.apisvc.util.StringUtil.isBlank;

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
