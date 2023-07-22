package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.TitleTypeRepository;
import raido.apisvc.repository.TitleTypeSchemeRepository;
import raido.db.jooq.api_svc.tables.records.TitleTypeRecord;
import raido.db.jooq.api_svc.tables.records.TitleTypeSchemeRecord;
import raido.idl.raidv2.model.Title;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static raido.apisvc.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class StableTitleValidationServiceTest {
  private static final String UNKNOWN_SCHEME_URI =
    "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";

  private static final int TITLE_TYPE_SCHEME_ID = 1;

  private static final TitleTypeSchemeRecord TITLE_TYPE_SCHEME_RECORD = new TitleTypeSchemeRecord()
    .setUri(TITLE_TYPE_SCHEME_URI)
    .setId(TITLE_TYPE_SCHEME_ID);

  private static final TitleTypeRecord TITLE_TYPE_RECORD = new TitleTypeRecord()
    .setUri(PRIMARY_TITLE_TYPE)
    .setSchemeId(TITLE_TYPE_SCHEME_ID);

  @Mock
  private TitleTypeSchemeRepository titleTypeSchemeRepository;
  @Mock
  private TitleTypeRepository titleTypeRepository;
  @InjectMocks
  private StableTitleValidationService validationService;

  @Test
  @DisplayName("Validation passes")
  void validationPasses() {
    final var title = new Title()
      .type(PRIMARY_TITLE_TYPE)
      .schemeUri(TITLE_TYPE_SCHEME_URI)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    when(titleTypeRepository.findByUriAndSchemeId(PRIMARY_TITLE_TYPE, TITLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(TITLE_TYPE_RECORD));

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures.size(), is(0));
  }

  @Test
  @DisplayName("Validation fails if type is missing")
  void missingType() {
    final var title = new Title()
      .schemeUri(TITLE_TYPE_SCHEME_URI)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures.size(), is(2));

    assertThat(failures, hasItems(
      new ValidationFailure()
        .errorType("notSet")
        .fieldId("titles[0].type")
        .message("field must be set"),
      new ValidationFailure()
        .errorType("missingPrimaryTitle")
        .fieldId("titles.type")
        .message("at least one primaryTitle entry must be provided")
    ));
  }

  @Test
  @DisplayName("Validation fails if schemeUri is missing")
  void missingSchemeUri() {
    final var title = new Title()
      .type(PRIMARY_TITLE_TYPE)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures.size(), is(1));

    final var failure = failures.get(0);
    assertThat(failure.getErrorType(), is("notSet"));
    assertThat(failure.getFieldId(), is("titles[0].schemeUri"));
    assertThat(failure.getMessage(), is("field must be set"));
  }

  @Test
  @DisplayName("Validation fails if schemeUri is blank")
  void blankSchemeUri() {
    final var title = new Title()
      .schemeUri("")
      .type(PRIMARY_TITLE_TYPE)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures.size(), is(1));

    final var failure = failures.get(0);
    assertThat(failure.getErrorType(), is("notSet"));
    assertThat(failure.getFieldId(), is("titles[0].schemeUri"));
    assertThat(failure.getMessage(), is("field must be set"));
  }

  @Test
  @DisplayName("Validation fails if title is null")
  void nullTitle() {
    final var title = new Title()
      .schemeUri(TITLE_TYPE_SCHEME_URI)
      .type(PRIMARY_TITLE_TYPE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    when(titleTypeRepository.findByUriAndSchemeId(PRIMARY_TITLE_TYPE, TITLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(TITLE_TYPE_RECORD));

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures.size(), is(1));

    final var failure = failures.get(0);
    assertThat(failure.getErrorType(), is("notSet"));
    assertThat(failure.getFieldId(), is("titles[0].title"));
    assertThat(failure.getMessage(), is("field must be set"));
  }

  @Test
  @DisplayName("Validation fails if title is blank")
  void blankTitle() {
    final var title = new Title()
      .schemeUri(TITLE_TYPE_SCHEME_URI)
      .type(PRIMARY_TITLE_TYPE)
      .startDate(START_DATE)
      .endDate(END_DATE)
      .title("");

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    when(titleTypeRepository.findByUriAndSchemeId(PRIMARY_TITLE_TYPE, TITLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(TITLE_TYPE_RECORD));

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures.size(), is(1));

    final var failure = failures.get(0);
    assertThat(failure.getErrorType(), is("notSet"));
    assertThat(failure.getFieldId(), is("titles[0].title"));
    assertThat(failure.getMessage(), is("field must be set"));
  }


  @Test
  @DisplayName("Validation fails if schemeUri does not exist")
  void nonExistentSchemeUri() {
    final var title = new Title()
      .schemeUri(TITLE_TYPE_SCHEME_URI)
      .type(PRIMARY_TITLE_TYPE)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[0].schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
    ));
  }

  @Test
  @DisplayName("Validation fails if type does not exist in scheme")
  void invalidTitleType() {
    final var title = new Title()
      .schemeUri(TITLE_TYPE_SCHEME_URI)
      .type(PRIMARY_TITLE_TYPE)
      .title(TITLE)
      .startDate(START_DATE)
      .endDate(END_DATE);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    when(titleTypeRepository.findByUriAndSchemeId(PRIMARY_TITLE_TYPE, TITLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[0].type")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }

  @Test
  @DisplayName("Validation fails if start date is missing")
  void missingStartDate() {
    final var title = new Title()
      .type(PRIMARY_TITLE_TYPE)
      .schemeUri(TITLE_TYPE_SCHEME_URI)
      .title(TITLE)
      .endDate(END_DATE);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    when(titleTypeRepository.findByUriAndSchemeId(PRIMARY_TITLE_TYPE, TITLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(TITLE_TYPE_RECORD));

    final var failures = validationService.validateTitles(List.of(title));

    assertThat(failures.size(), is(1));
    final var failure = failures.get(0);
    assertThat(failure.getErrorType(), is("notSet"));
    assertThat(failure.getFieldId(), is("titles[0].startDate"));
    assertThat(failure.getMessage(), is("field must be set"));  }

}