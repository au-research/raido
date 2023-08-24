package au.org.raid.api.validator;

import au.org.raid.api.service.raid.validation.AccessStatementValidationService;
import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.Access;
import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.AccessTypeWithSchemeUri;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccessValidatorTest {
    @Mock
    private AccessStatementValidationService accessStatementValidationService;

    @Mock
    private AccessTypeValidator accessTypeValidationService;

    @InjectMocks
    private AccessValidator validationService;

    @Test
    @DisplayName("Validation passes on closed raid with correct fields")
    void closedValidationSucceeds() {
        final var type = new AccessTypeWithSchemeUri()
                .id(TestConstants.CLOSED_ACCESS_TYPE_ID)
                .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI);

        final var access = new Access()
                .type(type)
                .accessStatement(new AccessStatement().statement("Closed"));

        final List<ValidationFailure> failures = validationService.validate(access);

        assertThat(failures, empty());
        verify(accessTypeValidationService).validate(type);
    }

    @Test
    @DisplayName("Validation passes on embargoed raid with correct fields")
    void embargoedValidationSucceeds() {
        final var type = new AccessTypeWithSchemeUri()
                .id(TestConstants.CLOSED_ACCESS_TYPE_ID)
                .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI);

        final var access = new Access()
                .type(type)
                .accessStatement(new AccessStatement().statement("Embargoed"))
                .embargoExpiry(LocalDate.now());

        final List<ValidationFailure> failures = validationService.validate(access);

        assertThat(failures, empty());
    }


    @Test
    @DisplayName("Validation fails with missing accessStatement on closed raid")
    void missingAccessStatement() {
        final var type = new AccessTypeWithSchemeUri()
                .id(TestConstants.CLOSED_ACCESS_TYPE_ID)
                .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI);

        final var access = new Access()
                .type(type);

        final var failure = new ValidationFailure()
                .fieldId("access.accessStatement")
                .errorType("notSet")
                .message("field must be set");

        when(accessStatementValidationService.validate(null)).thenReturn(List.of(failure));

        final List<ValidationFailure> failures = validationService.validate(access);

        assertThat(failures.size(), is(1));
        assertThat(failures, hasItem(failure));
    }

    @Test
    @DisplayName("Validation fails with blank accessStatement on closed raid")
    void blankStatementClosed() {
        final var type = new AccessTypeWithSchemeUri()
                .id(TestConstants.CLOSED_ACCESS_TYPE_ID)
                .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI);

        final var accessStatement = new AccessStatement().statement("");

        final var access = new Access()
                .type(type)
                .accessStatement(accessStatement);

        final var failure = new ValidationFailure()
                .fieldId("access.accessStatement")
                .errorType("notSet")
                .message("field must be set");

        when(accessStatementValidationService.validate(accessStatement)).thenReturn(List.of(failure));

        final List<ValidationFailure> failures = validationService.validate(access);

        assertThat(failures.size(), is(1));
        assertThat(failures, hasItem(failure));
    }

    @Test
    @DisplayName("Validation fails with blank accessStatement on embargoed raid")
    void blankStatementEmbargoed() {
        final var type = new AccessTypeWithSchemeUri()
                .id(TestConstants.EMBARGOED_ACCESS_TYPE_ID)
                .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI);

        final var accessStatement = new AccessStatement().statement("");

        final var access = new Access()
                .type(type)
                .accessStatement(accessStatement)
                .embargoExpiry(LocalDate.now());

        final var failure = new ValidationFailure()
                .fieldId("access.accessStatement")
                .errorType("notSet")
                .message("field must be set");

        when(accessStatementValidationService.validate(accessStatement)).thenReturn(List.of(failure));

        final List<ValidationFailure> failures = validationService.validate(access);

        assertThat(failures.size(), is(1));
        assertThat(failures, hasItem(failure));
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
                .id(TestConstants.EMBARGOED_ACCESS_TYPE_ID)
                .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI);

        final var access = new Access()
                .type(type)
                .accessStatement(new AccessStatement().statement("access statement"));

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