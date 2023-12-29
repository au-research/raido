package au.org.raid.api.factory.record;

import au.org.raid.idl.raidv2.model.ContributorPosition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidContributorPositionRecordFactoryTest {
    private final RaidContributorPositionRecordFactory factory = new RaidContributorPositionRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var contributorPositionId = 123;
        final var raidContributorId = 456;
        final var startDate = "2021";
        final var endDate = "2022";

        final var contributorPosition = new ContributorPosition()
                .startDate(startDate)
                .endDate(endDate);

        final var result = factory.create(contributorPosition, raidContributorId, contributorPositionId);

        assertThat(result.getRaidContributorId(), is(raidContributorId));
        assertThat(result.getContributorPositionId(), is(contributorPositionId));
        assertThat(result.getStartDate(), is(startDate));
        assertThat(result.getEndDate(), is(endDate));
    }
}