package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidContributorPositionRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidContributorPosition.RAID_CONTRIBUTOR_POSITION;

@Repository
@RequiredArgsConstructor
public class RaidContributorPositionRepository {
    private final DSLContext dslContext;
    public RaidContributorPositionRecord create(final RaidContributorPositionRecord contributorPosition) {
        return dslContext.insertInto(RAID_CONTRIBUTOR_POSITION)
                .set(RAID_CONTRIBUTOR_POSITION.RAID_CONTRIBUTOR_ID, contributorPosition.getRaidContributorId())
                .set(RAID_CONTRIBUTOR_POSITION.CONTRIBUTOR_POSITION_ID, contributorPosition.getContributorPositionId())
                .set(RAID_CONTRIBUTOR_POSITION.START_DATE, contributorPosition.getStartDate())
                .set(RAID_CONTRIBUTOR_POSITION.END_DATE, contributorPosition.getEndDate())
                .returning()
                .fetchOne();
    }

    public List<RaidContributorPositionRecord> findAllByRaidContributorId(final Integer raidContributorId) {
        return dslContext.selectFrom(RAID_CONTRIBUTOR_POSITION)
                .where(RAID_CONTRIBUTOR_POSITION.RAID_CONTRIBUTOR_ID.eq(raidContributorId))
                .fetch();
    }
}
