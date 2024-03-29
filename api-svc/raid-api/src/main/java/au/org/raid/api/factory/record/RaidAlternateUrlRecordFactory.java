package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidAlternateUrlRecord;
import au.org.raid.idl.raidv2.model.AlternateUrl;
import org.springframework.stereotype.Component;

@Component
public class RaidAlternateUrlRecordFactory {
    public RaidAlternateUrlRecord create(final AlternateUrl alternateUrl, final String handle) {
        return new RaidAlternateUrlRecord()
                .setHandle(handle)
                .setUrl(alternateUrl.getUrl());
    }
}
