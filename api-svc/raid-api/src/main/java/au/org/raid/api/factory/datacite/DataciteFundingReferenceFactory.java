package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteFundingReference;
import au.org.raid.idl.raidv2.model.Organisation;
import org.springframework.stereotype.Component;

@Component
public class DataciteFundingReferenceFactory {

    public DataciteFundingReference create(final Organisation organisation) {
        return new DataciteFundingReference()
                .setFunderName(organisation.getId())
                .setFunderIdentifier(organisation.getId())
                .setFunderIdentifierType("ROR")
                .setSchemeUri(organisation.getSchemaUri());
    }
}
