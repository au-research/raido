package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidContributorRoleRecordFactoryTest {
    private final RaidContributorRoleRecordFactory factory = new RaidContributorRoleRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var raidContributorId = 123;
        final var contributorRoleId = 456;

        final var result = factory.create(raidContributorId, contributorRoleId);

        assertThat(result.getRaidContributorId(), is(raidContributorId));
        assertThat(result.getContributorRoleId(), is(contributorRoleId));
    }
}