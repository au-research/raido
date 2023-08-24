package au.org.raid.api.validator;

import au.org.raid.api.repository.ContributorRoleRepository;
import au.org.raid.api.repository.ContributorRoleSchemeRepository;
import au.org.raid.api.util.TestConstants;
import au.org.raid.db.jooq.api_svc.tables.records.ContributorRoleRecord;
import au.org.raid.db.jooq.api_svc.tables.records.ContributorRoleSchemeRecord;
import au.org.raid.idl.raidv2.model.ContributorRoleWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContributorRoleValidatorTest {
    private static final int CONTRIBUTOR_ROLE_TYPE_SCHEME_ID = 1;

    private static final ContributorRoleSchemeRecord CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD =
            new ContributorRoleSchemeRecord()
                    .setId(CONTRIBUTOR_ROLE_TYPE_SCHEME_ID)
                    .setUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI);

    private static final ContributorRoleRecord CONTRIBUTOR_ROLE_TYPE_RECORD =
            new ContributorRoleRecord()
                    .setSchemeId(CONTRIBUTOR_ROLE_TYPE_SCHEME_ID)
                    .setUri(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

    @Mock
    private ContributorRoleSchemeRepository contributorRoleSchemeRepository;

    @Mock
    private ContributorRoleRepository contributorRoleRepository;

    @InjectMocks
    private ContributorRoleValidator validationService;

    @Test
    @DisplayName("Validation passes with valid ContributorRole")
    void validContributorRoleWithSchemeUri() {
        final var role = new ContributorRoleWithSchemeUri()
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE)
                .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI);

        when(contributorRoleSchemeRepository.findByUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

        when(contributorRoleRepository
                .findByUriAndSchemeId(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE, CONTRIBUTOR_ROLE_TYPE_SCHEME_ID))
                .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_RECORD));

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, empty());
    }

    @Test
    @DisplayName("Validation fails with null schemeUri")
    void nullSchemeUri() {
        final var role = new ContributorRoleWithSchemeUri()
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[2].roles[3].schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleSchemeRepository);
        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with empty schemeUri")
    void emptySchemeUri() {
        final var role = new ContributorRoleWithSchemeUri()
                .schemeUri("")
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[2].roles[3].schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleSchemeRepository);
        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with invalid schemeUri")
    void invalidSchemeUri() {
        final var role = new ContributorRoleWithSchemeUri()
                .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        when(contributorRoleSchemeRepository.findByUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI))
                .thenReturn(Optional.empty());

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[2].roles[3].schemeUri")
                        .errorType("invalidValue")
                        .message("scheme is unknown/unsupported")
        ));

        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with null role")
    void nullRole() {
        final var role = new ContributorRoleWithSchemeUri()
                .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI);

        when(contributorRoleSchemeRepository.findByUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[2].roles[3].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with empty role")
    void emptyRole() {
        final var role = new ContributorRoleWithSchemeUri()
                .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI)
                .id("");

        when(contributorRoleSchemeRepository.findByUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[2].roles[3].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with invalid role")
    void invalidRole() {
        final var role = new ContributorRoleWithSchemeUri()
                .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        when(contributorRoleSchemeRepository.findByUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(CONTRIBUTOR_ROLE_TYPE_SCHEME_RECORD));

        when(contributorRoleRepository
                .findByUriAndSchemeId(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE, CONTRIBUTOR_ROLE_TYPE_SCHEME_ID))
                .thenReturn(Optional.empty());

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[2].roles[3].id")
                        .errorType("invalidValue")
                        .message("id does not exist within the given scheme")
        ));
    }
}