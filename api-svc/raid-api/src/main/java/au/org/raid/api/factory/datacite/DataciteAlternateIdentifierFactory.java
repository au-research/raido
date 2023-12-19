package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteAlternateIdentifier;
import au.org.raid.api.model.datacite.DataciteTitle;
import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import au.org.raid.idl.raidv2.model.Title;
import org.springframework.stereotype.Component;

@Component
public class DataciteAlternateIdentifierFactory {
    public DataciteAlternateIdentifier create(final AlternateIdentifier raidAlternateIdentifier){
        if (raidAlternateIdentifier == null) {
            return null;
        }

        DataciteAlternateIdentifier dataciteAlternateIdentifierResult;

        String title = (raidAlternateIdentifier.getId() != null) ? raidAlternateIdentifier.getId() : "";
        String type = (raidAlternateIdentifier.getType() != null) ? raidAlternateIdentifier.getType() : "";

        dataciteAlternateIdentifierResult = new DataciteAlternateIdentifier()
                .alternateIdentifier(title)
                .alternateIdentifierType(type);

        return dataciteAlternateIdentifierResult;

    }
}
