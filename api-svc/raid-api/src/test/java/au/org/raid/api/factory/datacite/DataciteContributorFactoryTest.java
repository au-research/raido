package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.OrganisationRole;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteContributorFactoryTest {

    private DataciteContributorFactory dataciteContributorFactory = new DataciteContributorFactory();

    @Test
    @DisplayName("Create with registration agency")
    void createWithRegistrationAgency() {
        final var id = "_id";
        final var schemaUri = "schema-uri";

        final var registrationAgency = new RegistrationAgency()
                .id(id)
                .schemaUri(schemaUri);

        final var result = dataciteContributorFactory.create(registrationAgency);

        assertThat(result.getContributorType(), is("RegistrationAgency"));
        assertThat(result.getName(), is("RAiD AU"));
        assertThat(result.getNameType(), is("Organizational"));
        assertThat(result.getNameIdentifiers().get(0).getNameIdentifier(), is(id));
        assertThat(result.getNameIdentifiers().get(0).getNameIdentifierScheme(), is("ROR"));
        assertThat(result.getNameIdentifiers().get(0).getSchemeUri(), is(schemaUri));
    }

    @Test
    public void testCreateWithOrganisation() {
        Organisation organisation = new Organisation()
                .id("Organisation 1")
                .role(List.of(new OrganisationRole().id("https://vocabulary.raid.org/organisation.role.schema/182")));

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation);

        assertEquals("Organisation 1", dataciteContributor.getName());
        assertEquals("HostingInstitution", dataciteContributor.getContributorType());
    }
}
