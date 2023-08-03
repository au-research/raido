package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.Organisation;
import raido.idl.raidv2.model.OrganisationRole;
import raido.idl.raidv2.model.OrganisationRoleWithSchemeUri;
import raido.idl.raidv2.model.ValidationFailure;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static raido.apisvc.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class StableOrganisationValidationServiceTest {
  @Mock
  private StableOrganisationRoleValidationService roleValidationService;

  @Mock
  private StableRorValidationService rorValidationService;

  @InjectMocks
  private StableOrganisationValidationService validationService;

  @Test
  @DisplayName("Validation passes with valid organisation")
  void validOrganisation() {
    final var role = new OrganisationRoleWithSchemeUri()
      .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
      .id(LEAD_RESEARCH_ORGANISATION_ROLE)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    final var organisation = new Organisation()
      .id(VALID_ROR)
      .identifierSchemeUri(HTTPS_ROR_ORG)
      .roles(List.of(role));

    final var failures = validationService.validate(List.of(organisation));

    assertThat(failures, empty());
    verify(rorValidationService).validate(VALID_ROR, 0);
    verify(roleValidationService).validate(role, 0, 0);
  }

  @Test
  @DisplayName("Validation fails with missing identifierSchemeUri")
  void missingIdentifierSchemeUri() {
    final var organisation = new Organisation()
      .id(VALID_ROR)
      .roles(List.of(
        new OrganisationRoleWithSchemeUri()
          .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
          .id(LEAD_RESEARCH_ORGANISATION_ROLE)
          .startDate(LocalDate.now().minusYears(1))
          .endDate(LocalDate.now())
      ));

    final var failures = validationService.validate(List.of(organisation));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("organisations[0].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("Validation fails with empty identifierSchemeUri")
  void emptyIdentifierSchemeUri() {
    final var organisation = new Organisation()
      .id(VALID_ROR)
      .identifierSchemeUri("")
      .roles(List.of(
        new OrganisationRoleWithSchemeUri()
          .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
          .id(LEAD_RESEARCH_ORGANISATION_ROLE)
          .startDate(LocalDate.now().minusYears(1))
          .endDate(LocalDate.now())
      ));

    final var failures = validationService.validate(List.of(organisation));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("organisations[0].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));
  }

  @Test
  @DisplayName("ROR validation failures are returned")
  void rorValidationFailuresReturned() {
    final var role = new OrganisationRoleWithSchemeUri()
      .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
      .id(LEAD_RESEARCH_ORGANISATION_ROLE)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    final var organisation = new Organisation()
      .id(VALID_ROR)
      .identifierSchemeUri(HTTPS_ROR_ORG)
      .roles(List.of(role));

    final var rorError = new ValidationFailure()
      .fieldId("organisations[0].id")
      .errorType(NOT_SET_TYPE)
      .message(FIELD_MUST_BE_SET_MESSAGE);

    when(rorValidationService.validate(VALID_ROR, 0))
      .thenReturn(List.of(rorError));

    final var failures = validationService.validate(List.of(organisation));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(rorError));

    verify(rorValidationService).validate(VALID_ROR, 0);
    verify(roleValidationService).validate(role, 0, 0);
  }

  @Test
  @DisplayName("Role validation failures are returned")
  void roleValidationFailuresReturned() {
    final var role = new OrganisationRoleWithSchemeUri()
      .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
      .id(LEAD_RESEARCH_ORGANISATION_ROLE)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    final var organisation = new Organisation()
      .id(VALID_ROR)
      .identifierSchemeUri(HTTPS_ROR_ORG)
      .roles(List.of(role));

    final var roleError = new ValidationFailure()
      .fieldId("organisations[0].roles[0].id")
      .errorType(NOT_SET_TYPE)
      .message(FIELD_MUST_BE_SET_MESSAGE);

    when(roleValidationService.validate(role, 0, 0))
      .thenReturn(List.of(roleError));

    final var failures = validationService.validate(List.of(organisation));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(roleError));

    verify(rorValidationService).validate(VALID_ROR, 0);
    verify(roleValidationService).validate(role, 0, 0);
  }
}