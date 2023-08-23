package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.ror.RorService;
import au.org.raid.api.util.TestConstants;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StableRorValidationServiceTest {

    @Mock
    private RorService rorService;

    @InjectMocks
    private StableRorValidationService validationService;

    @Test
    @DisplayName("Validation passes with correct ROR")
    void validRor() {
        final var failures = validationService.validate(TestConstants.VALID_ROR, 0);

        assertThat(failures, empty());
    }

    @Test
    @DisplayName("Validation fails if ROR is null")
    void nullRor() {
        final var failures = validationService.validate(null, 0);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[0].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if ROR is empty string")
    void emptyRor() {
        final var failures = validationService.validate("", 0);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[0].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if ROR does not match correct pattern")
    void rorMatchesPattern() {
        final var invalidRor = "https://ror.org/038sjwqxx";
        final var failures = validationService.validate(invalidRor, 0);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[0].id")
                        .errorType("invalidValue")
                        .message("has invalid/unsupported value")
        ));
    }

    @Test
    @DisplayName("Validation fails if ROR does not exist")
    void rorDoesNotExist() {
        when(rorService.validateRorExists(TestConstants.VALID_ROR)).thenReturn(List.of(""));

        final var failures = validationService.validate(TestConstants.VALID_ROR, 0);

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("organisations[0].id")
                        .errorType("invalidValue")
                        .message("The organisation ROR does not exist")
        ));
    }

}