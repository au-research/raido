package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.OrganisationRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteContributorFactoryTest {

    private DataciteContributorFactory dataciteContributorFactory;

    @BeforeEach
    public void setUp() {
        dataciteContributorFactory = new DataciteContributorFactory();
    }

//    @Test
//    public void testCreateWithContributor() {
//        Contributor contributor1 = new Contributor();
//        contributor1.setId("Contributor 1");
//
//        DataciteContributor dataciteContributor = dataciteContributorFactory.create(contributor1);
//
//        assertEquals("Name for Contributor 1", dataciteContributor.getName());
//        assertEquals("Researcher", dataciteContributor.getContributorType());
//    }

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
