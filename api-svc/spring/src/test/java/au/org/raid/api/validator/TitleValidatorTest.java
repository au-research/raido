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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
                .text(TestConstants.TITLE)
                .startDate(TestConstants.START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(TestConstants.END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .language(language);

        final var failures = validationService.validate(List.of(title));

        assertThat(failures.size(), is(0));
        verify(typeValidationService).validate(type, 0);
        verify(languageValidator).validate(language, "title[0]");
    }

    @Test
    @DisplayName("Validation fails when primary title is duplicated")
    void duplicatePrimaryTitle() {
        final var type = new TitleTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_TITLE_TYPE_ID)
                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI);

        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var title1 = new Title()
                .type(type)
                .text(TestConstants.TITLE)
                .startDate(LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .language(language);

        final var title2 = new Title()
                .type(type)
                .text(TestConstants.TITLE)
                .startDate(LocalDate.now().minusYears(3).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .language(language);

        final var title3 = new Title()
                .type(type)
                .text(TestConstants.TITLE)
                .startDate(LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .language(language);

        final var failures = validationService.validate(List.of(title1, title2, title3));

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("title[0]")
                        .errorType("duplicateValue")
                        .message("an object with the same values appears in the list")
        )));
        verify(typeValidationService).validate(type, 0);
        verify(languageValidator).validate(language, "title[0]");
    }

    @Test
    @DisplayName("Validation fails when primary titles overlap")
    void PrimaryTitlesOverlap() {
        final var type = new TitleTypeWithSchemaUri()
                .id(TestConstants.PRIMARY_TITLE_TYPE_ID)
                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI);

        final var language = new Language()
                .id(TestConstants.LANGUAGE_ID)
                .schemaUri(TestConstants.LANGUAGE_SCHEMA_URI);

        final var title1 = new Title()
                .type(type)
                .text(TestConstants.TITLE)
                .startDate(LocalDate.now().minusYears(3).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .language(language);

        final var title2 = new Title()
                .type(type)
                .text(TestConstants.TITLE)
                .startDate(LocalDate.now().minusYears(3).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(LocalDate.now().minusYears(2).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .language(language);

        final var title3 = new Title()
                .type(type)
                .text(TestConstants.TITLE)
                .startDate(LocalDate.now().minusYears(4).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .language(language);

        final var failures = validationService.validate(List.of(title1, title2, title3));

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("title[1].startDate")
                        .errorType("invalidValue")
                        .message("There can only be one primary title in any given period. The start date for this title overlaps with title[2]"),
                new ValidationFailure()
                        .fieldId("title[0].startDate")
                        .errorType("invalidValue")
                        .message("There can only be one primary title in any given period. The start date for this title overlaps with title[1]")
        )));
        verify(typeValidationService).validate(type, 0);
        verify(languageValidator).validate(language, "title[0]");
    }

    @Test
    @DisplayName("Validation fails if primary title is missing")
    void missingPrimaryTitle() {
        final var type = new TitleTypeWithSchemaUri()
                .id(TestConstants.ALTERNATIVE_TITLE_TYPE)
                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI);

        final var title = new Title()
                .type(type)
                .text(TestConstants.TITLE)
                .startDate(TestConstants.START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(TestConstants.END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE));

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, is(List.of(
                new ValidationFailure()
                        .fieldId("title.type")
                        .errorType("missingPrimaryTitle")
                        .message("at least one primaryTitle entry must be provided"))));
    }

    @Test
    @DisplayName("Validation fails if list of titles is null")
    void nullTitles() {
        final var failures = validationService.validate(null);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("title")
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
                .startDate(TestConstants.START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(TestConstants.END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE));

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("title[0].title")
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
                .startDate(TestConstants.START_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(TestConstants.END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .text("");

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("title[0].title")
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
                .text(TestConstants.TITLE)
                .endDate(TestConstants.END_DATE.format(DateTimeFormatter.ISO_LOCAL_DATE));

        final var failures = validationService.validate(List.of(title));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("title[0].startDate")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }
}