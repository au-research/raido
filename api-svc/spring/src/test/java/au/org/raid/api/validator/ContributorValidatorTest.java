package au.org.raid.api.validator;

import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemaUri;
import au.org.raid.idl.raidv2.model.ContributorRoleWithSchemaUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContributorValidatorTest {
    @Mock
    private OrcidValidator orcidValidationService;

    @Mock
    private ContributorRoleValidator roleValidationService;

    @Mock
    private ContributorPositionValidator positionValidationService;

    @InjectMocks
    private ContributorValidator validationService;

    @Test
    @DisplayName("Validation fails with missing positions")
    void missingPositions() {
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var contributor = new Contributor()
                .schemaUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .id(TestConstants.VALID_ORCID)
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
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var position = new ContributorPositionWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/other-participant.json")
                .startDate(LocalDate.now());

        final var contributor = new Contributor()
                .schemaUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .id(TestConstants.VALID_ORCID)
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
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var position = new ContributorPositionWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                .id(TestConstants.LEADER_CONTRIBUTOR_POSITION)
                .startDate(LocalDate.now());

        final var contributor = new Contributor()
                .schemaUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .id(TestConstants.VALID_ORCID)
                .roles(List.of(role))
                .positions(List.of(position));

        final var failures = validationService.validate(List.of(contributor));

        assertThat(failures, empty());

        verify(roleValidationService).validate(role, 0, 0);
        verify(positionValidationService).validate(position, 0, 0);
    }

    @Test
    @DisplayName("Failures in validation services are added to return value")
    void roleValidationFailures() {
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var position = new ContributorPositionWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                .id(TestConstants.LEADER_CONTRIBUTOR_POSITION)
                .startDate(LocalDate.now());

        final var contributor = new Contributor()
                .schemaUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .id(TestConstants.VALID_ORCID)
                .roles(List.of(role))
                .positions(List.of(position));

        final var orcidError = new ValidationFailure()
                .fieldId("contributors[0].id")
                .errorType(NOT_SET_TYPE)
                .message(NOT_SET_MESSAGE);

        final var roleError = new ValidationFailure()
                .fieldId("contributors[0].roles[0].role")
                .errorType(NOT_SET_TYPE)
                .message(NOT_SET_MESSAGE);

        final var positionError = new ValidationFailure()
                .fieldId("contributors[0].positions[0].position")
                .errorType(NOT_SET_TYPE)
                .message(NOT_SET_MESSAGE);

        when(orcidValidationService.validate(TestConstants.VALID_ORCID, 0)).thenReturn(List.of(orcidError));
        when(roleValidationService.validate(role, 0, 0)).thenReturn(List.of(roleError));
        when(positionValidationService.validate(position, 0, 0)).thenReturn(List.of(positionError));

        final var failures = validationService.validate(List.of(contributor));

        assertThat(failures, hasSize(3));
        assertThat(failures, hasItems(orcidError, roleError, positionError));

        verify(orcidValidationService).validate(TestConstants.VALID_ORCID, 0);
        verify(roleValidationService).validate(role, 0, 0);
        verify(positionValidationService).validate(position, 0, 0);
    }

    @Test
    @DisplayName("Validation fails with more than one lead position")
    void multipleLeadPositions() {
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var position = new ContributorPositionWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                .id(TestConstants.LEADER_CONTRIBUTOR_POSITION)
                .startDate(LocalDate.now());

        final var contributor = new Contributor()
                .schemaUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .id(TestConstants.VALID_ORCID)
                .roles(List.of(role))
                .positions(List.of(position));

        final var failures = validationService.validate(List.of(contributor, contributor));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(new ValidationFailure()
                .fieldId("contributors.positions")
                .errorType("invalidValue")
                .message("only one leader can be specified")));
    }

    @Test
    @DisplayName("Validation fails with null schemaUri")
    void nullIdentifierSchemeUri() {
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var position = new ContributorPositionWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                .id(TestConstants.LEADER_CONTRIBUTOR_POSITION)
                .startDate(LocalDate.now());

        final var contributor = new Contributor()
                .id(TestConstants.VALID_ORCID)
                .roles(List.of(role))
                .positions(List.of(position));

        final var failures = validationService.validate(List.of(contributor));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verify(roleValidationService).validate(role, 0, 0);
        verify(positionValidationService).validate(position, 0, 0);
    }
    @Test
    @DisplayName("Validation fails with empty schemaUri")
    void emptyIdentifierSchemeUri() {
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var position = new ContributorPositionWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                .id(TestConstants.LEADER_CONTRIBUTOR_POSITION)
                .startDate(LocalDate.now());

        final var contributor = new Contributor()
                .id(TestConstants.VALID_ORCID)
                .schemaUri("")
                .roles(List.of(role))
                .positions(List.of(position));

        final var failures = validationService.validate(List.of(contributor));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));

        verify(roleValidationService).validate(role, 0, 0);
        verify(positionValidationService).validate(position, 0, 0);
    }
    @Test
    @DisplayName("Validation fails with invalid schemaUri")
    void invalidIdentifierSchemeUri() {
        final var role = new ContributorRoleWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)
                .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE);

        final var position = new ContributorPositionWithSchemaUri()
                .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                .id(TestConstants.LEADER_CONTRIBUTOR_POSITION)
                .startDate(LocalDate.now());

        final var contributor = new Contributor()
                .id(TestConstants.VALID_ORCID)
                .schemaUri("https://example.org/")
                .roles(List.of(role))
                .positions(List.of(position));

        final var failures = validationService.validate(List.of(contributor));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("contributors[0].schemaUri")
                        .errorType("invalidValue")
                        .message("has invalid/unsupported value - should be https://orcid.org/")
        ));

        verify(roleValidationService).validate(role, 0, 0);
        verify(positionValidationService).validate(position, 0, 0);
    }
}