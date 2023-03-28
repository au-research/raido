package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.AlternateIdentifierBlock;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

class AlternateIdentifierValidationServiceTest {
  private final AlternateIdentifierValidationService validationService =
    new AlternateIdentifierValidationService();

  @Test
  void noFailuresIfAlternateIdentifiersIsNull() {
    final var failures = validationService.validateAlternateIdentifiers(null);
    assertThat(failures, is(empty()));
  }

  @Test
  void noFailuresIfAlternateIdentifiersIsEmptyList() {
    final var failures = validationService.validateAlternateIdentifiers(Collections.emptyList());
    assertThat(failures, is(empty()));
  }

  @Test
  void noFailuresIfAlternateIdentifierIsValid() {
    final var alternateIdentifier = "alternate-identifier";
    final var alternateIdentifierType = "alternate-identifier-type";

    final var failures = validationService.validateAlternateIdentifiers(List.of(
      new AlternateIdentifierBlock()
        .alternateIdentifier(alternateIdentifier)
        .alternateIdentifierType(alternateIdentifierType)
    ));

    assertThat(failures, is(empty()));
  }

  @Test
  void addsFailureIfAlternateIdentifierIsNull() {
    final var alternateIdentifierType = "alternate-identifier-type";

    final var failures = validationService.validateAlternateIdentifiers(List.of(
      new AlternateIdentifierBlock()
        .alternateIdentifierType(alternateIdentifierType)
    ));

    final var failure = failures.get(0);
    assertThat(failures.size(), is(1));
    assertThat(failure.getFieldId(), is("alternateIdentifiers[0].alternateIdentifier"));
    assertThat(failure.getErrorType(), is("required"));
    assertThat(failure.getMessage(), is("This is a required field."));
  }

  @Test
  void addsFailureIfAlternateIdentifierTypeIsNull() {
    final var alternateIdentifier = "alternate-identifier";

    final var failures = validationService.validateAlternateIdentifiers(List.of(
      new AlternateIdentifierBlock()
        .alternateIdentifier(alternateIdentifier)
    ));

    final var failure = failures.get(0);
    assertThat(failures.size(), is(1));
    assertThat(failure.getFieldId(), is("alternateIdentifiers[0].alternateIdentifierType"));
    assertThat(failure.getErrorType(), is("required"));
    assertThat(failure.getMessage(), is("This is a required field."));
  }

  @Test
  void addsFailureIfAllFieldsAreNull() {
    final var failures = validationService.validateAlternateIdentifiers(List.of(
      new AlternateIdentifierBlock()
    ));

    assertThat(failures.size(), is(2));
    assertThat(failures.get(0).getFieldId(), is("alternateIdentifiers[0].alternateIdentifier"));
    assertThat(failures.get(0).getErrorType(), is("required"));
    assertThat(failures.get(0).getMessage(), is("This is a required field."));
    assertThat(failures.get(1).getFieldId(), is("alternateIdentifiers[0].alternateIdentifierType"));
    assertThat(failures.get(1).getErrorType(), is("required"));
    assertThat(failures.get(1).getMessage(), is("This is a required field."));
  }
}