package au.org.raid.api.validator;

import au.org.raid.api.repository.OrganisationRoleRepository;
import au.org.raid.api.repository.OrganisationRoleSchemeRepository;
import au.org.raid.db.jooq.api_svc.tables.records.OrganisationRoleRecord;
import au.org.raid.db.jooq.api_svc.tables.records.OrganisationRoleSchemeRecord;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static au.org.raid.api.util.TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE;
import static au.org.raid.api.util.TestConstants.ORGANISATION_ROLE_SCHEME_URI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisationRoleValidatorTest {
    private static final int ORGANISATION_ROLE_SCHEME_ID = 1;

    private static final OrganisationRoleSchemeRecord ORGANISATION_ROLE_SCHEME_RECORD =
            new OrganisationRoleSchemeRecord()
                    .setId(ORGANISATION_ROLE_SCHEME_ID)
                    .setUri(ORGANISATION_ROLE_SCHEME_URI);

    private static final OrganisationRoleRecord ORGANISATION_ROLE_RECORD =
            new OrganisationRoleRecord()
                    .setSchemeId(ORGANISATION_ROLE_SCHEME_ID)
                    .setUri(LEAD_RESEARCH_ORGANISATION_ROLE);

    @Mock
    private OrganisationRoleSchemeRepository contributorRoleSchemeRepository;

    @Mock
    private OrganisationRoleRepository contributorRoleRepository;

    @InjectMocks
    private OrganisationRoleValidator validationService;

    @Test
    @DisplayName("Validation passes with valid OrganisationRole")
    void validOrganisationRoleWithSchemeUri() {
        final var role = new OrganisationRoleWithSchemeUri()
                .id(LEAD_RESEARCH_ORGANISATION_ROLE)
                .schemeUri(ORGANISATION_ROLE_SCHEME_URI);

        when(contributorRoleSchemeRepository.findByUri(ORGANISATION_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(ORGANISATION_ROLE_SCHEME_RECORD));

        when(contributorRoleRepository
                .findByUriAndSchemeId(LEAD_RESEARCH_ORGANISATION_ROLE, ORGANISATION_ROLE_SCHEME_ID))
                .thenReturn(Optional.of(ORGANISATION_ROLE_RECORD));

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, empty());
    }

    @Test
    @DisplayName("Validation fails with null schemeUri")
    void nullSchemeUri() {
        final var role = new OrganisationRoleWithSchemeUri()
                .id(LEAD_RESEARCH_ORGANISATION_ROLE);

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[2].roles[3].schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleSchemeRepository);
        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with empty schemeUri")
    void emptySchemeUri() {
        final var role = new OrganisationRoleWithSchemeUri()
                .schemeUri("")
                .id(LEAD_RESEARCH_ORGANISATION_ROLE);

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[2].roles[3].schemeUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleSchemeRepository);
        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with invalid schemeUri")
    void invalidSchemeUri() {
        final var role = new OrganisationRoleWithSchemeUri()
                .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
                .id(LEAD_RESEARCH_ORGANISATION_ROLE);

        when(contributorRoleSchemeRepository.findByUri(ORGANISATION_ROLE_SCHEME_URI))
                .thenReturn(Optional.empty());

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[2].roles[3].schemeUri")
                        .errorType("invalidValue")
                        .message("scheme is unknown/unsupported")
        ));

        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with null role")
    void nullRole() {
        final var role = new OrganisationRoleWithSchemeUri()
                .schemeUri(ORGANISATION_ROLE_SCHEME_URI);

        when(contributorRoleSchemeRepository.findByUri(ORGANISATION_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(ORGANISATION_ROLE_SCHEME_RECORD));

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[2].roles[3].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with empty role")
    void emptyRole() {
        final var role = new OrganisationRoleWithSchemeUri()
                .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
                .id("");

        when(contributorRoleSchemeRepository.findByUri(ORGANISATION_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(ORGANISATION_ROLE_SCHEME_RECORD));

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[2].roles[3].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verifyNoInteractions(contributorRoleRepository);
    }

    @Test
    @DisplayName("Validation fails with invalid role")
    void invalidRole() {
        final var role = new OrganisationRoleWithSchemeUri()
                .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
                .id(LEAD_RESEARCH_ORGANISATION_ROLE);

        when(contributorRoleSchemeRepository.findByUri(ORGANISATION_ROLE_SCHEME_URI))
                .thenReturn(Optional.of(ORGANISATION_ROLE_SCHEME_RECORD));

        when(contributorRoleRepository
                .findByUriAndSchemeId(LEAD_RESEARCH_ORGANISATION_ROLE, ORGANISATION_ROLE_SCHEME_ID))
                .thenReturn(Optional.empty());

        final var failures = validationService.validate(role, 2, 3);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[2].roles[3].id")
                        .errorType("invalidValue")
                        .message("id does not exist within the given scheme")
        ));
    }
}