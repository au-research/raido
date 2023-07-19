package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.idl.raidv2.model.ContribPosition;
import raido.idl.raidv2.model.ContribRole;
import raido.idl.raidv2.model.Contributor;
import raido.idl.raidv2.model.ValidationFailure;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static raido.apisvc.endpoint.message.ValidationMessage.FIELD_MUST_BE_SET_MESSAGE;
import static raido.apisvc.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static raido.apisvc.util.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class StableContributorValidationServiceTest {
  @Mock
  private StableOrcidValidationService orcidValidationService;

  @Mock
  private StableContributorRoleValidationService roleValidationService;

  @Mock
  private StableContributorPositionValidationService positionValidationService;

  @InjectMocks
  private StableContributorValidationService validationService;

  @Test
  @DisplayName("Validation fails with missing positions")
  void missingPositions() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
      .type(SUPERVISION_CONTRIBUTOR_ROLE);

    final var contributor = new Contributor()
      .schemeUri(CONTRIBUTOR_IDENTIFIER_SCHEME_URI)
      .id(VALID_ORCID)
      .roles(List.of(role));

    final var failures = validationService.validate(List.of(contributor));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors.positions")
        .errorType("invalidValue")
        .message("leader must be specified")
    ));

    verify(roleValidationService).validate(role, 0, 0);
    verifyNoInteractions(positionValidationService);
  }

  @Test
  @DisplayName("Validation fails with missing lead position")
  void missingLeadPositions() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
      .type(SUPERVISION_CONTRIBUTOR_ROLE);

    final var position = new ContribPosition()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .type("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/other-participant.json")
      .startDate(LocalDate.now());

    final var contributor = new Contributor()
      .schemeUri(CONTRIBUTOR_IDENTIFIER_SCHEME_URI)
      .id(VALID_ORCID)
      .roles(List.of(role))
      .positions(List.of(position));

    final var failures = validationService.validate(List.of(contributor));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors.positions")
        .errorType("invalidValue")
        .message("leader must be specified")
    ));

    verify(roleValidationService).validate(role, 0, 0);
    verify(positionValidationService).validate(position, 0, 0);
  }

  @Test
  @DisplayName("Validation fails with no contributors")
  void noContributors() {
    final var failures = validationService.validate(Collections.emptyList());

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(roleValidationService);
    verifyNoInteractions(positionValidationService);
  }

  @Test
  @DisplayName("Validation fails with null contributors")
  void nullContributors() {
    final var failures = validationService.validate(null);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(roleValidationService);
    verifyNoInteractions(positionValidationService);
  }

  @Test
  @DisplayName("Validation passes with valid contributor")
  void validContributor() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
      .type(SUPERVISION_CONTRIBUTOR_ROLE);

    final var position = new ContribPosition()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .type(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now());

    final var contributor = new Contributor()
      .schemeUri(CONTRIBUTOR_IDENTIFIER_SCHEME_URI)
      .id(VALID_ORCID)
      .roles(List.of(role))
      .positions(List.of(position));

    final var failures = validationService.validate(List.of(contributor));

    assertThat(failures, empty());

    verify(roleValidationService).validate(role, 0, 0);
    verify(positionValidationService).validate(position, 0, 0);
  }

  @Test
  @DisplayName("Validation passes with missing schemeUri")
  void missingSchemeUri() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
      .type(SUPERVISION_CONTRIBUTOR_ROLE);

    final var position = new ContribPosition()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .type(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now());

    final var contributor = new Contributor()
      .id(VALID_ORCID)
      .roles(List.of(role))
      .positions(List.of(position));

    final var failures = validationService.validate(List.of(contributor));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[0].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));

    verify(roleValidationService).validate(role, 0, 0);
    verify(positionValidationService).validate(position, 0, 0);
  }

  @Test
  @DisplayName("Validation passes with empty schemeUri")
  void emptySchemeUri() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
      .type(SUPERVISION_CONTRIBUTOR_ROLE);

    final var position = new ContribPosition()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .type(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now());

    final var contributor = new Contributor()
      .schemeUri("")
      .id(VALID_ORCID)
      .roles(List.of(role))
      .positions(List.of(position));

    final var failures = validationService.validate(List.of(contributor));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[0].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));

    verify(roleValidationService).validate(role, 0, 0);
    verify(positionValidationService).validate(position, 0, 0);
  }

  @Test
  @DisplayName("Failures validation services are added to return value")
  void roleValidationFailures() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
      .type(SUPERVISION_CONTRIBUTOR_ROLE);

    final var position = new ContribPosition()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .type(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now());

    final var contributor = new Contributor()
      .schemeUri(CONTRIBUTOR_IDENTIFIER_SCHEME_URI)
      .id(VALID_ORCID)
      .roles(List.of(role))
      .positions(List.of(position));

    final var orcidError = new ValidationFailure()
      .fieldId("contributors[0].id")
      .errorType(NOT_SET_TYPE)
      .message(FIELD_MUST_BE_SET_MESSAGE);

    final var roleError = new ValidationFailure()
      .fieldId("contributors[0].roles[0].role")
      .errorType(NOT_SET_TYPE)
      .message(FIELD_MUST_BE_SET_MESSAGE);

    final var positionError = new ValidationFailure()
      .fieldId("contributors[0].positions[0].position")
      .errorType(NOT_SET_TYPE)
      .message(FIELD_MUST_BE_SET_MESSAGE);

    when(orcidValidationService.validate(VALID_ORCID, 0)).thenReturn(List.of(orcidError));
    when(roleValidationService.validate(role, 0, 0)).thenReturn(List.of(roleError));
    when(positionValidationService.validate(position, 0,0)).thenReturn(List.of(positionError));

    final var failures = validationService.validate(List.of(contributor));

    assertThat(failures, hasSize(3));
    assertThat(failures, hasItems(orcidError, roleError, positionError));

    verify(orcidValidationService).validate(VALID_ORCID, 0);
    verify(roleValidationService).validate(role, 0, 0);
    verify(positionValidationService).validate(position, 0, 0);
  }

  @Test
  @DisplayName("Validation fails with more than one lead position")
  void multipleLeadPositions() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
      .type(SUPERVISION_CONTRIBUTOR_ROLE);

    final var position = new ContribPosition()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .type(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now());

    final var contributor = new Contributor()
      .schemeUri(CONTRIBUTOR_IDENTIFIER_SCHEME_URI)
      .id(VALID_ORCID)
      .roles(List.of(role))
      .positions(List.of(position));

    final var failures = validationService.validate(List.of(contributor, contributor));

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(new ValidationFailure()
      .fieldId("contributors.positions")
      .errorType("invalidValue")
      .message("only one leader can be specified")));
  }
}