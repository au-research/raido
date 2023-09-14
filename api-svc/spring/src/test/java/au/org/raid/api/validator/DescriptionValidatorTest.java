package au.org.raid.api.validator;

import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.Description;
import au.org.raid.idl.raidv2.model.DescriptionTypeWithSchemaUri;
import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DescriptionValidatorTest {
    private static final String DESCRIPTION_VALUE = "Test description";

    @Mock
    private DescriptionTypeValidator typeValidationService;
    @Mock
    private LanguageValidator languageValidator;
    @InjectMocks
    private DescriptionValidator validationService;

    @Test
    @DisplayName("Validation passes with valid description")
    void validDescription() {
        final var type = new DescriptionTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                .schemaUri(TestConstants.DESCRIPTION_TYPE_SCHEMA_URI);

        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var description = new Description()
                .text(DESCRIPTION_VALUE)
                .type(type)
                .language(language);

        final var failures = validationService.validate(List.of(description));

        assertThat(failures, empty());

        verify(typeValidationService).validate(type, 0);
        verify(languageValidator).validate(language, "description[0]");

    }

    @Test
    @DisplayName("Validation fails with null description")
    void nullDescription() {
        final var type = new DescriptionTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                .schemaUri(TestConstants.DESCRIPTION_TYPE_SCHEMA_URI);

        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var description = new Description()
                .type(type)
                .language(language);

        final var failures = validationService.validate(List.of(description));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("description[0].text")
                        .errorType("notSet")
                        .message("field must be set")
        ));
        verify(typeValidationService).validate(type, 0);
        verify(languageValidator).validate(language, "description[0]");
    }

    @Test
    @DisplayName("Validation fails with empty description")
    void emptyDescription() {
        final var description = new Description()
                .text("")
                .type(new DescriptionTypeWithSchemaUri()
                        .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                        .schemaUri(TestConstants.DESCRIPTION_TYPE_SCHEMA_URI)
                );

        final var failures = validationService.validate(List.of(description));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("description[0].text")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Type validation failures are returned")
    void typeErrorAreReturned() {
        final var type = new DescriptionTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                .schemaUri(TestConstants.DESCRIPTION_TYPE_SCHEMA_URI);

        final var description = new Description()
                .text(DESCRIPTION_VALUE)
                .type(type);

        final var typeError = new ValidationFailure();

        when(typeValidationService.validate(type, 0)).thenReturn(List.of(typeError));

        final var failures = validationService.validate(List.of(description));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(typeError));
    }
}