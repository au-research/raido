package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidContributorRecord;
import au.org.raid.idl.raidv2.model.Contributor;
import org.springframework.stereotype.Component;

@Component
public class RaidContributorRecordFactory {

    public RaidContributorRecord create(final Contributor contributor, final int contributorId, final String raidName) {
        return new RaidContributorRecord()
                .setContributorId(contributorId)
                .setContact(contributor.getContact())
                .setLeader(contributor.getLeader())
                .setRaidName(raidName);
    }
}
