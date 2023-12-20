package au.org.raid.api.factory;

import au.org.raid.api.dto.TeamDto;
import au.org.raid.db.jooq.tables.records.TeamRecord;
import org.springframework.stereotype.Component;

@Component
public class TeamRecordFactory {
    public TeamRecord create(final TeamDto teamDto) {
        return new TeamRecord()
                .setId(teamDto.getId())
                .setName(teamDto.getName())
                .setPrefix(teamDto.getPrefix())
                .setServicePointId(teamDto.getServicePointId());

    }
}
