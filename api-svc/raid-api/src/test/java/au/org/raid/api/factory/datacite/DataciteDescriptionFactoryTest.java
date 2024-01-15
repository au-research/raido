package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteDescription;
import au.org.raid.idl.raidv2.model.Description;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteDescriptionFactoryTest {

    private DataciteDescriptionFactory dataciteDescriptionFactory = new DataciteDescriptionFactory();

    @Test
    public void testCreateDescription(){
        Description description1 = new Description();
        description1.setText("Description 1");

        DataciteDescription description = dataciteDescriptionFactory.create(description1);

        assertEquals(description.getDescription(), "Description 1");
        assertEquals(description.getDescriptionType(), "Abstract");
    }

}
