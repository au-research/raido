package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.RelatedRaid;
import raido.idl.raidv2.model.RelatedRaidBlock;
import raido.idl.raidv2.model.RelatedRaidType;

@Component
public class RelatedRaidFactory {
    private static final String TYPE_SCHEME_URI =
        "https://github.com/au-research/raid-metadata/tree/main/scheme/related-raid/type/v1";

    public RelatedRaid create(final RelatedRaidBlock relatedRaidBlock) {
        if (relatedRaidBlock == null) {
            return null;
        }

        return new RelatedRaid()
            .id(relatedRaidBlock.getRelatedRaid())
            .type(new RelatedRaidType()
                .id(relatedRaidBlock.getRelatedRaidType())
                .schemeUri(TYPE_SCHEME_URI));
    }
}