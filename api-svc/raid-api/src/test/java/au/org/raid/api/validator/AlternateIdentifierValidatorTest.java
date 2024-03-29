package au.org.raid.api.validator;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class AlternateIdentifierValidatorTest {
    private final AlternateIdentifierValidator validationService =
            new AlternateIdentifierValidator();

    @Test
    @DisplayName("Validation passes if alternateIdentifier is null")
    void noFailuresIfAlternateIdentifierIsNull() {
        final var failures = validationService.validateAlternateIdentifier(null);
        assertThat(failures, is(empty()));
    }

    @Test
    @DisplayName("Validation passes if alternateIdentifier is empty list")
    void noFailuresIfAlternateIdentifierIsEmptyList() {
        final var failures = validationService.validateAlternateIdentifier(Collections.emptyList());
        assertThat(failures, is(empty()));
    }

    @Test
    @DisplayName("Validation passes with valid alternate identifiers")
    void noFailuresIfAlternateIdentifierIsValid() {
        final var id = "alternate-identifier";
        final var type = "alternate-identifier-type";

        final var failures = validationService.validateAlternateIdentifier(List.of(
                new AlternateIdentifier()
                        .id(id)
                        .type(type)
        ));

        assertThat(failures, is(empty()));
    }

    @Test
    @DisplayName("Validation fails if id is null")
    void addsFailureIfIdIsNull() {
        final var type = "alternate-identifier-type";

        final var failures = validationService.validateAlternateIdentifier(List.of(
                new AlternateIdentifier()
                        .type(type)
        ));

        final var failure = failures.get(0);
        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("alternateIdentifier[0].id")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails if type is null")
    void addsFailureIfAlternateIdentifierTypeIsNull() {
        final var alternateIdentifier = "alternate-identifier";

        final var failures = validationService.validateAlternateIdentifier(List.of(
                new AlternateIdentifier()
                        .id(alternateIdentifier)
        ));

        assertThat(failures, hasSize(1));
        assertThat(failures, hasItem(
                new ValidationFailure()
                        .fieldId("alternateIdentifier[0].type")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }

    @Test
    @DisplayName("Validation fails and all failures are returned if id and type are null")
    void addsFailuresIfAllFieldsAreNull() {
        final var failures = validationService.validateAlternateIdentifier(List.of(
                new AlternateIdentifier()
        ));

        assertThat(failures, hasSize(2));
        assertThat(failures, hasItems(
                new ValidationFailure()
                        .fieldId("alternateIdentifier[0].id")
                        .errorType("notSet")
                        .message("field must be set"),
                new ValidationFailure()
                        .fieldId("alternateIdentifier[0].type")
                        .errorType("notSet")
                        .message("field must be set")
        ));
    }
}