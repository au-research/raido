package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidOrganisationRoleRecordFactoryTest {
    private final RaidOrganisationRoleRecordFactory factory = new RaidOrganisationRoleRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var raidOrganisationId = 123;
        final var organisationRoleId = 456;
        final var startDate = "2021";
        final var endDate = "2022";

        final var result = factory.create(raidOrganisationId, organisationRoleId, startDate, endDate);

        assertThat(result.getRaidOrganisationId(), is(raidOrganisationId));
        assertThat(result.getOrganisationRoleId(), is(organisationRoleId));
        assertThat(result.getStartDate(), is(startDate));
        assertThat(result.getEndDate(), is(endDate));
    }
}