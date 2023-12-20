package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteCreator;
import au.org.raid.idl.raidv2.model.RegistrationAgency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DataciteCreatorFactoryTest {

    private final DataciteCreatorFactory dataciteCreatorFactory = new DataciteCreatorFactory();

    @Test
    public void testCreateWithNonNullRegistrationAgency() {
        RegistrationAgency registrationAgency = new RegistrationAgency();
        registrationAgency.setId("test-id");

        DataciteCreator result = dataciteCreatorFactory.create(registrationAgency);

        assertEquals("test-id", result.getDataciteCreator(), "DataciteCreator should be created with the correct ID");
    }

    @Test
    public void testCreateWithNullRegistrationAgency() {
        DataciteCreator result = dataciteCreatorFactory.create(null);

        assertNull(result, "DataciteCreator should be null when RegistrationAgency is null");
    }
}
