package au.org.raid.api.service.raid;

import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.db.jooq.api_svc.tables.records.ServicePointRecord;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class MetadataServiceTest {
    private final MetadataProps metadataProps = new MetadataProps();

    private final MetadataService metadataService =
            new MetadataService(metadataProps);

    @Test
    void createIdBlock() {
        final var servicePointId = 999L;
        final var identifierOwner = "identifier-owner";
        final var identifierRegistrationAgency = "registration-agency";
        final var id = new IdentifierUrl("https://unittest.com",
                new IdentifierHandle("prefix", "suffix"));
        final var identifierSchemeUri = "https://raid.org";
        final var globalUrlPrefix = "globalUrlPrefix";

        metadataProps.identifierRegistrationAgency = identifierRegistrationAgency;
        metadataProps.globalUrlPrefix = globalUrlPrefix;
        metadataProps.handleUrlPrefix = "raid-url";

        final var servicePointRecord = new ServicePointRecord()
                .setId(servicePointId)
                .setIdentifierOwner(identifierOwner);

        final var idBlock = metadataService.createIdBlock(id, servicePointRecord);

        assertThat(idBlock.getIdentifier(), is(id.formatUrl()));
        assertThat(idBlock.getIdentifierSchemeURI(), is(identifierSchemeUri));
        assertThat(idBlock.getIdentifierServicePoint(), is(servicePointId));
        assertThat(idBlock.getIdentifierOwner(), is(identifierOwner));
        assertThat(idBlock.getIdentifierRegistrationAgency(), is(identifierRegistrationAgency));

        assertThat(idBlock.getGlobalUrl(), is("globalUrlPrefix/prefix/suffix"));
        assertThat(idBlock.getRaidAgencyUrl(), is("raid-url/prefix/suffix"));

    }
}