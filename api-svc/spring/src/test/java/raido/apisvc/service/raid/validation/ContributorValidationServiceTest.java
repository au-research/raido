package raido.apisvc.service.raid.validation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.ContributorBlock;
import raido.idl.raidv2.model.ContributorIdentifierSchemeType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContributorValidationServiceTest {

  final ContributorValidationService validationService = new ContributorValidationService();

  @Test
  void failsWithInvalidId() {
    final var contributor = new ContributorBlock()
      .id("0000-0000-0000-0001")
      .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

    final var failures = validationService.validateIdFields(1, contributor);

    final var failure = failures.get(0);

    assertThat(failure.getFieldId(), Matchers.is("contributors[1].id"));
    assertThat(failure.getErrorType(), Matchers.is("invalidValue"));
    assertThat(failure.getMessage(), Matchers.is("should start with https://orcid.org/"));
    assertThat(failures.size(), Matchers.is(1));
  }

  @Test
  void passesWithValidId() {
    final var contributor = new ContributorBlock()
      .id("https://orcid.org/0000-0000-0000-0001")
      .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

    final var failures = validationService.validateIdFields(1, contributor);
    assertTrue(failures.isEmpty());
  }
}