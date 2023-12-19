package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteRelatedIdentifier;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import au.org.raid.idl.raidv2.model.RelatedObject;
import au.org.raid.idl.raidv2.model.RelatedRaid;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteRelatedIdentifierFactory {
    public DataciteRelatedIdentifier create(final RelatedObject raidRelatedObject){
        if (raidRelatedObject == null) {
            return null;
        }

        DataciteRelatedIdentifier dataciteRelatedIdentifierResult;

        String relatedIdentifier = (raidRelatedObject.getId() != null) ? raidRelatedObject.getId() : "";
        String relatedIdentifierType = (raidRelatedObject.getType() != null) ? raidRelatedObject.getType().getId() : "";


        dataciteRelatedIdentifierResult = new DataciteRelatedIdentifier().relatedIdentifier(relatedIdentifier);

        if(relatedIdentifierType == "output") {
            dataciteRelatedIdentifierResult.relatedIdentifierType("HasPart");
        }

        return dataciteRelatedIdentifierResult;

    }

    public DataciteRelatedIdentifier create(final AlternateUrl alternateUrl){
        if (alternateUrl == null) {
            return null;
        }

        DataciteRelatedIdentifier dataciteRelatedIdentifierResult;

        String relatedIdentifier = (alternateUrl.getUrl() != null) ? alternateUrl.getUrl() : "";

        dataciteRelatedIdentifierResult = new DataciteRelatedIdentifier().relatedIdentifier(relatedIdentifier);
        dataciteRelatedIdentifierResult.relatedIdentifierType("IsSourceOf");

        return dataciteRelatedIdentifierResult;

    }

    public DataciteRelatedIdentifier create(final RelatedRaid relatedRaid){
        if (relatedRaid == null) {
            return null;
        }

        DataciteRelatedIdentifier dataciteRelatedIdentifierResult;

        String relatedIdentifier = (relatedRaid.getId() != null) ? relatedRaid.getId() : "";

        dataciteRelatedIdentifierResult = new DataciteRelatedIdentifier().relatedIdentifier(relatedIdentifier);
        dataciteRelatedIdentifierResult.relatedIdentifierType("HasPart");

        return dataciteRelatedIdentifierResult;

    }
}
