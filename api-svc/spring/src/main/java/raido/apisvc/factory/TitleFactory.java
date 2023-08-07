package raido.apisvc.factory;

import org.springframework.stereotype.Component;
import raido.idl.raidv2.model.Title;
import raido.idl.raidv2.model.TitleBlock;
import raido.idl.raidv2.model.TitleType;
import raido.idl.raidv2.model.TitleTypeWithSchemeUri;

import java.util.Map;

@Component
public class TitleFactory {
    private static final String TITLE_TYPE_SCHEME_URI =
        "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";
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
            .type(new TitleTypeWithSchemeUri()
                .id(titleBlock.getType() != null ? TITLE_TYPE_MAP.get(titleBlock.getType()) : null)
                .schemeUri(TITLE_TYPE_SCHEME_URI)
            );
    }
}