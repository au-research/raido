package au.org.raid.api.factory;

import au.org.raid.db.jooq.tables.records.TeamRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class TeamFactoryTest {
    final TeamFactory factory = new TeamFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = "_id";
        final var name = "_name";
        final var prefix = "_prefix";
        final var servicePointId = 123L;

        final var record = new TeamRecord()
                .setId(id)
                .setName(name)
                .setPrefix(prefix)
                .setServicePointId(servicePointId);

        final var result = factory.create(record);

        assertThat(result.getId(), is(id));
        assertThat(result.getName(), is(name));
        assertThat(result.getPrefix(), is(prefix));
        assertThat(result.getServicePointId(), is(servicePointId));
    }
}