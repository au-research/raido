package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidAlternateUrlRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidAlternateUrl.RAID_ALTERNATE_URL;

@Repository
@RequiredArgsConstructor
public class RaidAlternateUrlRepository {
    private DSLContext dslContext;

    public RaidAlternateUrlRecord create(final RaidAlternateUrlRecord record) {
        return dslContext.insertInto(RAID_ALTERNATE_URL)
                .set(RAID_ALTERNATE_URL.RAID_NAME, record.getRaidName())
                .set(RAID_ALTERNATE_URL.URL, record.getUrl())
                .returning()
                .fetchOne();
    }
}
