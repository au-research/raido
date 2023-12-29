package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidOrganisationRecordFactoryTest {
    private final RaidOrganisationRecordFactory factory = new RaidOrganisationRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var organisationId = 123;
        final var handle = "_handle";

        final var result = factory.create(organisationId, handle);

        assertThat(result.getOrganisationId(), is(organisationId));
        assertThat(result.getHandle(), is(handle));
    }
}