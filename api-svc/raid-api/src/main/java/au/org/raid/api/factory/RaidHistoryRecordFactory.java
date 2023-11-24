package au.org.raid.api.factory;

import au.org.raid.api.entity.ChangeType;
import au.org.raid.api.service.Handle;
import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import jakarta.json.JsonPatch;
import org.springframework.stereotype.Component;
@Component
public class RaidHistoryRecordFactory {
    public RaidHistoryRecord create(final Handle handle, final int revision, final ChangeType changeType, final JsonPatch diff) {
        return new RaidHistoryRecord()
                .setHandle(handle.toString())
                .setRevision(revision)
                .setChangeType(changeType.name())
                .setDiff(diff.toString());
    }
}
