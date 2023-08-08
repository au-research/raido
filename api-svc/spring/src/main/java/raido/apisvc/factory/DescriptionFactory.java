package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Description;
import raido.idl.raidv2.model.DescriptionBlock;
import raido.idl.raidv2.model.DescriptionType;
import raido.idl.raidv2.model.DescriptionTypeWithSchemeUri;

import java.util.Map;

@Component
public class DescriptionFactory {
    private final String DESCRIPTION_TYPE_SCHEME_URI =
        "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1/";

    private final Map<DescriptionType, String> DESCRIPTION_TYPE_MAP = Map.of(
        DescriptionType.PRIMARY_DESCRIPTION, "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json",
        DescriptionType.ALTERNATIVE_DESCRIPTION, "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/alternative.json"
    );

    public Description create(final DescriptionBlock descriptionBlock) {
        if(descriptionBlock == null) {
            return null;
        }

        return new Description()
            .description(descriptionBlock.getDescription())
            .type(new DescriptionTypeWithSchemeUri()
                .id(descriptionBlock.getType() != null ? DESCRIPTION_TYPE_MAP.get(descriptionBlock.getType()) : null)
                .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
            );
    }
}