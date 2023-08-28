package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.TitleBlock;
import au.org.raid.idl.raidv2.model.TitleType;
import au.org.raid.idl.raidv2.model.TitleTypeWithSchemaUri;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TitleFactory {
    private static final String TITLE_TYPE_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/";
    private static final Map<TitleType, String> TITLE_TYPE_MAP = Map.of(
            TitleType.PRIMARY_TITLE, "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json",
            TitleType.ALTERNATIVE_TITLE, "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/alternative.json"
    );

    public Title create(final TitleBlock titleBlock) {
        if (titleBlock == null) {
            return null;
        }

        return new Title()
                .title(titleBlock.getTitle())
                .startDate(titleBlock.getStartDate())
                .endDate(titleBlock.getEndDate())
                .type(new TitleTypeWithSchemaUri()
                        .id(titleBlock.getType() != null ? TITLE_TYPE_MAP.get(titleBlock.getType()) : null)
                        .schemaUri(TITLE_TYPE_SCHEME_URI)
                );
    }
}