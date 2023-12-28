package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.TeamRecord;
import au.org.raid.idl.raidv2.model.Team;
import au.org.raid.idl.raidv2.model.TeamCreateRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TeamRecordFactory {
    public TeamRecord create(final TeamCreateRequest createRequest, final Long servicePointId) {
        return new TeamRecord()
                .setId(UUID.randomUUID().toString())
                .setName(createRequest.getName())
                .setPrefix(createRequest.getPrefix())
                .setServicePointId(servicePointId);
    }

    public TeamRecord create(final Team team) {
        return new TeamRecord()
                .setId(UUID.randomUUID().toString())
                .setName(team.getName())
                .setPrefix(team.getPrefix())
                .setServicePointId(team.getServicePointId());
    }
}
