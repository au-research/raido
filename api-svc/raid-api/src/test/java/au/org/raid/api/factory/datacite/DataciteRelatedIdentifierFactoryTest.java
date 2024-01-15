package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteRelatedIdentifier;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import au.org.raid.idl.raidv2.model.RelatedObject;
import au.org.raid.idl.raidv2.model.RelatedObjectType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataciteRelatedIdentifierFactoryTest {

    private DataciteRelatedIdentifierFactory dataciteRelatedIdentifierFactory = new DataciteRelatedIdentifierFactory();

    @Test
    public void testCreateFromRelatedObject(){
        RelatedObject relatedObject1 = new RelatedObject();
        relatedObject1.setId("Related Object 1");

        RelatedObjectType relatedObjectType1 = new RelatedObjectType();
        relatedObjectType1.setId("output");
        relatedObject1.setType(relatedObjectType1);


        DataciteRelatedIdentifier dataciteRelatedIdentifier = dataciteRelatedIdentifierFactory.create(relatedObject1);

        assertEquals(dataciteRelatedIdentifier.getRelatedIdentifier(), "Related Object 1");
        assertEquals(dataciteRelatedIdentifier.getRelatedIdentifierType(), "HasPart");
    }

    @Test
    public void testCreateFromAlternateUrl(){
        AlternateUrl alternateUrl1 = new AlternateUrl();
        alternateUrl1.setUrl("Alternate URL 1");

        DataciteRelatedIdentifier dataciteRelatedIdentifier = dataciteRelatedIdentifierFactory.create(alternateUrl1);

        assertEquals(dataciteRelatedIdentifier.getRelatedIdentifier(), "Alternate URL 1");
        assertEquals(dataciteRelatedIdentifier.getRelatedIdentifierType(), "IsSourceOf");
    }

}
