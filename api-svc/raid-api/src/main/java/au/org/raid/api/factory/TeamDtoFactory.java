package au.org.raid.api.factory;

import au.org.raid.api.dto.TeamDto;
import au.org.raid.db.jooq.tables.records.TeamRecord;
import org.springframework.stereotype.Component;

@Component
public class TeamDtoFactory {
    public TeamDto create(final TeamRecord teamRecord) {
        return TeamDto.builder()
                .id(teamRecord.getId())
                .name(teamRecord.getName())
                .prefix(teamRecord.getPrefix())
                .servicePointId(teamRecord.getServicePointId())
                .build();
    }
}
