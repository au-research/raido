package au.org.raid.api.repository;

import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static au.org.raid.db.jooq.tables.RaidHistory.RAID_HISTORY;
import static org.jooq.impl.DSL.max;

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
        final var maxBaselineRevision = findMaxBaseline(handle);

        if (maxBaselineRevision.isPresent()) {
            return findAllWithBaselineByHandle(handle, maxBaselineRevision.get());
        }

        return dslContext.select(RAID_HISTORY.fields())
                .from(RAID_HISTORY)
                .where(RAID_HISTORY.HANDLE.eq(handle))
                .orderBy(RAID_HISTORY.REVISION)
                .fetch(record -> new RaidHistoryRecord()
                        .setHandle(RAID_HISTORY.HANDLE.getValue(record))
                        .setRevision(RAID_HISTORY.REVISION.getValue(record))
                        .setChangeType(RAID_HISTORY.CHANGE_TYPE.getValue(record))
                        .setDiff(RAID_HISTORY.DIFF.getValue(record))
                        .setCreated(RAID_HISTORY.CREATED.getValue(record))

                );
    }

    public Optional<Integer> findMaxBaseline(final String handle) {
        return dslContext.select(max(RAID_HISTORY.REVISION))
                .from(RAID_HISTORY)
                .where(RAID_HISTORY.HANDLE.eq(handle))
                .and(RAID_HISTORY.CHANGE_TYPE.eq("BASELINE"))
                .fetchOptional(max(RAID_HISTORY.REVISION));
    }

    public List<RaidHistoryRecord> findAllWithBaselineByHandle(final String handle, final int revision) {
        return dslContext.select(RAID_HISTORY.fields())
                .from(RAID_HISTORY)
                .where(RAID_HISTORY.HANDLE.eq(handle))
                .and(RAID_HISTORY.CHANGE_TYPE.eq("BASELINE"))
                .and(RAID_HISTORY.REVISION.eq(revision))
                .unionAll(dslContext.select(RAID_HISTORY.fields())
                        .from(RAID_HISTORY)
                        .where(RAID_HISTORY.HANDLE.eq(handle))
                        .and(RAID_HISTORY.CHANGE_TYPE.eq("PATCH"))
                        .and(RAID_HISTORY.REVISION.gt(revision))
                        .orderBy(RAID_HISTORY.REVISION))
                .fetch(record -> new RaidHistoryRecord()
                        .setHandle(RAID_HISTORY.HANDLE.getValue(record))
                        .setRevision(RAID_HISTORY.REVISION.getValue(record))
                        .setChangeType(RAID_HISTORY.CHANGE_TYPE.getValue(record))
                        .setDiff(RAID_HISTORY.DIFF.getValue(record))
                        .setCreated(RAID_HISTORY.CREATED.getValue(record))
                );
    }

}
