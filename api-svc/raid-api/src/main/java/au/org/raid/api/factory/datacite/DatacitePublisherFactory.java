package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DatacitePublisher;
import au.org.raid.idl.raidv2.model.Owner;
import org.springframework.stereotype.Component;

@Component
public class DatacitePublisherFactory {
    private static final String PUBLISHER_IDENTIFIER_SCHEME = "ROR";
    public DatacitePublisher create(final Owner owner) {
        return new DatacitePublisher()
                .setName(owner.getId())
                .setPublisherIdentifier(owner.getId())
                .setPublisherIdentifierScheme(PUBLISHER_IDENTIFIER_SCHEME)
                .setSchemeUri(owner.getSchemaUri());
    }
}
