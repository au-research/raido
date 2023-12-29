package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidSpatialCoveragePlaceRecordFactoryTest {
    private final RaidSpatialCoveragePlaceRecordFactory factory = new RaidSpatialCoveragePlaceRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var raidSpatialCoverageId = 123;
        final var place = "_place";
        final var languageId = 456;

        final var result = factory.create(raidSpatialCoverageId, place, languageId);

        assertThat(result.getRaidSpatialCoverageId(), is(raidSpatialCoverageId));
        assertThat(result.getPlace(), is(place));
        assertThat(result.getLanguageId(), is(languageId));
    }
}