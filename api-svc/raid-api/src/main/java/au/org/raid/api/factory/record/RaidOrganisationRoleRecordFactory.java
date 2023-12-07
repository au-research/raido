package au.org.raid.api.factory.record;

import au.org.raid.db.jooq.tables.records.RaidOrganisationRoleRecord;
import org.springframework.stereotype.Component;

@Component
public class RaidOrganisationRoleRecordFactory {
    public RaidOrganisationRoleRecord create(final int raidOrganisationId, final Integer organisationRoleId, final String startDate, final String endDate) {
        return new RaidOrganisationRoleRecord()
                .setRaidOrganisationId(raidOrganisationId)
                .setOrganisationRoleId(organisationRoleId)
                .setStartDate(startDate)
                .setEndDate(endDate);
    }
}
