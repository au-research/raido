package au.org.raid.api.factory;

import au.org.raid.api.spring.config.IdentifierProperties;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.Id;
import au.org.raid.idl.raidv2.model.IdBlock;
import au.org.raid.idl.raidv2.model.Owner;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdFactoryTest {
    private static final String IDENTIFIER = "_identifier";
    private static final String IDENTIFIER_SCHEMA_URI = "identifier-scheme-uri";
    private static final String IDENTIFIER_REGISTRATION_AGENCY = "identifier-registration-agency";
    private static final String IDENTIFIER_OWNER = "identifier-owner";
    private static final Long IDENTIFIER_SERVICE_POINT = 999L;
    private static final String GLOBAL_URL = "global-url";
    private static final String RAID_AGENCY_URL = "raid-agency-url";
    private static final String LICENSE = "raid-license";
    private static final String GLOBAL_URL_PREFIX = "__global-url-prefix__";
    private static final String HANDLE_URL_PREFIX = "__handle-url-prefix__";
    private static final String NAME_PREFIX = "__name-prefix__";

    @Mock
    private IdentifierProperties identifierProperties;

    @InjectMocks
    private IdFactory idFactory;

    @Test
    @DisplayName("Sets all fields when creating from IdBlock")
    void createFromIdBlock() {
        final var idBlock = new IdBlock()
                .identifier(IDENTIFIER)
                .identifierSchemeURI(IDENTIFIER_SCHEMA_URI)
                .identifierRegistrationAgency(IDENTIFIER_REGISTRATION_AGENCY)
                .identifierOwner(IDENTIFIER_OWNER)
                .identifierServicePoint(IDENTIFIER_SERVICE_POINT)
                .globalUrl(GLOBAL_URL)
                .raidAgencyUrl(RAID_AGENCY_URL);

        final var result = idFactory.create(idBlock);

        assertThat(result.getId(), is(IDENTIFIER));
        assertThat(result.getSchemaUri(), is(IDENTIFIER_SCHEMA_URI));
        assertThat(result.getRegistrationAgency(), is(new RegistrationAgency()
                .id(IDENTIFIER_REGISTRATION_AGENCY)
                .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri())));
        assertThat(result.getOwner(), is(new Owner()
                .id(IDENTIFIER_OWNER)
                .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri())
                .servicePoint(IDENTIFIER_SERVICE_POINT)
        ));
        assertThat(result.getGlobalUrl(), is(GLOBAL_URL));
        assertThat(result.getRaidAgencyUrl(), is(RAID_AGENCY_URL));
    }

    @Test
    @DisplayName("Returns null if IdBlock is null")
    void returnsNull() {
        assertThat(idFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("Sets all fields when creating from IdentifierUrl and ServicePointRecord")
    void createFromIdentifierUrlAndServicePointRecord() {
        final var urlPrefix = "url-prefix";
        final var prefix = "_prefix_";
        final var suffix = "_suffix_:";
        final var handle = "%s/%s".formatted(prefix, suffix);
        final var servicePointRecord = new ServicePointRecord()
                .setIdentifierOwner(IDENTIFIER_OWNER)
                .setId(IDENTIFIER_SERVICE_POINT);
        final var rorSchemaUri = "https://ror.org/";

        when(identifierProperties.getRegistrationAgencyIdentifier()).thenReturn(IDENTIFIER_REGISTRATION_AGENCY);
        when(identifierProperties.getLicense()).thenReturn(LICENSE);
        when(identifierProperties.getGlobalUrlPrefix()).thenReturn(GLOBAL_URL_PREFIX);
        when(identifierProperties.getHandleUrlPrefix()).thenReturn(HANDLE_URL_PREFIX);
        when(identifierProperties.getNamePrefix()).thenReturn(NAME_PREFIX);

        final var id = idFactory.create(handle, servicePointRecord);

        assertThat(id, is(new Id()
                .id("%s%s/%s".formatted(NAME_PREFIX, prefix, suffix))
                .schemaUri("https://raid.org/")
                .registrationAgency(new RegistrationAgency()
                        .id(IDENTIFIER_REGISTRATION_AGENCY)
                        .schemaUri(rorSchemaUri))
                .owner(new Owner()
                        .id(IDENTIFIER_OWNER)
                        .schemaUri(rorSchemaUri)
                        .servicePoint(IDENTIFIER_SERVICE_POINT))
                .globalUrl("%s%s/%s".formatted(GLOBAL_URL_PREFIX, prefix, suffix))
                .raidAgencyUrl("%s%s/%s".formatted(HANDLE_URL_PREFIX, prefix, suffix))
                .license(LICENSE)
                .version(1)));
    }
}