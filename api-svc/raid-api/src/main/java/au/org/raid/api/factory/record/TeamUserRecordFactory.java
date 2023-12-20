package au.org.raid.api.factory.record;

import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.db.jooq.tables.records.TeamUserRecord;
import org.springframework.stereotype.Component;

@Component
public class TeamUserRecordFactory {
    public TeamUserRecord create(final TeamUserDto teamUserDto) {
        return new TeamUserRecord()
                .setTeamId(teamUserDto.getTeamId())
                .setAppUserId(teamUserDto.getUserId());
    }
}
