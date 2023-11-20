package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static au.org.raid.db.jooq.tables.RaidHistory.RAID_HISTORY;

@Repository
@RequiredArgsConstructor
public class RaidHistoryRepository {
    private final DSLContext dslContext;
    public void insert(final RaidHistoryRecord raidHistoryRecord) {
        dslContext.insertInto(RAID_HISTORY)
                .set(RAID_HISTORY.HANDLE, raidHistoryRecord.getHandle())
                .set(RAID_HISTORY.REVISION, raidHistoryRecord.getRevision())
                .set(RAID_HISTORY.DIFF, raidHistoryRecord.getDiff())
                .set(RAID_HISTORY.CHANGE_TYPE, raidHistoryRecord.getChangeType())
                .set(RAID_HISTORY.CREATED, LocalDateTime.now())
                .execute();
    }

    public List<RaidHistoryRecord> findAllByHandle(final String handle) {
        return dslContext.select(RAID_HISTORY.fields())
                .from(RAID_HISTORY)
                .where(RAID_HISTORY.HANDLE.eq(handle))
                .orderBy(RAID_HISTORY.REVISION)
                .fetch(record -> new RaidHistoryRecord()
                        .setHandle(RAID_HISTORY.HANDLE.getValue(record))
                        .setRevision(RAID_HISTORY.REVISION.getValue(record))
                        .setDiff(RAID_HISTORY.DIFF.getValue(record))
                        .setCreated(RAID_HISTORY.CREATED.getValue(record))

                );
    }
}
