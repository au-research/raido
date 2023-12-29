package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.TeamUserRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TeamUserDtoFactoryTest {
    private final TeamUserDtoFactory factory = new TeamUserDtoFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var teamId = "team-id";
        final var userId = 123L;

        final var record = new TeamUserRecord()
                .setTeamId(teamId)
                .setAppUserId(userId);

        final var result = factory.create(record);

        assertThat(result.getTeamId(), is(teamId));
        assertThat(result.getUserId(), is(userId));
    }
}