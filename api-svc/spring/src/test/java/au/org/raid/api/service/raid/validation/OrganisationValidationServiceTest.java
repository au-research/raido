package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.ror.RorService;
import au.org.raid.idl.raidv2.model.OrganisationBlock;
import au.org.raid.idl.raidv2.model.OrganisationIdentifierSchemeType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class OrganisationValidationServiceTest {
  @Mock
  private RorService rorService;

  @InjectMocks
  private OrganisationValidationService validationService;

  @Test
  void addFailureIfOrganisationDoesNotExist() {
    final var contributor = new OrganisationBlock()
      .id("https://ror.org/015w2mp89")
      .identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_);

    doReturn(of("The ROR does not exist.")).
      when(rorService).validateRorExists(any());
    

    final var failures = validationService.validateRorExists(1, contributor);
    final var failure = failures.get(0);

    assertThat(failure.getFieldId(), is("organisations[1].id"));
    assertThat(failure.getErrorType(), is("invalidValue"));
    assertThat(failure.getMessage(), is("The organisation ROR does not exist."));
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