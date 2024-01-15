package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteAlternateIdentifier;
import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DataciteAlternateIdentifierFactoryTest {

    private DataciteAlternateIdentifierFactory dataciteAlternateIdentifierFactory;

    @BeforeEach
    public void setUp() {
        dataciteAlternateIdentifierFactory = new DataciteAlternateIdentifierFactory();
    }

    @Test
    public void testCreateWithValidAlternateIdentifier() {
        AlternateIdentifier alternateIdentifier1 = new AlternateIdentifier();
        alternateIdentifier1.setId("Alternate Identifier 1");

        DataciteAlternateIdentifier dataciteAlternateIdentifier = dataciteAlternateIdentifierFactory.create(alternateIdentifier1);

        assertEquals("Alternate Identifier 1", dataciteAlternateIdentifier.getAlternateIdentifier());
    }

    @Test
    public void testCreateWithNullAlternateIdentifier() {
        DataciteAlternateIdentifier result = dataciteAlternateIdentifierFactory.create(null);

        assertNull(result);
    }
}
