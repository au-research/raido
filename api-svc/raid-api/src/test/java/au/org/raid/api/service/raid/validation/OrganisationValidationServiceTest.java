package au.org.raid.api.service.raid.validation;

import au.org.raid.api.service.ror.RorService;
import au.org.raid.idl.raidv2.model.OrganisationBlock;
import au.org.raid.idl.raidv2.model.OrganisationIdentifierSchemeType;
import au.org.raid.idl.raidv2.model.ValidationFailure;
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
class OrganisationValidationServiceTest {
    @Mock
    private RorService rorService;

    @InjectMocks
    private OrganisationValidationService validationService;

    @Test
    void addFailureIfOrganisationDoesNotExist() {
        final var fieldId = "organisations[1].id";
        final var ror = "https://ror.org/015w2mp89";
        final var contributor = new OrganisationBlock()
                .id(ror)
                .identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_);

        final var failure = new ValidationFailure()
                .fieldId(fieldId)
                .errorType("invalidValue")
                .message("uri not found");
        when(rorService.validate(ror, fieldId)).thenReturn(List.of(failure));

        final var failures = validationService.validateRorExists(1, contributor);
        assertThat(failures, is(List.of(failure)));
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