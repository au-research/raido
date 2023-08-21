package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.Description;
import raido.idl.raidv2.model.DescriptionTypeWithSchemeUri;
import raido.idl.raidv2.model.Language;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static raido.apisvc.util.TestConstants.*;


@ExtendWith(MockitoExtension.class)
class StableDescriptionValidationServiceTest {
  private static final String DESCRIPTION_VALUE = "Test description";

  @Mock
  private StableDescriptionTypeValidationService typeValidationService;
  @Mock
  private LanguageValidationService languageValidationService;
  @InjectMocks
  private StableDescriptionValidationService validationService;

  @Test
  @DisplayName("Validation passes with valid description")
  void validDescription() {
    final var type = new DescriptionTypeWithSchemeUri()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var language = new Language()
            .id(LANGUAGE_ID)
            .schemeUri(LANGUAGE_SCHEME_URI);

    final var description = new Description()
      .description(DESCRIPTION_VALUE)
      .type(type)
      .language(language);

    final var failures = validationService.validate(List.of(description));

    assertThat(failures, empty());

    verify(typeValidationService).validate(type, 0);
    verify(languageValidationService).validate(language, "descriptions[0]");

  }

  @Test
  @DisplayName("Validation fails with null description")
  void nullDescription() {
    final var type = new DescriptionTypeWithSchemeUri()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var language = new Language()
            .id(LANGUAGE_ID)
            .schemeUri(LANGUAGE_SCHEME_URI);

    final var description = new Description()
            .type(type)
            .language(language);

    final var failures = validationService.validate(List.of(description));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[0].description")
        .errorType("notSet")
        .message("field must be set")
    ));
    verify(typeValidationService).validate(type, 0);
    verify(languageValidationService).validate(language, "descriptions[0]");
  }

  @Test
  @DisplayName("Validation fails with empty description")
  void emptyDescription() {
    final var description = new Description()
      .description("")
      .type(new DescriptionTypeWithSchemeUri()
        .id(PRIMARY_DESCRIPTION_TYPE)
        .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
      );

    final var failures = validationService.validate(List.of(description));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[0].description")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Type validation failures are returned")
  void typeErrorAreReturned() {
    final var type = new DescriptionTypeWithSchemeUri()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    final var description = new Description()
      .description(DESCRIPTION_VALUE)
      .type(type);

    final var typeError = new ValidationFailure();

    when(typeValidationService.validate(type, 0)).thenReturn(List.of(typeError));

    final var failures = validationService.validate(List.of(description));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(typeError));
  }
}