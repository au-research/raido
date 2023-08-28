package au.org.raid.api.validator;

import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.Language;
import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.TitleTypeWithSchemaUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TitleValidatorTest {
    @Mock
    private TitleTypeValidator typeValidationService;
    @Mock
    private LanguageValidator languageValidator;
    @InjectMocks
    private TitleValidator validationService;

    @Test
    @DisplayName("Validation passes")
    void validationPasses() {
        final var type = new TitleTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_TITLE_TYPE_ID)
                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI);

        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var title = new Title()
                .type(type)
                .title(TestConstants.TITLE)
                .startDate(TestConstants.START_DATE)
                .endDate(TestConstants.END_DATE)
                .language(language);

        final var failures = validationService.validate(List.of(title));

        assertThat(failures.size(), is(0));
        verify(typeValidationService).validate(type, 0);
        verify(languageValidator).validate(language, "titles[0]");
    }

    @Test
    @DisplayName("Validation fails if primary title is missing")
    void missingPrimaryTitle() {
        final var type = new TitleTypeWithSchemaUri()
                .id(TestConstants.ALTERNATIVE_TITLE_TYPE)
                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI);

        final var title = new Title()
                .type(type)
                .title(TestConstants.TITLE)
                .startDate(TestConstants.START_DATE)
                .endDate(TestConstants.END_DATE);

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("titles.type")
                        .errorType("missingPrimaryTitle")
                        .message("at least one primaryTitle entry must be provided")));
    }

    @Test
    @DisplayName("Validation fails if more than one primary title")
    void multiplePrimaryTitles() {
        final var type = new TitleTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_TITLE_TYPE_ID)
                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI);

        final var title = new Title()
                .type(type)
                .title(TestConstants.TITLE)
                .startDate(TestConstants.START_DATE)
                .endDate(TestConstants.END_DATE);

        final var failures = validationService.validate(List.of(title, title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("titles.type")
                        .errorType("tooManyPrimaryTitle")
                        .message("too many primaryTitle entries provided")));
    }

    @Test
    @DisplayName("Validation fails if list of titles is null")
    void nullTitles() {
        final var failures = validationService.validate(null);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("titles")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if title is null")
    void nullTitle() {
        final var title = new Title()
                .type(new TitleTypeWithSchemaUri()
                        .id(TestConstants.PRIMARY_TITLE_TYPE_ID)
                        .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI))
                .startDate(TestConstants.START_DATE)
                .endDate(TestConstants.END_DATE);

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("titles[0].title")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }


    @Test
    @DisplayName("Validation fails if title is blank")
    void blankTitle() {
        final var title = new Title()
                .type(new TitleTypeWithSchemaUri()
                        .id(TestConstants.PRIMARY_TITLE_TYPE_ID)
                        .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI))
                .startDate(TestConstants.START_DATE)
                .endDate(TestConstants.END_DATE)
                .title("");

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("titles[0].title")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if start date is missing")
    void missingStartDate() {
        final var title = new Title()
                .type(new TitleTypeWithSchemaUri()
                        .id(TestConstants.PRIMARY_TITLE_TYPE_ID)
                        .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI))
                .title(TestConstants.TITLE)
                .endDate(TestConstants.END_DATE);

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("titles[0].startDate")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }
}