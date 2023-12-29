package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RelatedRaidRecordFactoryTest {
    private final RelatedRaidRecordFactory factory = new RelatedRaidRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var handle = "_handle";
        final var relatedRaidHandle = "related-raid-handle";
        final var relatedRaidTypeId = 123;

        final var result = factory.create(handle, relatedRaidHandle, relatedRaidTypeId);

        assertThat(result.getHandle(), is(handle));
        assertThat(result.getRelatedRaidHandle(), is(relatedRaidHandle));
        assertThat(result.getRelatedRaidTypeId(), is(relatedRaidTypeId));
    }
}