package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidOrganisationRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidOrganisation.RAID_ORGANISATION;

@Repository
@RequiredArgsConstructor
public class RaidOrganisationRepository {
    private final DSLContext dslContext;

    public RaidOrganisationRecord create(final RaidOrganisationRecord record) {
        return dslContext.insertInto(RAID_ORGANISATION)
                .set(RAID_ORGANISATION.RAID_NAME, record.getRaidName())
                .set(RAID_ORGANISATION.ORGANISATION_ID, record.getOrganisationId())
                .returning()
                .fetchOne();
    }
}
