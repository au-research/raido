package raido.apisvc.service.raid.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.idl.raidv2.model.OrganisationBlock;
import raido.idl.raidv2.model.OrganisationIdentifierSchemeType;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class OrganisationValidationServiceTest {
  @Mock
  private RestTemplate restTemplate;

  @InjectMocks
  private OrganisationValidationService validationService;

  @Test
  void addFailureIfOrganisationDoesNotExist() {
    final var contributor = new OrganisationBlock()
      .id("https://ror.org/015w2mp89")
      .identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_);

    doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
      .when(restTemplate).exchange(any(RequestEntity.class), eq(Void.class));

    final var failures = validationService.validateRorExists(1, contributor);
    final var failure = failures.get(0);

    assertThat(failure.getFieldId(), is("organisations[1].id"));
    assertThat(failure.getErrorType(), is("invalidValue"));
    assertThat(failure.getMessage(), is("The organisation does not exist."));
  }

  @Test
  void NoFailuresIfOrganisationExists() {
    final var contributor = new OrganisationBlock()
      .id("https://ror.org/015w2mp89")
      .identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_);

    final var failures = validationService.validateRorExists(1, contributor);

    assertThat(failures, is(empty()));
  }
}