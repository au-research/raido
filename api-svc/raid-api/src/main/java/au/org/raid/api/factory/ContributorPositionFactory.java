package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.ContributorPosition;
import au.org.raid.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemaUri;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class ContributorPositionFactory {
    private static final Map<ContributorPositionRaidMetadataSchemaType, String> POSITION_MAP =
            Map.of(ContributorPositionRaidMetadataSchemaType.LEADER,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json",
                    ContributorPositionRaidMetadataSchemaType.PRINCIPAL_INVESTIGATOR,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/principal-investigator.json",
                    ContributorPositionRaidMetadataSchemaType.CO_INVESTIGATOR,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/co-investigator.json",
                    ContributorPositionRaidMetadataSchemaType.OTHER_PARTICIPANT,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/other-participant.json",
                    ContributorPositionRaidMetadataSchemaType.CONTACT_PERSON,
                    "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/contact-person.json"
            );

    private static final String SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1/";

    public ContributorPositionWithSchemaUri create(final ContributorPosition position) {
        if (position == null) {
            return null;
        }

        var startDate = (position.getStartDate() != null) ?
                position.getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        var endDate = (position.getEndDate() != null) ?
                position.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null;

        return new ContributorPositionWithSchemaUri()
                .id(position.getPosition() != null ? POSITION_MAP.get(position.getPosition()) : null)
                .schemaUri(SCHEMA_URI)
                .startDate(startDate)
                .endDate(endDate);
    }
}