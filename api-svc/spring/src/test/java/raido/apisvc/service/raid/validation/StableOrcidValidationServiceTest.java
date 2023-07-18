package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.service.orcid.OrcidService;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StableOrcidValidationServiceTest {
  @Mock
  private OrcidService orcidService;

  @InjectMocks
  private StableOrcidValidationService validationService;

  @Test
  @DisplayName("Fails validation if orcid is null")
  void nullOrcid() {
    final var failures = validationService.validate(null, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(orcidService);
  }

  @Test
  @DisplayName("Fails validation if orcid is empty string")
  void emptyString() {
    final var failures = validationService.validate("", 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("notSet")
        .message("field must be set")
    ));
    verifyNoInteractions(orcidService);
  }

  @Test
  @DisplayName("Fails validation with incorrect host")
  void incorrectHost() {
    final var orcid = "http://example.org";

    final var failures = validationService.validate(orcid, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("invalidValue")
        .message("should start with https://orcid.org/")
    ));
    verifyNoInteractions(orcidService);
  }

  @Test
  @DisplayName("Fails validation if orcid is too short")
  void tooShort() {
    final var orcid = "https://orcid.org/";

    final var failures = validationService.validate(orcid, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("invalidValue")
        .message("too short")
    ));
    verifyNoInteractions(orcidService);
  }

  @Test
  @DisplayName("Fails validation if orcid is too long")
  void tooLong() {
    final var orcid = "https://orcid.org/0000-0000-0000-00001";

    final var failures = validationService.validate(orcid, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("invalidValue")
        .message("too long")
    ));
    verifyNoInteractions(orcidService);
  }

  @Test
  @DisplayName("Fails validation if checksum digit is incorrect")
  void invalidChecksum() {
    final var orcid = "https://orcid.org/0000-0000-0000-0000";

    final var failures = validationService.validate(orcid, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("invalidValue")
        .message("failed checksum, last digit should be `1`")
    ));
    verifyNoInteractions(orcidService);
  }

  @Test
  @DisplayName("Fails validation if pattern is incorrect")
  void invalidPattern() {
    final var orcid = "https://orcid.org/0000-0c00-0000-0000";

    final var failures = validationService.validate(orcid, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("invalidValue")
        .message("Contributor ORCID should have the format https://orcid.org/0000-0000-0000-0000")
    ));
    verifyNoInteractions(orcidService);
  }

  @Test
  @DisplayName("Fails validation if orcid does not exist")
  void orcidDoesNotExist() {
    final var orcid = "https://orcid.org/0000-0000-0000-0001";

    when(orcidService.validateOrcidExists(orcid)).thenReturn(List.of("NOT FOUND"));

    final var failures = validationService.validate(orcid, 3);

    assertThat(failures, hasSize(1));
    assertThat(failures, hasItem(
      new ValidationFailure()
        .fieldId("contributors[3].id")
        .errorType("invalidValue")
        .message("The contributor ORCID does not exist")
    ));
  }

  @Test
  @DisplayName("Valid Orcid passes validation")
  void passes() {
    final var orcid = "https://orcid.org/0000-0000-0000-0001";

    when(orcidService.validateOrcidExists(orcid)).thenReturn(Collections.emptyList());

    final var failures = validationService.validate(orcid, 3);

    assertThat(failures, empty());
  }
}