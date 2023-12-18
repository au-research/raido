package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidAlternateUrlRecord;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;

import static au.org.raid.db.jooq.tables.RaidAlternateUrl.RAID_ALTERNATE_URL;

@Repository
@RequiredArgsConstructor
public class RaidAlternateUrlRepository {
    private final DSLContext dslContext;
    public RaidAlternateUrlRecord create(final RaidAlternateUrlRecord record) {
        return dslContext.insertInto(RAID_ALTERNATE_URL)
                .set(RAID_ALTERNATE_URL.HANDLE, record.getHandle())
                .set(RAID_ALTERNATE_URL.URL, record.getUrl())
                .returning()
                .fetchOne();
    }

    public List<RaidAlternateUrlRecord> findAllByHandle(final String handle) {
        return dslContext.selectFrom(RAID_ALTERNATE_URL)
                .where(RAID_ALTERNATE_URL.HANDLE.eq(handle))
                .fetch();
    }

}
