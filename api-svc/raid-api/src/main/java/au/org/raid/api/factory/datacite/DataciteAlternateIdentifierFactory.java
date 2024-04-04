package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteAlternateIdentifier;
import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.springframework.stereotype.Component;

@Component
public class DataciteAlternateIdentifierFactory {
    public DataciteAlternateIdentifier create(final AlternateIdentifier alternateIdentifier) {
        return new DataciteAlternateIdentifier()
                .setAlternateIdentifier(alternateIdentifier.getId())
                .setAlternateIdentifierType("URL");
    }
}
