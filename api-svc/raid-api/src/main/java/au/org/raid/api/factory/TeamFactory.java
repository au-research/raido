package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.TeamRecord;
import au.org.raid.idl.raidv2.model.Team;
import org.springframework.stereotype.Component;

@Component
public class TeamFactory {
    public Team create(final TeamRecord teamRecord) {
        return new Team()
                .id(teamRecord.getId())
                .name(teamRecord.getName())
                .prefix(teamRecord.getPrefix())
                .servicePointId(teamRecord.getServicePointId());
    }
}
