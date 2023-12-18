package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidContributorRoleRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidContributorRole.RAID_CONTRIBUTOR_ROLE;

@Repository
@RequiredArgsConstructor
public class RaidContributorRoleRepository {
    private final DSLContext dslContext;
    public RaidContributorRoleRecord create(final RaidContributorRoleRecord contributorRole) {
        return dslContext.insertInto(RAID_CONTRIBUTOR_ROLE)
                .set(RAID_CONTRIBUTOR_ROLE.RAID_CONTRIBUTOR_ID, contributorRole.getRaidContributorId())
                .set(RAID_CONTRIBUTOR_ROLE.CONTRIBUTOR_ROLE_ID, contributorRole.getContributorRoleId())
                .returning()
                .fetchOne();
    }

    public List<RaidContributorRoleRecord> findAllByRaidContributorId(final Integer raidContributorId) {
        return dslContext.selectFrom(RAID_CONTRIBUTOR_ROLE)
                .where(RAID_CONTRIBUTOR_ROLE.RAID_CONTRIBUTOR_ID.eq(raidContributorId))
                .fetch();
    }
}