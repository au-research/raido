package raido.apisvc.service.raid.validation;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.idl.raidv2.model.ContributorBlock;
import raido.idl.raidv2.model.ContributorIdentifierSchemeType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class ContributorValidationServiceTest {

  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private ContributorValidationService validationService;

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
    assertThat(failures, is(empty()));
  }

  @Test
  void addFailureIfContributorOrcidIsInvalid() {
    final var contributor = new ContributorBlock()
      .id("https://orcid.org/0000-00-000000-0001")
      .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

    final var failures = validationService.validateOrcidExists(1, contributor);
    final var failure = failures.get(0);

    assertThat(failure.getFieldId(), is("contributors[1].id"));
    assertThat(failure.getErrorType(), is("invalid"));
    assertThat(failure.getMessage(), is("ORCID should have the format https://orcid.org/0000-0000-0000-0000."));

    verifyNoInteractions(restTemplate);
  }

  @Test
  void addFailureIfContributorDoesNotExist() {
    final var contributor = new ContributorBlock()
      .id("https://orcid.org/0000-0000-0000-0001")
      .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
      .when(restTemplate).exchange(any(RequestEntity.class), eq(Void.class));

    final var failures = validationService.validateOrcidExists(1, contributor);
    final var failure = failures.get(0);

    assertThat(failure.getFieldId(), is("contributors[1].id"));
    assertThat(failure.getErrorType(), is("invalid"));
    assertThat(failure.getMessage(), is("The contributor does not exist."));
  }

  @Test
  void NoFailuresIfContributorExists() {
    final var contributor = new ContributorBlock()
      .id("https://orcid.org/0000-0000-0000-0001")
      .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

    final var failures = validationService.validateOrcidExists(1, contributor);

    assertThat(failures, is(empty()));
  }
}