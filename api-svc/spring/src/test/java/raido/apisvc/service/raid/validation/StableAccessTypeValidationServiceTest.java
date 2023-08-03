package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.AccessTypeRepository;
import raido.apisvc.repository.AccessTypeSchemeRepository;
import raido.db.jooq.api_svc.tables.records.AccessTypeRecord;
import raido.db.jooq.api_svc.tables.records.AccessTypeSchemeRecord;
import raido.idl.raidv2.model.AccessType;
import raido.idl.raidv2.model.AccessTypeWithSchemeUri;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static raido.apisvc.util.TestConstants.ACCESS_TYPE_SCHEME_URI;
import static raido.apisvc.util.TestConstants.OPEN_ACCESS_TYPE_ID;

@ExtendWith(MockitoExtension.class)
class StableAccessTypeValidationServiceTest {
  private static final int ACCESS_TYPE_SCHEME_ID = 1;

  private static final AccessTypeSchemeRecord ACCESS_TYPE_SCHEME_RECORD = new AccessTypeSchemeRecord()
    .setId(ACCESS_TYPE_SCHEME_ID)
    .setUri(ACCESS_TYPE_SCHEME_URI);

  private static final AccessTypeRecord ACCESS_TYPE_RECORD = new AccessTypeRecord()
    .setSchemeId(ACCESS_TYPE_SCHEME_ID)
    .setUri(OPEN_ACCESS_TYPE_ID);

  @Mock
  private AccessTypeSchemeRepository accessTypeSchemeRepository;
  @Mock
  private AccessTypeRepository accessTypeRepository;
  @InjectMocks
  private StableAccessTypeValidationService validationService;


  @Test
  @DisplayName("Validation passes with valid access type")
  void validAccessType() {
    final var accessType = new AccessTypeWithSchemeUri()
      .id(OPEN_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));
    when(accessTypeRepository.findByUriAndSchemeId(OPEN_ACCESS_TYPE_ID, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(ACCESS_TYPE_RECORD));

    final var failures = validationService.validate(accessType);

    assertThat(failures, empty());

    verify(accessTypeSchemeRepository).findByUri(ACCESS_TYPE_SCHEME_URI);
    verify(accessTypeRepository).findByUriAndSchemeId(OPEN_ACCESS_TYPE_ID, ACCESS_TYPE_SCHEME_ID);
  }

  @Test
  @DisplayName("Validation fails when id is null")
  void nullId() {
    final var accessType = new AccessTypeWithSchemeUri()
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(accessType);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type.id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when id is empty string")
  void emptyId() {
    final var accessType = new AccessTypeWithSchemeUri()
      .id("")
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(accessType);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type.id")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is null")
  void nullSchemeUri() {
    final var accessType = new AccessTypeWithSchemeUri()
      .id(OPEN_ACCESS_TYPE_ID);

    final var failures = validationService.validate(accessType);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is empty")
  void emptySchemeUri() {
    final var accessType = new AccessTypeWithSchemeUri()
      .id(OPEN_ACCESS_TYPE_ID)
      .schemeUri("");

    final var failures = validationService.validate(accessType);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails when schemeUri is invalid")
  void invalidSchemeUri() {
    final var accessType = new AccessTypeWithSchemeUri()
      .id(OPEN_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(accessType);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type.schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
    ));
  }

  @Test
  @DisplayName("Validation fails when type is null")
  void nullType() {
    final var failures = validationService.validate(null);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(accessTypeSchemeRepository);
    verifyNoInteractions(accessTypeRepository);
  }

  @Test
  @DisplayName("Validation fails when id not found in scheme")
  void invalidTypeForScheme() {
    final var accessType = new AccessTypeWithSchemeUri()
      .id(OPEN_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    when(accessTypeRepository.findByUriAndSchemeId(OPEN_ACCESS_TYPE_ID, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(accessType);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type.id")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }
}