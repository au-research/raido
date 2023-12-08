package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidDescriptionRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import static au.org.raid.db.jooq.tables.RaidDescription.RAID_DESCRIPTION;

@Repository
@RequiredArgsConstructor
public class RaidDescriptionRepository {
    private final DSLContext dslContext;

    public RaidDescriptionRecord create(final RaidDescriptionRecord record) {
        return dslContext.insertInto(RAID_DESCRIPTION)
                .set(RAID_DESCRIPTION.RAID_NAME, record.getRaidName())
                .set(RAID_DESCRIPTION.DESCRIPTION_TYPE_ID, record.getDescriptionTypeId())
                .set(RAID_DESCRIPTION.TEXT, record.getText())
                .set(RAID_DESCRIPTION.LANGUAGE_ID, record.getLanguageId())
                .returning()
                .fetchOne();
    }
}
