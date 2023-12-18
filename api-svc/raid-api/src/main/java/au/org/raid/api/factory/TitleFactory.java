package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.RaidTitleRecord;
import au.org.raid.idl.raidv2.model.*;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class TitleFactory {
    private static final String TITLE_TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1/";
    private static final Map<TitleType, String> TITLE_TYPE_MAP = Map.of(
            TitleType.PRIMARY_TITLE, "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json",
            TitleType.ALTERNATIVE_TITLE, "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/alternative.json"
    );

    public Title create(final TitleBlock titleBlock) {
        if (titleBlock == null) {
            return null;
        }

        var startDate = (titleBlock.getStartDate() != null) ?
                titleBlock.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        final var endDate = (titleBlock.getEndDate() != null) ?
                titleBlock.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        return new Title()
                .text(titleBlock.getTitle())
                .startDate(startDate)
                .endDate(endDate)
                .type(new TitleTypeWithSchemaUri()
                        .id(titleBlock.getType() != null ? TITLE_TYPE_MAP.get(titleBlock.getType()) : null)
                        .schemaUri(TITLE_TYPE_SCHEMA_URI)
                );
    }

    public Title create(final RaidTitleRecord record, final String typeId, final String typeSchemaUri, final Language language) {
        return new Title()
                .text(record.getText())
                .startDate(record.getStartDate())
                .endDate(record.getEndDate())
                .type(new TitleTypeWithSchemaUri()
                        .id(typeId)
                        .schemaUri(typeSchemaUri)

                )
                .language(language);
    }
}