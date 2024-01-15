package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.Organisation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteContributorFactoryTest {

    private DataciteContributorFactory dataciteContributorFactory;

    @BeforeEach
    public void setUp() {
        dataciteContributorFactory = new DataciteContributorFactory();
    }

    @Test
    public void testCreateWithContributor() {
        Contributor contributor1 = new Contributor();
        contributor1.setId("Contributor 1");

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(contributor1);

        assertEquals("Name for Contributor 1", dataciteContributor.getContributor());
        assertEquals("Researcher", dataciteContributor.getContributorType());
    }

    @Test
    public void testCreateWithOrganisation() {
        Organisation organisation1 = new Organisation();
        organisation1.setId("Organisation 1");

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation1);

        assertEquals("Name for Organisation 1", dataciteContributor.getContributor());
        assertEquals("ResearchGroup", dataciteContributor.getContributorType());
    }
}
