package au.org.raid.api.factory;

import au.org.raid.api.dto.TeamUserDto;
import au.org.raid.db.jooq.tables.records.TeamUserRecord;
import org.springframework.stereotype.Component;

@Component
public class TeamUserDtoFactory {
    public TeamUserDto create(final TeamUserRecord record) {
        return TeamUserDto.builder()
                .teamId(record.getTeamId())
                .userId(record.getAppUserId())
                .build();
    }
}
