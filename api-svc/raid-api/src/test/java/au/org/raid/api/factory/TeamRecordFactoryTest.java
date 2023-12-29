package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.TeamCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TeamRecordFactoryTest {
    final TeamRecordFactory factory = new TeamRecordFactory();

    @Test
    @DisplayName("Sets all fields from TeamCreateRequest")
    void setsAllFieldsFromTeamCreateRequest() {
        final var name = "_name";
        final var prefix = "_prefix";
        final var servicePointId = 123L;

        final var createRequest = new TeamCreateRequest()
                .name(name)
                .prefix(prefix);

        final var result = factory.create(createRequest, servicePointId);

        assertThat(result.getName(), is(name));
        assertThat(result.getPrefix(), is(prefix));
        assertThat(result.getServicePointId(), is(servicePointId));
    }
}