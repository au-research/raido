package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.Title;
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.TitleTypeWithSchemeUri;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;
import static raido.apisvc.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class StableTitleValidationServiceTest {
  @Mock
  private StableTitleTypeValidationService typeValidationService;
  @InjectMocks
  private StableTitleValidationService validationService;

  @Test
  @DisplayName("Validation passes")
  void validationPasses() {
    final var type = new TitleTypeWithSchemeUri()
      .id(PRIMARY_TITLE_TYPE_ID)
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    final var title = new Title()
      .type(type)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    final var failures = validationService.validate(List.of(title));

    assertThat(failures.size(), is(0));
    verify(typeValidationService).validate(type, 0);
  }

  @Test
  @DisplayName("Validation fails if primary title is missing")
  void missingPrimaryTitle() {
    final var type = new TitleTypeWithSchemeUri()
      .id(ALTERNATIVE_TITLE_TYPE)
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    final var title = new Title()
      .type(type)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

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
    final var type = new TitleTypeWithSchemeUri()
      .id(PRIMARY_TITLE_TYPE_ID)
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    final var title = new Title()
      .type(type)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

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
      .type(new TitleTypeWithSchemeUri()
        .id(PRIMARY_TITLE_TYPE_ID)
        .schemeUri(TITLE_TYPE_SCHEME_URI))
      .startDate(START_DATE)
      .endDate(END_DATE);

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
      .type(new TitleTypeWithSchemeUri()
        .id(PRIMARY_TITLE_TYPE_ID)
        .schemeUri(TITLE_TYPE_SCHEME_URI))
      .startDate(START_DATE)
      .endDate(END_DATE)
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
      .type(new TitleTypeWithSchemeUri()
        .id(PRIMARY_TITLE_TYPE_ID)
        .schemeUri(TITLE_TYPE_SCHEME_URI))
      .title(TITLE)
      .endDate(END_DATE);

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