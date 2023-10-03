package au.org.raid.api.validator;

import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.OrganisationRoleWithSchemaUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_MESSAGE;
import static au.org.raid.api.endpoint.message.ValidationMessage.NOT_SET_TYPE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisationValidatorTest {
    @Mock
    private OrganisationRoleValidator roleValidationService;

    @Mock
    private RorValidator rorValidationService;

    @InjectMocks
    private OrganisationValidator validationService;

    @Test
    @DisplayName("Validation passes with valid organisation")
    void validOrganisation() {
        final var role = new OrganisationRoleWithSchemaUri()
                .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                .id(TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE)
                .startDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        final var organisation = new Organisation()
                .id(TestConstants.VALID_ROR)
                .schemaUri(TestConstants.HTTPS_ROR_ORG)
                .role(List.of(role));

        final var failures = validationService.validate(List.of(organisation));

        assertThat(failures, empty());
        verify(rorValidationService).validate(TestConstants.VALID_ROR, 0);
        verify(roleValidationService).validate(role, 0, 0);
    }

    @Test
    @DisplayName("Validation fails with missing schemaUri")
    void missingIdentifierSchemeUri() {
        final var organisation = new Organisation()
                .id(TestConstants.VALID_ROR)
                .role(List.of(
                        new OrganisationRoleWithSchemaUri()
                                .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                                .id(TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE)
                                .startDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                                .endDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                ));

        final var failures = validationService.validate(List.of(organisation));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisation[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails with empty schemaUri")
    void emptyIdentifierSchemeUri() {
        final var organisation = new Organisation()
                .id(TestConstants.VALID_ROR)
                .schemaUri("")
                .role(List.of(
                        new OrganisationRoleWithSchemaUri()
                                .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                                .id(TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE)
                                .startDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                                .endDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
                ));

        final var failures = validationService.validate(List.of(organisation));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisation[0].schemaUri")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("ROR validation failures are returned")
    void rorValidationFailuresReturned() {
        final var role = new OrganisationRoleWithSchemaUri()
                .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                .id(TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE)
                .startDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        final var organisation = new Organisation()
                .id(TestConstants.VALID_ROR)
                .schemaUri(TestConstants.HTTPS_ROR_ORG)
                .role(List.of(role));

        final var rorError = new ValidationFailure()
                .fieldId("organisation[0].id")
                .errorType(NOT_SET_TYPE)
                .message(NOT_SET_MESSAGE);

        when(rorValidationService.validate(TestConstants.VALID_ROR, 0))
                .thenReturn(List.of(rorError));

        final var failures = validationService.validate(List.of(organisation));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(rorError));

        verify(rorValidationService).validate(TestConstants.VALID_ROR, 0);
        verify(roleValidationService).validate(role, 0, 0);
    }

    @Test
    @DisplayName("Role validation failures are returned")
    void roleValidationFailuresReturned() {
        final var role = new OrganisationRoleWithSchemaUri()
                .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                .id(TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE)
                .startDate(LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        final var organisation = new Organisation()
                .id(TestConstants.VALID_ROR)
                .schemaUri(TestConstants.HTTPS_ROR_ORG)
                .role(List.of(role));

        final var roleError = new ValidationFailure()
                .fieldId("organisation[0].role[0].id")
                .errorType(NOT_SET_TYPE)
                .message(NOT_SET_MESSAGE);

        when(roleValidationService.validate(role, 0, 0))
                .thenReturn(List.of(roleError));

        final var failures = validationService.validate(List.of(organisation));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(roleError));

        verify(rorValidationService).validate(TestConstants.VALID_ROR, 0);
        verify(roleValidationService).validate(role, 0, 0);
    }
}