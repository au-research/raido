package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidContributorRoleRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class RaidContributorRoleRecordFactory {
    public RaidContributorRoleRecord create(final int raidContributorId, final int contributorRoleId) {
        return new RaidContributorRoleRecord()
                .setRaidContributorId(raidContributorId)
                .setContributorRoleId(contributorRoleId);
    }
}
