package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteContributor;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.TitleTypeWithSchemaUri;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteContributorFactoryTest {

    private DataciteContributorFactory dataciteContributorFactory = new DataciteContributorFactory();

    @Test
    public void testCreateContributorFromContributor(){
        Contributor contributor1 = new Contributor();
        contributor1.setId("contributor1");

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(contributor1);

        assertEquals(dataciteContributor.getContributor(), "Name for contributor1");
        assertEquals(dataciteContributor.getContributorType(), "Researcher");
    }

    @Test
    public void testCreateContributorFromOrganisation(){
        Organisation organisation1 = new Organisation();
        organisation1.setId("organisation1");

        DataciteContributor dataciteContributor = dataciteContributorFactory.create(organisation1);

        assertEquals(dataciteContributor.getContributor(), "Name for organisation1");
        assertEquals(dataciteContributor.getContributorType(), "ResearchGroup");
    }

}
