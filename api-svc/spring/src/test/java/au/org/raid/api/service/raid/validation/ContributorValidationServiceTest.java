package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.orcid.OrcidService;
import au.org.raid.idl.raidv2.model.ContributorBlock;
import au.org.raid.idl.raidv2.model.ContributorIdentifierSchemeType;
import au.org.raid.idl.raidv2.model.ValidationFailure;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContributorValidationServiceTest {

    @InjectMocks
    private ContributorValidationService validationService;

    @Mock
    private OrcidService orcidService;

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
    @DisplayName("Validation passes with X ORCID checksum value")
    void passesWithNonDigitChecksum() {
        final var contributor = new ContributorBlock()
                .id("https://orcid.org/0009-0001-8177-319X")
                .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

        final var failures = validationService.validateIdFields(1, contributor);
        assertThat(failures, is(empty()));
    }

    @Test
    @DisplayName("Adds failures from OrcidService")
    void addFailureIfContributorOrcidIsInvalid() {
        final var fieldId = "contributors[1].id";
        final var orcid = "https://orcid.org/0000-00-000000-0001";
        final var contributor = new ContributorBlock()
                .id(orcid)
                .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

        final var failure = new ValidationFailure()
                .fieldId(fieldId)
                .errorType("invalidValue")
                .message("Contributor ORCID should have the format https://orcid.org/0000-0000-0000-0000.");

        when(orcidService.validate(orcid, fieldId)).thenReturn(List.of(failure));

        final var failures = validationService.validateOrcidExists(1, contributor);

        assertThat(failures, is(List.of(failure)));
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