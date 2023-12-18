package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidOrganisationRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidOrganisationRecordFactory {
    public RaidOrganisationRecord create(final int organisationId, final String handle) {
        return new RaidOrganisationRecord()
                .setOrganisationId(organisationId)
                .setHandle(handle);
    }
}
