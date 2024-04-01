package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteCreator;
import au.org.raid.api.model.datacite.NameIdentifier;
import au.org.raid.idl.raidv2.model.Contributor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DataciteCreatorFactory {

    private static final Map<String, String> NAME_IDENTIFIER_SCHEMA_MAP = Map.of(
            "https://orcid.org/", "ORCID",
            "https://isni.org/", "ISNI"
    );
    public DataciteCreator create(final Contributor contributor) {
        return new DataciteCreator()
                .setName(contributor.getId())
                .setNameType("Personal")
                .setNameIdentifiers(List.of(
                        new NameIdentifier()
                                .setNameIdentifier(contributor.getId())
                                .setSchemeUri(contributor.getSchemaUri())
                                .setNameIdentifierScheme(NAME_IDENTIFIER_SCHEMA_MAP.get(contributor.getSchemaUri()))
                ));
    }
}
