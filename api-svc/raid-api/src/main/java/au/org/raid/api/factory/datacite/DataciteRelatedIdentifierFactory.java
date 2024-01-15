package au.org.raid.api.factory.datacite;

import au.org.raid.api.model.datacite.DataciteRelatedIdentifier;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import au.org.raid.idl.raidv2.model.RelatedObject;
import au.org.raid.idl.raidv2.model.RelatedRaid;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataciteRelatedIdentifierFactory {

    private static final String HAS_PART = "HasPart";
    private static final String IS_SOURCE_OF = "IsSourceOf";

    public DataciteRelatedIdentifier create(final RelatedObject raidRelatedObject) {
        return Optional.ofNullable(raidRelatedObject)
                .map(obj -> new DataciteRelatedIdentifier()
                        .setRelatedIdentifier(Optional.ofNullable(obj.getId()).orElse(""))
                        .setRelatedIdentifierType(obj.getType() != null && "output".equals(obj.getType().getId()) ? HAS_PART : null))
                .orElse(null);
    }

    public DataciteRelatedIdentifier create(final AlternateUrl alternateUrl) {
        return Optional.ofNullable(alternateUrl)
                .map(url -> new DataciteRelatedIdentifier()
                        .setRelatedIdentifier(Optional.ofNullable(url.getUrl()).orElse(""))
                        .setRelatedIdentifierType(IS_SOURCE_OF))
                .orElse(null);
    }

    public DataciteRelatedIdentifier create(final RelatedRaid relatedRaid) {
        return Optional.ofNullable(relatedRaid)
                .map(raid -> new DataciteRelatedIdentifier()
                        .setRelatedIdentifier(Optional.ofNullable(raid.getId()).orElse(""))
                        .setRelationType(HAS_PART))

                .orElse(null);
    }
}
