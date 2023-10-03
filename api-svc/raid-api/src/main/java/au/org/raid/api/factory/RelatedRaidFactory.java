package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.RelatedRaid;
import au.org.raid.idl.raidv2.model.RelatedRaidBlock;
import au.org.raid.idl.raidv2.model.RelatedRaidType;
import org.springframework.stereotype.Component;

@Component
public class RelatedRaidFactory {
    private static final String TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/related-raid/type/v1/";

    public RelatedRaid create(final RelatedRaidBlock relatedRaidBlock) {
        if (relatedRaidBlock == null) {
            return null;
        }

        return new RelatedRaid()
                .id(relatedRaidBlock.getRelatedRaid())
                .type(new RelatedRaidType()
                        .id(relatedRaidBlock.getRelatedRaidType())
                        .schemaUri(TYPE_SCHEMA_URI));
    }
}