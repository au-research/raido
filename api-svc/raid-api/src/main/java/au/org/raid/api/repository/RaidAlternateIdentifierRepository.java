package au.org.raid.api.repository;

import java.util.List;
import au.org.raid.db.jooq.tables.records.RaidAlternateIdentifierRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidAlternateIdentifier.RAID_ALTERNATE_IDENTIFIER;

@Repository
@RequiredArgsConstructor
public class RaidAlternateIdentifierRepository {
    private final DSLContext dslContext;

    public RaidAlternateIdentifierRecord create(final RaidAlternateIdentifierRecord record) {
        return dslContext.insertInto(RAID_ALTERNATE_IDENTIFIER)
                .set(RAID_ALTERNATE_IDENTIFIER.HANDLE, record.getHandle())
                .set(RAID_ALTERNATE_IDENTIFIER.TYPE, record.getType())
                .set(RAID_ALTERNATE_IDENTIFIER.ID, record.getId())
                .returning()
                .fetchOne();
    }

    public List<RaidAlternateIdentifierRecord> findAllByHandle(final String handle) {
        return dslContext.selectFrom(RAID_ALTERNATE_IDENTIFIER)
                .where(RAID_ALTERNATE_IDENTIFIER.HANDLE.eq(handle))
                .fetch();
    }

    public void deleteAllByHandle(final String handle) {
        dslContext.deleteFrom(RAID_ALTERNATE_IDENTIFIER)
                .where(RAID_ALTERNATE_IDENTIFIER.HANDLE.eq(handle))
                .execute();
    }
}
