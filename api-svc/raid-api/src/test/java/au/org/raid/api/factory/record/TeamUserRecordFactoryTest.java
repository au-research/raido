package au.org.raid.api.factory.record;

import au.org.raid.api.dto.TeamUserDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TeamUserRecordFactoryTest {
    private final TeamUserRecordFactory factory = new TeamUserRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var teamId = "team-id";
        final var userId = 123L;

        final var teamUser = TeamUserDto.builder()
                .teamId(teamId)
                .userId(userId)
                .build();

        final var result = factory.create(teamUser);

        assertThat(result.getTeamId(), is(teamId));
        assertThat(result.getAppUserId(), is(userId));
    }
}