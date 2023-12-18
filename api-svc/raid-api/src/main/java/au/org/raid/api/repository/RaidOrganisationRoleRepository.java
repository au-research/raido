package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.RaidOrganisationRole;
import au.org.raid.db.jooq.tables.records.RaidOrganisationRoleRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidOrganisationRole.RAID_ORGANISATION_ROLE;

@Repository
@RequiredArgsConstructor
public class RaidOrganisationRoleRepository {
    private final DSLContext dslContext;

    public RaidOrganisationRoleRecord create(final RaidOrganisationRoleRecord record) {
        return dslContext.insertInto(RAID_ORGANISATION_ROLE)
                .set(RAID_ORGANISATION_ROLE.ORGANISATION_ROLE_ID, record.getOrganisationRoleId())
                .set(RAID_ORGANISATION_ROLE.RAID_ORGANISATION_ID, record.getRaidOrganisationId())
                .set(RAID_ORGANISATION_ROLE.START_DATE, record.getStartDate())
                .set(RAID_ORGANISATION_ROLE.END_DATE, record.getEndDate())
                .returning()
                .fetchOne();
    }

    public List<RaidOrganisationRoleRecord> findAllByRaidOrganisationId(final Integer raidOrganisationId) {
        return dslContext.selectFrom(RAID_ORGANISATION_ROLE)
                .where(RAID_ORGANISATION_ROLE.RAID_ORGANISATION_ID.eq(raidOrganisationId))
                .fetch();
    }
}
