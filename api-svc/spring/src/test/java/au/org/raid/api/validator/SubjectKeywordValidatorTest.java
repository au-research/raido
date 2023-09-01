package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.SubjectKeyword;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubjectKeywordValidatorTest {
    @Mock
    private LanguageValidator languageValidator;

    @InjectMocks
    private SubjectKeywordValidator validator;

    @Test
    @DisplayName("Valid keyword passes validation")
    void validKeyword() {
        final var parentField = "subjects[1].keywords[1]";

        final var language = new Language();

        final var keyword = new SubjectKeyword()
                .keyword("blah")
                .language(language);

        when(languageValidator.validate(language, parentField)).thenReturn(Collections.emptyList());

        final var failures = validator.validate(keyword, 1, 1);

        assertThat(failures, empty());

        verify(languageValidator).validate(language, parentField);
    }

    @Test
    @DisplayName("null keyword fails validation")
    void nullKeyword() {
        final var parentField = "subjects[1].keywords[1]";

        final var language = new Language();

        final var keyword = new SubjectKeyword()
                .language(language);

        when(languageValidator.validate(language, parentField)).thenReturn(Collections.emptyList());

        final var failures = validator.validate(keyword, 1, 1);

        assertThat(failures, is(List.of(new ValidationFailure()
                .fieldId("subjects[1].keywords[1].keyword")
                .errorType("notSet")
                .message("field must be set")
        )));

        verify(languageValidator).validate(language, parentField);
    }

    @Test
    @DisplayName("null keyword fails validation")
    void emptyKeyword() {
        final var parentField = "subjects[1].keywords[1]";

        final var language = new Language();

        final var keyword = new SubjectKeyword()
                .keyword("")
                .language(language);

        when(languageValidator.validate(language, parentField)).thenReturn(Collections.emptyList());

        final var failures = validator.validate(keyword, 1, 1);

        assertThat(failures, is(List.of(new ValidationFailure()
                .fieldId("subjects[1].keywords[1].keyword")
                .errorType("notSet")
                .message("field must be set")
        )));

        verify(languageValidator).validate(language, parentField);
    }

    @Test
    @DisplayName("Validation failures on language field are returned")
    void languageFailuresAreReturned() {
        final var parentField = "subjects[1].keywords[1]";

        final var language = new Language();

        final var keyword = new SubjectKeyword()
                .keyword("valid")
                .language(language);

        var failure = new ValidationFailure();

        when(languageValidator.validate(language, parentField)).thenReturn(List.of(failure));

        final var failures = validator.validate(keyword, 1, 1);

        assertThat(failures, is(List.of(failure)));

        verify(languageValidator).validate(language, parentField);
    }
}
