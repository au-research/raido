package au.org.raid.api.service.raid.validation;

import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessStatementValidationServiceTest {
    @Mock
    private LanguageValidationService languageValidationService;

    @InjectMocks
    private AccessStatementValidationService validationService;

    @Test
    @DisplayName("Valid access statement returns no errors")
    void validAccessStatement() {
        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemeUri(TestConstants.LANGUAGE_SCHEME_URI);

        when(languageValidationService.validate(language, "access.accessStatement"))
                .thenReturn(Collections.emptyList());

        final var accessStatement = new AccessStatement()
                .statement("Embargoed")
                .language(language);

        final var failures = validationService.validate(accessStatement);

        assertThat(failures, empty());
        verify(languageValidationService).validate(language, "access.accessStatement");
    }

    @Test
    @DisplayName("Fails validation with null accessStatement")
    void nullAccessStatement() {

        final var failures = validationService.validate(null);

        assertThat(failures, is(
                List.of(new ValidationFailure()
                        .fieldId("access.accessStatement")
                        .errorType("notSet")
                        .message("field must be set"))));
        verifyNoInteractions(languageValidationService);
    }

    @Test
    @DisplayName("Fails validation with null statement")
    void nullStatement() {
        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemeUri(TestConstants.LANGUAGE_SCHEME_URI);

        when(languageValidationService.validate(language, "access.accessStatement"))
                .thenReturn(Collections.emptyList());

        final var accessStatement = new AccessStatement()
                .language(language);

        final var failures = validationService.validate(accessStatement);

        assertThat(failures, is(
                List.of(new ValidationFailure()
                        .fieldId("access.accessStatement.statement")
                        .errorType("notSet")
                        .message("field must be set"))));
        verify(languageValidationService).validate(language, "access.accessStatement");
    }

    @Test
    @DisplayName("Language validation failures are returned")
    void invalidLanguage() {
        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemeUri(TestConstants.LANGUAGE_SCHEME_URI);

        final var failure = new ValidationFailure()
                .fieldId("access.accessStatement.language.id")
                .errorType("notSet")
                .message("field must be set");

        when(languageValidationService.validate(language, "access.accessStatement"))
                .thenReturn(List.of(failure));

        final var accessStatement = new AccessStatement()
                .statement("Embargoed")
                .language(language);

        final var failures = validationService.validate(accessStatement);

        assertThat(failures, is(List.of(failure)));
        verify(languageValidationService).validate(language, "access.accessStatement");
    }
}