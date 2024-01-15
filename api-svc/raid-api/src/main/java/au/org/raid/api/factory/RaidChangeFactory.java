package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.RaidHistoryRecord;
import au.org.raid.idl.raidv2.model.RaidChange;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Base64;

@Component
public class RaidChangeFactory {
    public RaidChange create(RaidHistoryRecord record) {
        return new RaidChange()
                .handle(record.getHandle())
                .version(record.getRevision())
                .diff(new String(Base64.getEncoder().encode(record.getDiff().getBytes())))
                .timestamp(record.getCreated().atOffset(ZoneOffset.UTC));
    }
}
