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
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static raido.apisvc.util.TestConstants.PRIMARY_TITLE_TYPE_ID;
import static raido.apisvc.util.TestConstants.TITLE_TYPE_SCHEME_URI;

@ExtendWith(MockitoExtension.class)
class StableTitleTypeValidationServiceTest {
  private static final int INDEX = 3;
  private static final int TITLE_TYPE_SCHEME_ID = 1;

  private static final TitleTypeSchemeRecord TITLE_TYPE_SCHEME_RECORD = new TitleTypeSchemeRecord()
    .setId(TITLE_TYPE_SCHEME_ID)
    .setUri(TITLE_TYPE_SCHEME_URI);

  private static final TitleTypeRecord TITLE_TYPE_RECORD = new TitleTypeRecord()
    .setSchemeId(TITLE_TYPE_SCHEME_ID)
    .setUri(PRIMARY_TITLE_TYPE_ID);

  @Mock
  private TitleTypeSchemeRepository titleTypeSchemeRepository;
  @Mock
  private TitleTypeRepository titleTypeRepository;
  @InjectMocks
  private StableTitleTypeValidationService validationService;


  @Test
  @DisplayName("Validation passes with valid title type")
  void validTitleType() {
    final var titleType = new TitleType()
      .id(PRIMARY_TITLE_TYPE_ID)
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));
    when(titleTypeRepository.findByUriAndSchemeId(PRIMARY_TITLE_TYPE_ID, TITLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(TITLE_TYPE_RECORD));

    final var failures = validationService.validate(titleType, INDEX);

    assertThat(failures, empty());

    verify(titleTypeSchemeRepository).findByUri(TITLE_TYPE_SCHEME_URI);
    verify(titleTypeRepository).findByUriAndSchemeId(PRIMARY_TITLE_TYPE_ID, TITLE_TYPE_SCHEME_ID);
  }

  @Test
  @DisplayName("Validation fails when id is null")
  void nullId() {
    final var titleType = new TitleType()
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(titleType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[3].type.id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when id is empty string")
  void emptyId() {
    final var titleType = new TitleType()
      .id("")
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(titleType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[3].type.id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is null")
  void nullSchemeUri() {
    final var titleType = new TitleType()
      .id(PRIMARY_TITLE_TYPE_ID);

    final var failures = validationService.validate(titleType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[3].type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is empty")
  void emptySchemeUri() {
    final var titleType = new TitleType()
      .id(PRIMARY_TITLE_TYPE_ID)
      .schemeUri("");

    final var failures = validationService.validate(titleType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[3].type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is invalid")
  void invalidSchemeUri() {
    final var titleType = new TitleType()
      .id(PRIMARY_TITLE_TYPE_ID)
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(titleType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[3].type.schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
    ));
  }

  @Test
  @DisplayName("Validation fails when type is null")
  void nullType() {
    final var failures = validationService.validate(null, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[3].type")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(titleTypeSchemeRepository);
    verifyNoInteractions(titleTypeRepository);
  }

  @Test
  @DisplayName("Validation fails when id not found in scheme")
  void invalidTypeForScheme() {
    final var titleType = new TitleType()
      .id(PRIMARY_TITLE_TYPE_ID)
      .schemeUri(TITLE_TYPE_SCHEME_URI);

    when(titleTypeSchemeRepository.findByUri(TITLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(TITLE_TYPE_SCHEME_RECORD));

    when(titleTypeRepository.findByUriAndSchemeId(PRIMARY_TITLE_TYPE_ID, TITLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(titleType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("titles[3].type.id")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }
}