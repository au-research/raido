package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.repository.ContributorRoleTypeRepository;
import raido.apisvc.repository.ContributorRoleTypeSchemeRepository;
import raido.db.jooq.api_svc.tables.records.ContributorRoleTypeRecord;
import raido.db.jooq.api_svc.tables.records.ContributorRoleTypeSchemeRecord;
import raido.idl.raidv2.model.ContribRole;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static raido.apisvc.util.TestConstants.CONTRIBUTOR_ROLE_TYPE_SCHEME_URI;
import static raido.apisvc.util.TestConstants.SUPERVISION_CONTRIBUTOR_ROLE;

@ExtendWith(MockitoExtension.class)
class StableContributorRoleValidationServiceTest {
  private static final int CONTRIBUTOR_ROLE_TYPE_SCHEME_ID = 1;

  private static final ContributorRoleTypeSchemeRecord CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD =
    new ContributorRoleTypeSchemeRecord()
      .setId(CONTRIBUTOR_ROLE_TYPE_SCHEME_ID)
      .setUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI);

  private static final ContributorRoleTypeRecord CONTRIBUTOR_ROLE_TYPE_RECORD =
    new ContributorRoleTypeRecord()
      .setSchemeId(CONTRIBUTOR_ROLE_TYPE_SCHEME_ID)
      .setUri(SUPERVISION_CONTRIBUTOR_ROLE);

  @Mock
  private ContributorRoleTypeSchemeRepository contributorRoleTypeSchemeRepository;

  @Mock
  private ContributorRoleTypeRepository contributorRoleTypeRepository;

  @InjectMocks
  private StableContributorRoleValidationService validationService;

  @Test
  @DisplayName("Validation passes with valid ContributorRole")
  void validContributorRole() {
    final var role = new ContribRole()
      .role(SUPERVISION_CONTRIBUTOR_ROLE)
      .schemeUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI);

    when(contributorRoleTypeSchemeRepository.findByUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

    when(contributorRoleTypeRepository
      .findByUriAndSchemeId(SUPERVISION_CONTRIBUTOR_ROLE, CONTRIBUTOR_ROLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_RECORD));

    final var failures = validationService.validate(role, 2, 3);

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Validation fails with null schemeUri")
  void nullSchemeUri() {
    final var role = new ContribRole()
      .role(SUPERVISION_CONTRIBUTOR_ROLE);

    final var failures = validationService.validate(role, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].roles[3].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorRoleTypeSchemeRepository);
    verifyNoInteractions(contributorRoleTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with empty schemeUri")
  void emptySchemeUri() {
    final var role = new ContribRole()
      .schemeUri("")
      .role(SUPERVISION_CONTRIBUTOR_ROLE);

    final var failures = validationService.validate(role, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].roles[3].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorRoleTypeSchemeRepository);
    verifyNoInteractions(contributorRoleTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with invalid schemeUri")
  void invalidSchemeUri() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI)
      .role(SUPERVISION_CONTRIBUTOR_ROLE);

    when(contributorRoleTypeSchemeRepository.findByUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(role, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].roles[3].schemeUri")
        .errorType("invalidValue")
        .message("has invalid/unsupported value")
    ));

    verifyNoInteractions(contributorRoleTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with null role")
  void nullRole() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI);

    when(contributorRoleTypeSchemeRepository.findByUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(role, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].roles[3].role")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorRoleTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with empty role")
  void emptyRole() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI)
      .role("");

    when(contributorRoleTypeSchemeRepository.findByUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(role, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].roles[3].role")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorRoleTypeRepository);
  }

  @Test
  @DisplayName("Validation fails with invalid role")
  void invalidRole() {
    final var role = new ContribRole()
      .schemeUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI)
      .role(SUPERVISION_CONTRIBUTOR_ROLE);

    when(contributorRoleTypeSchemeRepository.findByUri(CONTRIBUTOR_ROLE_TYPE_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

    when(contributorRoleTypeRepository
      .findByUriAndSchemeId(SUPERVISION_CONTRIBUTOR_ROLE, CONTRIBUTOR_ROLE_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(role, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].roles[3].role")
        .errorType("invalidValue")
        .message("has invalid/unsupported value")
    ));
  }
}