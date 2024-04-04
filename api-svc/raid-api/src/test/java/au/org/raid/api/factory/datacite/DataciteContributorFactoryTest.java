package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.api.util.SchemaValues;
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
    @DisplayName("Create organisation contributor with 'Lead Research Organisation' role")
    public void leadResearchOrganisation() {
        final var id = "_id";

        Organisation organisation = new Organisation()
                .id(id)
                .role(List.of(
                        new OrganisationRole().id(SchemaValues.FUNDER_ORGANISATION_ROLE.getUri()),
                        new OrganisationRole().id(SchemaValues.LEAD_RESEARCH_ORGANISATION_ROLE.getUri())
                ));

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation);

        assertEquals(id, dataciteContributor.getName());
        assertEquals("HostingInstitution", dataciteContributor.getContributorType());
    }

    @Test
    @DisplayName("Create organisation contributor with 'Other Research Organisation' role")
    public void otherResearchOrganisation() {
        final var id = "_id";

        Organisation organisation = new Organisation()
                .id(id)
                .role(List.of(
                        new OrganisationRole().id(SchemaValues.FUNDER_ORGANISATION_ROLE.getUri()),
                        new OrganisationRole().id(SchemaValues.OTHER_RESEARCH_ORGANISATION_ROLE.getUri())
                ));

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation);

        assertEquals(id, dataciteContributor.getName());
        assertEquals("Other", dataciteContributor.getContributorType());
    }
    @Test
    @DisplayName("Create organisation contributor with 'Partner' role")
    public void partnerOrganisation() {
        final var id = "_id";

        Organisation organisation = new Organisation()
                .id(id)
                .role(List.of(
                        new OrganisationRole().id(SchemaValues.FUNDER_ORGANISATION_ROLE.getUri()),
                        new OrganisationRole().id(SchemaValues.PARTNER_ORGANISATION_ROLE.getUri())
                ));

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation);

        assertEquals(id, dataciteContributor.getName());
        assertEquals("Other", dataciteContributor.getContributorType());
    }
    @Test
    @DisplayName("Create organisation contributor with 'Contractor' role")
    public void contractorOrganisation() {
        final var id = "_id";

        Organisation organisation = new Organisation()
                .id(id)
                .role(List.of(
                        new OrganisationRole().id(SchemaValues.FUNDER_ORGANISATION_ROLE.getUri()),
                        new OrganisationRole().id(SchemaValues.CONTRACTOR_ORGANISATION_ROLE.getUri())
                ));

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation);

        assertEquals(id, dataciteContributor.getName());
        assertEquals("Other", dataciteContributor.getContributorType());
    }

    @Test
    @DisplayName("Create organisation contributor with 'Facility' role")
    public void facilityOrganisation() {
        final var id = "_id";

        Organisation organisation = new Organisation()
                .id(id)
                .role(List.of(
                        new OrganisationRole().id(SchemaValues.FUNDER_ORGANISATION_ROLE.getUri()),
                        new OrganisationRole().id(SchemaValues.FACILITY_RESEARCH_ORGANISATION_ROLE.getUri())
                ));

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation);

        assertEquals(id, dataciteContributor.getName());
        assertEquals("Sponsor", dataciteContributor.getContributorType());
    }

    @Test
    @DisplayName("Create organisation contributor with 'Other Organisation' role")
    public void otherOrganisation() {
        final var id = "_id";

        Organisation organisation = new Organisation()
                .id(id)
                .role(List.of(
                        new OrganisationRole().id(SchemaValues.FUNDER_ORGANISATION_ROLE.getUri()),
                        new OrganisationRole().id(SchemaValues.OTHER_ORGANISATION_ROLE.getUri())
                ));

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation);

        assertEquals(id, dataciteContributor.getName());
        assertEquals("Other", dataciteContributor.getContributorType());
    }
}
