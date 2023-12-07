package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidContributorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidContributor.RAID_CONTRIBUTOR;

@Repository
@RequiredArgsConstructor
public class RaidContributorRepository {
    private final DSLContext dslContext;
    public RaidContributorRecord create(final RaidContributorRecord raidContributor) {
        return dslContext.insertInto(RAID_CONTRIBUTOR)
                .set(RAID_CONTRIBUTOR.RAID_NAME, raidContributor.getRaidName())
                .set(RAID_CONTRIBUTOR.CONTRIBUTOR_ID, raidContributor.getId())
                .set(RAID_CONTRIBUTOR.LEADER, raidContributor.getLeader())
                .set(RAID_CONTRIBUTOR.CONTACT, raidContributor.getContact())
                .onConflictDoNothing()
                .returning()
                .fetchOne();
    }
}
