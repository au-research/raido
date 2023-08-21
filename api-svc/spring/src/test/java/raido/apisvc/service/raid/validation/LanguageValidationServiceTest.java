package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.LanguageRepository;
import raido.apisvc.repository.LanguageSchemeRepository;
import raido.db.jooq.api_svc.tables.records.LanguageRecord;
import raido.db.jooq.api_svc.tables.records.LanguageSchemeRecord;
import raido.idl.raidv2.model.Language;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LanguageValidationServiceTest {
    private static final String LANGUAGE_ID = "eng";
    private static final int LANGUAGE_SCHEME_ID = 1;
    private static final String LANGUAGE_SCHEME_URI = "https://www.iso.org/standard/39534.html";

    private static final LanguageRecord LANGUAGE_RECORD = new LanguageRecord()
            .setId(LANGUAGE_ID)
            .setSchemeId(LANGUAGE_SCHEME_ID);

    private static final LanguageSchemeRecord LANGUAGE_SCHEME_RECORD = new LanguageSchemeRecord()
            .setId(LANGUAGE_SCHEME_ID)
            .setUri(LANGUAGE_SCHEME_URI);

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private LanguageSchemeRepository languageSchemeRepository;

    @InjectMocks
    private LanguageValidationService languageValidationService;

    @Test
    @DisplayName("Returns empty list when Language is null")
    void nullLanguage() {
        assertThat(languageValidationService.validate(null, "parent"), is(Collections.emptyList()));
    }

    @Test
    @DisplayName("Returns failure if id is null")
    void nullId() {
        final var language = new Language().id(null).schemeUri(LANGUAGE_SCHEME_URI);

        when(languageSchemeRepository.findByUri(LANGUAGE_SCHEME_URI)).thenReturn(Optional.of(LANGUAGE_SCHEME_RECORD));

        final var failures = languageValidationService.validate(language, "parent");

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("parent.language.id")
                        .errorType("notSet")
                        .message("field must be set")
        )));
    }

    @Test
    @DisplayName("Returns failure if id is empty string")
    void emptyId() {
        final var language = new Language().id("").schemeUri(LANGUAGE_SCHEME_URI);

        when(languageSchemeRepository.findByUri(LANGUAGE_SCHEME_URI)).thenReturn(Optional.of(LANGUAGE_SCHEME_RECORD));

        final var failures = languageValidationService.validate(language, "parent");

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("parent.language.id")
                        .errorType("notSet")
                        .message("field must be set")
        )));
    }

    @Test
    @DisplayName("Returns failure if schemeUri is empty string")
    void emptySchemeUri() {
        final var language = new Language().id("eng").schemeUri("");

        final var failures = languageValidationService.validate(language, "parent");

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("parent.language.schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        )));
    }

    @Test
    @DisplayName("Returns failure if schemeUri is null")
    void nullSchemeUri() {
        final var language = new Language().id("eng");

        final var failures = languageValidationService.validate(language, "parent");

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("parent.language.schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        )));
    }

    @Test
    @DisplayName("Returns failure if schemeUri is not supported")
    void invalidSchemeUri() {
        final var language = new Language()
                .id("eng")
                .schemeUri(LANGUAGE_SCHEME_URI);

        when(languageSchemeRepository.findByUri(LANGUAGE_SCHEME_URI)).thenReturn(Optional.empty());

        final var failures = languageValidationService.validate(language, "parent");

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("parent.language.schemeUri")
                        .errorType("invalidValue")
                        .message("scheme is unknown/unsupported")
        )));
    }

    @Test
    @DisplayName("Returns failure if id is not found with scheme")
    void invalidId() {
        final var language = new Language()
                .id(LANGUAGE_ID)
                .schemeUri(LANGUAGE_SCHEME_URI);

        final var languageScheme = new LanguageSchemeRecord().setId(LANGUAGE_SCHEME_ID);

        when(languageSchemeRepository.findByUri(LANGUAGE_SCHEME_URI)).thenReturn(Optional.of(languageScheme));
        when(languageRepository.findByIdAndSchemeId(LANGUAGE_ID, LANGUAGE_SCHEME_ID)).thenReturn(Optional.empty());

        final var failures = languageValidationService.validate(language, "parent");

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("parent.language.id")
                        .errorType("invalidValue")
                        .message("id does not exist within the given scheme")
        )));
    }

    @Test
    @DisplayName("Valid language returns no failures")
    void validLanguage() {
        final var language = new Language()
                .id(LANGUAGE_ID)
                .schemeUri(LANGUAGE_SCHEME_URI);

        when(languageSchemeRepository.findByUri(LANGUAGE_SCHEME_URI)).thenReturn(Optional.of(LANGUAGE_SCHEME_RECORD));
        when(languageRepository.findByIdAndSchemeId(LANGUAGE_ID, LANGUAGE_SCHEME_ID))
                .thenReturn(Optional.of(LANGUAGE_RECORD));

        final var failures = languageValidationService.validate(language, "parent");

        assertThat(failures, empty());
    }
}