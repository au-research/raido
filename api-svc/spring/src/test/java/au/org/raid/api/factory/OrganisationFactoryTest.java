package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganisationFactoryTest {
    @Mock
    private OrganisationRoleFactory roleFactory;

    @InjectMocks
    private OrganisationFactory organisationFactory;

    @Test
    @DisplayName("If OrganisationBlock is null return null")
    void returnsNull() {
        assertThat(organisationFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If OrganisationBlock has empty fields return empty fields")
    void returnsEmptyFields() {
        assertThat(organisationFactory.create(new OrganisationBlock()), is(new Organisation()));
    }

    @Test
    @DisplayName("All fields are set")
    void setsAllFields() {
        final var ror = "0000-0002-6492-9025";

        final var role1 = new OrganisationRole();
        final var role2 = new OrganisationRole();
        final var role3 = new OrganisationRoleWithSchemaUri();
        final var role4 = new OrganisationRoleWithSchemaUri();

        when(roleFactory.create(role1)).thenReturn(role3);
        when(roleFactory.create(role2)).thenReturn(role4);

        final var organisation = new OrganisationBlock()
                .id(ror)
                .identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_)
                .roles(List.of(role1, role2));

        final var result = organisationFactory.create(organisation);

        assertThat(result, is(new Organisation()
                .id(ror)
                .schemaUri("https://ror.org/")
                .roles(List.of(role3, role4))
        ));
    }
}