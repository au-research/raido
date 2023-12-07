package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidContributorPositionRecord;
import au.org.raid.idl.raidv2.model.ContributorPositionWithSchemaUri;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RaidContributorPositionRecordFactory {
    public RaidContributorPositionRecord create(
            final ContributorPositionWithSchemaUri position,
            final int raidContributorId,
            final int contributorPositionId) {
        return new RaidContributorPositionRecord()
                .setContributorPositionId(contributorPositionId)
                .setRaidContributorId(raidContributorId)
                .setStartDate(position.getStartDate())
                .setEndDate(position.getEndDate());
    }
}
