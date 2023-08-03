package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.Access;
import raido.idl.raidv2.model.AccessTypeWithSchemeUri;
import raido.idl.raidv2.model.ValidationFailure;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.verify;
import static raido.apisvc.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class StableAccessValidationServiceTest {
  @Mock
  private StableAccessTypeValidationService accessTypeValidationService;

  @InjectMocks
  private StableAccessValidationService validationService;

  @Test
  @DisplayName("Validation passes on closed raid with correct fields")
  void closedValidationSucceeds() {
    final var type = new AccessTypeWithSchemeUri()
      .id(CLOSED_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    final var access = new Access()
      .type(type)
      .accessStatement("Closed");

    final List<ValidationFailure> failures = validationService.validate(access);

    assertThat(failures, empty());
    verify(accessTypeValidationService).validate(type);
  }

  @Test
  @DisplayName("Validation passes on embargoed raid with correct fields")
  void embargoedValidationSucceeds() {
    final var type = new AccessTypeWithSchemeUri()
      .id(CLOSED_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    final var access = new Access()
      .type(type)
      .accessStatement("Embargoed")
      .embargoExpiry(LocalDate.now());

    final List<ValidationFailure> failures = validationService.validate(access);

    assertThat(failures, empty());
  }


  @Test
  @DisplayName("Validation fails with missing accessStatement on closed raid")
  void missingAccessStatement() {
    final var type = new AccessTypeWithSchemeUri()
      .id(CLOSED_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    final var access = new Access()
      .type(type);

    final List<ValidationFailure> failures = validationService.validate(access);

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
  void blankStatementClosed() {
    final var type = new AccessTypeWithSchemeUri()
      .id(CLOSED_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    final var access = new Access()
      .type(type)
      .accessStatement("");

    final List<ValidationFailure> failures = validationService.validate(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.accessStatement")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with blank accessStatement on embargoed raid")
  void blankStatementEmbargoed() {
    final var type = new AccessTypeWithSchemeUri()
      .id(EMBARGOED_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    final var access = new Access()
      .type(type)
      .accessStatement("")
      .embargoExpiry(LocalDate.now());

    final List<ValidationFailure> failures = validationService.validate(access);

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
    final var access = new Access();

    final List<ValidationFailure> failures = validationService.validate(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.type")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with missing embargoExpiry on embargoed raid")
  void missingEmbargoExpiry() {
    final var type = new AccessTypeWithSchemeUri()
      .id(EMBARGOED_ACCESS_TYPE_ID)
      .schemeUri(ACCESS_TYPE_SCHEME_URI);

    final var access = new Access()
      .type(type)
      .accessStatement("access statement");

    final List<ValidationFailure> failures = validationService.validate(access);

    assertThat(failures.size(), is(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("access.embargoExpiry")
        .errorType("notSet")
        .message("field must be set")
    ));
  }
}