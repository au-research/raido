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
import raido.idl.raidv2.model.Access;
import raido.idl.raidv2.model.ValidationFailure;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static raido.apisvc.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class StableAccessValidationServiceTest {
  private static final int ACCESS_TYPE_SCHEME_ID = 1;
  private static final AccessTypeSchemeRecord ACCESS_TYPE_SCHEME_RECORD =
    new AccessTypeSchemeRecord()
      .setId(ACCESS_TYPE_SCHEME_ID)
      .setUri(ACCESS_TYPE_SCHEME_URI);

  private static final AccessTypeRecord ACCESS_TYPE_RECORD =
    new AccessTypeRecord()
      .setSchemeId(ACCESS_TYPE_SCHEME_ID)
      .setUri(OPEN_ACCESS_TYPE);

  @Mock
  private AccessTypeSchemeRepository accessTypeSchemeRepository;

  @Mock
  private AccessTypeRepository accessTypeRepository;

  @InjectMocks
  private StableAccessValidationService validationService;

  @Test
  @DisplayName("Validation passes on closed raid with correct fields")
  void closedValidationSucceeds() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(CLOSED_ACCESS_TYPE)
      .accessStatement("Closed");

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    when(accessTypeRepository.findByUriAndSchemeId(CLOSED_ACCESS_TYPE, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(ACCESS_TYPE_RECORD));

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Validation passes on embargoed raid with correct fields")
  void embargoedValidationSucceeds() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(EMBARGOED_ACCESS_TYPE)
      .embargoExpiry(LocalDate.now());

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    when(accessTypeRepository.findByUriAndSchemeId(EMBARGOED_ACCESS_TYPE, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(ACCESS_TYPE_RECORD));

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures, empty());
  }


  @Test
  @DisplayName("Validation fails with missing accessStatement on closed raid")
  void missingAccessStatement() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(CLOSED_ACCESS_TYPE);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    when(accessTypeRepository.findByUriAndSchemeId(CLOSED_ACCESS_TYPE, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(ACCESS_TYPE_RECORD));

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.accessStatement")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with blank accessStatement on closed raid")
  void blankStatement() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(CLOSED_ACCESS_TYPE)
      .accessStatement("");

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    when(accessTypeRepository.findByUriAndSchemeId(CLOSED_ACCESS_TYPE, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(ACCESS_TYPE_RECORD));

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.accessStatement")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with missing type")
  void missingType() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(accessTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with blank type")
  void blankType() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type("");

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(accessTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with missing schemeUri")
  void missingSchemeUri() {
    final var access = new Access()
      .type(OPEN_ACCESS_TYPE);

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(accessTypeSchemeRepository);
    verifyNoInteractions(accessTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with blank schemeUri")
  void blankSchemeUri() {
    final var access = new Access()
      .schemeUri("")
      .type(OPEN_ACCESS_TYPE);

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(accessTypeSchemeRepository);
    verifyNoInteractions(accessTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with missing embargoExpiry on embargoed raid")
  void missingEmbargoExpiry() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(EMBARGOED_ACCESS_TYPE);

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    when(accessTypeRepository.findByUriAndSchemeId(EMBARGOED_ACCESS_TYPE, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(ACCESS_TYPE_RECORD));

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.embargoExpiry")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with invalid schemeUri")
  void invalidSchemeUri() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(EMBARGOED_ACCESS_TYPE)
      .embargoExpiry(LocalDate.now());

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI)).thenReturn(Optional.empty());

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.schemeUri")
        .errorType("invalidValue")
        .message("has invalid/unsupported value")
    ));
  }

  @Test
  @DisplayName("Validation fails when type is not found in scheme")
  void invalidTypeForScheme() {
    final var access = new Access()
      .schemeUri(ACCESS_TYPE_SCHEME_URI)
      .type(EMBARGOED_ACCESS_TYPE)
      .embargoExpiry(LocalDate.now());

    when(accessTypeSchemeRepository.findByUri(ACCESS_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(ACCESS_TYPE_SCHEME_RECORD));

    when(accessTypeRepository.findByUriAndSchemeId(EMBARGOED_ACCESS_TYPE, ACCESS_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final List<ValidationFailure> failures = validationService.validateAccess(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type")
        .errorType("invalidValue")
        .message("has invalid/unsupported value")
    ));
  }
}