package au.org.raid.api.factory;

import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.idl.raidv2.model.IdBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class IdFactoryTest {

    @Mock
    private MetadataProps metadataProps;

    @InjectMocks
    private IdFactory idFactory;

    @Test
    @DisplayName("Sets all fields")
    void create() {
        final var identifier = "_identifier";
        final var identifierSchemeUri = "identifier-scheme-uri";
        final var identifierRegistrationAgency = "identifier-registration-agency";
        final var identifierOwner = "identifier-owner";
        final var identifierServicePoint = 999L;
        final var globalUrl = "global-url";
        final var raidAgencyUrl = "raid-agency-url";

        final var idBlock = new IdBlock()
                .identifier(identifier)
                .identifierSchemeURI(identifierSchemeUri)
                .identifierRegistrationAgency(identifierRegistrationAgency)
                .identifierOwner(identifierOwner)
                .identifierServicePoint(identifierServicePoint)
                .globalUrl(globalUrl)
                .raidAgencyUrl(raidAgencyUrl);

        final var result = idFactory.create(idBlock);

        assertThat(result.getIdentifier(), is(identifier));
        assertThat(result.getIdentifierSchemeUri(), is(identifierSchemeUri));
        assertThat(result.getIdentifierRegistrationAgency(), is(identifierRegistrationAgency));
        assertThat(result.getIdentifierOwner(), is(identifierOwner));
        assertThat(result.getIdentifierServicePoint(), is(identifierServicePoint));
        assertThat(result.getGlobalUrl(), is(globalUrl));
        assertThat(result.getRaidAgencyUrl(), is(raidAgencyUrl));
    }

    @Test
    @DisplayName("Returns null if IdBlock is null")
    void returnsNull() {
        assertThat(idFactory.create(null), nullValue());
    }
}