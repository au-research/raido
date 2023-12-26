package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.RaidContributorPositionRecord;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemaUri;
import org.springframework.stereotype.Component;

@Component
public class ContributorPositionFactory {
    public ContributorPositionWithSchemaUri create(final RaidContributorPositionRecord raidContributorPosition, final String uri, final String schemaUri) {
        return new ContributorPositionWithSchemaUri()
                .id(uri)
                .schemaUri(schemaUri)
                .startDate(raidContributorPosition.getStartDate())
                .endDate(raidContributorPosition.getEndDate());
    }
}