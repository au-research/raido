package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidAlternateIdentifierRecord;
import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.springframework.stereotype.Component;

@Component
public class RaidAlternateIdentifierRecordFactory {
    public RaidAlternateIdentifierRecord create(final AlternateIdentifier alternateIdentifier, final String raidName) {
        return new RaidAlternateIdentifierRecord()
                .setId(alternateIdentifier.getId())
                .setType(alternateIdentifier.getType())
                .setRaidName(raidName);
    }
}
