package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidContributorRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidContributor.RAID_CONTRIBUTOR;

@Repository
@RequiredArgsConstructor
public class RaidContributorRepository {
    private final DSLContext dslContext;
    public RaidContributorRecord create(final RaidContributorRecord raidContributor) {
        return dslContext.insertInto(RAID_CONTRIBUTOR)
                .set(RAID_CONTRIBUTOR.HANDLE, raidContributor.getHandle())
                .set(RAID_CONTRIBUTOR.CONTRIBUTOR_ID, raidContributor.getContributorId())
                .set(RAID_CONTRIBUTOR.LEADER, raidContributor.getLeader())
                .set(RAID_CONTRIBUTOR.CONTACT, raidContributor.getContact())
                .set(RAID_CONTRIBUTOR.STATUS, raidContributor.getStatus())
                .returning()
                .fetchOne();
    }

    public List<RaidContributorRecord> findAllByHandle(final String handle) {
        return dslContext.selectFrom(RAID_CONTRIBUTOR)
                .where(RAID_CONTRIBUTOR.HANDLE.eq(handle))
                .fetch();
    }

    public void deleteAllByHandle(final String handle) {
        dslContext.deleteFrom(RAID_CONTRIBUTOR)
                .where(RAID_CONTRIBUTOR.HANDLE.eq(handle))
                .execute();
    }
}
