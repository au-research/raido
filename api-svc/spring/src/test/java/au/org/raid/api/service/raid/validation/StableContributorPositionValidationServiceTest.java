package au.org.raid.api.service.raid.validation;

import au.org.raid.api.repository.ContributorPositionRepository;
import au.org.raid.api.repository.ContributorPositionSchemeRepository;
import au.org.raid.db.jooq.api_svc.tables.records.ContributorPositionRecord;
import au.org.raid.db.jooq.api_svc.tables.records.ContributorPositionSchemeRecord;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static au.org.raid.api.util.TestConstants.CONTRIBUTOR_POSITION_SCHEME_URI;
import static au.org.raid.api.util.TestConstants.LEADER_CONTRIBUTOR_POSITION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StableContributorPositionValidationServiceTest {
  private static final int CONTRIBUTOR_POSITION_TYPE_SCHEME_ID = 1;

  private static final ContributorPositionSchemeRecord CONTRIBUTOR_POSITION_TYPE_SCHEME_RECORD =
    new ContributorPositionSchemeRecord()
      .setId(CONTRIBUTOR_POSITION_TYPE_SCHEME_ID)
      .setUri(CONTRIBUTOR_POSITION_SCHEME_URI);

  private static final ContributorPositionRecord CONTRIBUTOR_POSITION_TYPE_RECORD =
    new ContributorPositionRecord()
      .setSchemeId(CONTRIBUTOR_POSITION_TYPE_SCHEME_ID)
      .setUri(LEADER_CONTRIBUTOR_POSITION);


  @Mock
  private ContributorPositionSchemeRepository contributorPositionSchemeRepository;

  @Mock
  private ContributorPositionRepository contributorPositionRepository;

  @InjectMocks
  private StableContributorPositionValidationService validationService;

  @Test
  @DisplayName("Validation passes with valid ContributorPosition")
  void validContributorPositionWithSchemeUri() {
    final var position = new ContributorPositionWithSchemeUri()
      .id(LEADER_CONTRIBUTOR_POSITION)
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    when(contributorPositionSchemeRepository.findByUri(CONTRIBUTOR_POSITION_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_POSITION_TYPE_SCHEME_RECORD));

    when(contributorPositionRepository
      .findByUriAndSchemeId(LEADER_CONTRIBUTOR_POSITION, CONTRIBUTOR_POSITION_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(CONTRIBUTOR_POSITION_TYPE_RECORD));

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, empty());
  }

  @Test
  @DisplayName("Validation fails with null schemeUri")
  void nullSchemeUri() {
    final var position = new ContributorPositionWithSchemeUri()
      .id(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].positions[3].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorPositionSchemeRepository);
    verifyNoInteractions(contributorPositionRepository);
  }

  @Test
  @DisplayName("Validation fails with empty schemeUri")
  void emptySchemeUri() {
    final var position = new ContributorPositionWithSchemeUri()
      .schemeUri("")
      .id(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].positions[3].schemeUri")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorPositionSchemeRepository);
    verifyNoInteractions(contributorPositionRepository);
  }

  @Test
  @DisplayName("Validation fails with invalid schemeUri")
  void invalidSchemeUri() {
    final var position = new ContributorPositionWithSchemeUri()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .id(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    when(contributorPositionSchemeRepository.findByUri(CONTRIBUTOR_POSITION_SCHEME_URI))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].positions[3].schemeUri")
        .errorType("invalidValue")
        .message("scheme is unknown/unsupported")
    ));

    verifyNoInteractions(contributorPositionRepository);
  }

  @Test
  @DisplayName("Validation fails with null position")
  void nullPosition() {
    final var position = new ContributorPositionWithSchemeUri()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    when(contributorPositionSchemeRepository.findByUri(CONTRIBUTOR_POSITION_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_POSITION_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].positions[3].id")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorPositionRepository);
  }

  @Test
  @DisplayName("Validation fails with empty position")
  void emptyPosition() {
    final var position = new ContributorPositionWithSchemeUri()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .id("")
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    when(contributorPositionSchemeRepository.findByUri(CONTRIBUTOR_POSITION_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_POSITION_TYPE_SCHEME_RECORD));

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].positions[3].id")
        .errorType("notSet")
        .message("field must be set")
    ));

    verifyNoInteractions(contributorPositionRepository);
  }

  @Test
  @DisplayName("Validation fails with invalid position")
  void invalidPosition() {
    final var position = new ContributorPositionWithSchemeUri()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .id(LEADER_CONTRIBUTOR_POSITION)
      .startDate(LocalDate.now().minusYears(1))
      .endDate(LocalDate.now());

    when(contributorPositionSchemeRepository.findByUri(CONTRIBUTOR_POSITION_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_POSITION_TYPE_SCHEME_RECORD));

    when(contributorPositionRepository
      .findByUriAndSchemeId(LEADER_CONTRIBUTOR_POSITION, CONTRIBUTOR_POSITION_TYPE_SCHEME_ID))
      .thenReturn(Optional.empty());

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].positions[3].id")
        .errorType("invalidValue")
        .message("id does not exist within the given scheme")
    ));
  }

  @Test
  @DisplayName("Validation fails with null startDate")
  void nullstartDate() {
    final var position = new ContributorPositionWithSchemeUri()
      .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
      .id(LEADER_CONTRIBUTOR_POSITION)
      .endDate(LocalDate.now());

    when(contributorPositionSchemeRepository.findByUri(CONTRIBUTOR_POSITION_SCHEME_URI))
      .thenReturn(Optional.of(CONTRIBUTOR_POSITION_TYPE_SCHEME_RECORD));

    when(contributorPositionRepository
      .findByUriAndSchemeId(LEADER_CONTRIBUTOR_POSITION, CONTRIBUTOR_POSITION_TYPE_SCHEME_ID))
      .thenReturn(Optional.of(CONTRIBUTOR_POSITION_TYPE_RECORD));

    final var failures = validationService.validate(position, 2, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[2].positions[3].startDate")
        .errorType("notSet")
        .message("field must be set")
    ));
  }
}