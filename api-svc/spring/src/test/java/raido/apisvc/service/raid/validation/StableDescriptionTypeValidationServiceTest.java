package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.DescriptionTypeRepository;
import raido.apisvc.repository.DescriptionTypeSchemeRepository;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeRecord;
import raido.db.jooq.api_svc.tables.records.DescriptionTypeSchemeRecord;
import raido.idl.raidv2.model.DescType;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static raido.apisvc.util.TestConstants.DESCRIPTION_TYPE_SCHEME_URI;
import static raido.apisvc.util.TestConstants.PRIMARY_DESCRIPTION_TYPE;

@ExtendWith(MockitoExtension.class)
class StableDescriptionTypeValidationServiceTest {
  private static final int INDEX = 3;
  private static final int DESCRIPTION_TYPE_SCHEME_ID = 1;

  private static final DescriptionTypeSchemeRecord DESCRIPTION_TYPE_SCHEME_RECORD = new DescriptionTypeSchemeRecord()
    .setId(DESCRIPTION_TYPE_SCHEME_ID)
    .setUri(DESCRIPTION_TYPE_SCHEME_URI);

  private static final DescriptionTypeRecord DESCRIPTION_TYPE_RECORD = new DescriptionTypeRecord()
    .setSchemeId(DESCRIPTION_TYPE_SCHEME_ID)
    .setUri(PRIMARY_DESCRIPTION_TYPE);

  @Mock
  private DescriptionTypeSchemeRepository descriptionTypeSchemeRepository;
  @Mock
  private DescriptionTypeRepository descriptionTypeRepository;
  @InjectMocks
  private StableDescriptionTypeValidationService validationService;


  @Test
  @DisplayName("Validation passes with valid description type")
  void validDescriptionType() {
    final var descriptionType = new DescType()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));
    when(descriptionTypeRepository.findByUriAndSchemeId(PRIMARY_DESCRIPTION_TYPE, DESCRIPTION_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_RECORD));

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, empty());

    verify(descriptionTypeSchemeRepository).findByUri(DESCRIPTION_TYPE_SCHEME_URI);
    verify(descriptionTypeRepository).findByUriAndSchemeId(PRIMARY_DESCRIPTION_TYPE, DESCRIPTION_TYPE_SCHEME_ID);
  }

  @Test
  @DisplayName("Validation fails when id is null")
  void nullId() {
    final var descriptionType = new DescType()
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[3].type.id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when id is empty string")
  void emptyId() {
    final var descriptionType = new DescType()
      .id("")
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[3].type.id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is null")
  void nullSchemeUri() {
    final var descriptionType = new DescType()
      .id(PRIMARY_DESCRIPTION_TYPE);

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[3].type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is empty")
  void emptySchemeUri() {
    final var descriptionType = new DescType()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri("");

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[3].type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is invalid")
  void invalidSchemeUri() {
    final var descriptionType = new DescType()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[3].type.schemeUri")
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
        .fieldId("descriptions[3].type")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(descriptionTypeSchemeRepository);
    verifyNoInteractions(descriptionTypeRepository);
  }

  @Test
  @DisplayName("Validation fails when id not found in scheme")
  void invalidTypeForScheme() {
    final var descriptionType = new DescType()
      .id(PRIMARY_DESCRIPTION_TYPE)
      .schemeUri(DESCRIPTION_TYPE_SCHEME_URI);

    when(descriptionTypeSchemeRepository.findByUri(DESCRIPTION_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(DESCRIPTION_TYPE_SCHEME_RECORD));

    when(descriptionTypeRepository.findByUriAndSchemeId(PRIMARY_DESCRIPTION_TYPE, DESCRIPTION_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(descriptionType, INDEX);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("descriptions[3].type.id")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }
}