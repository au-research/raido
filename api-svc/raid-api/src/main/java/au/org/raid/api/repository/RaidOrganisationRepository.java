package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidOrganisationRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidOrganisation.RAID_ORGANISATION;

@Repository
@RequiredArgsConstructor
public class RaidOrganisationRepository {
    private final DSLContext dslContext;

    public RaidOrganisationRecord create(final RaidOrganisationRecord record) {
        return dslContext.insertInto(RAID_ORGANISATION)
                .set(RAID_ORGANISATION.HANDLE, record.getHandle())
                .set(RAID_ORGANISATION.ORGANISATION_ID, record.getOrganisationId())
                .returning()
                .fetchOne();
    }

    public List<RaidOrganisationRecord> findAllByHandle(final String handle) {
        return dslContext.selectFrom(RAID_ORGANISATION)
                .where(RAID_ORGANISATION.HANDLE.eq(handle))
                .fetch();
    }

    public void deleteAllByHandle(final String handle) {
        dslContext.deleteFrom(RAID_ORGANISATION)
                .where(RAID_ORGANISATION.HANDLE.eq(handle))
                .execute();
    }
}
