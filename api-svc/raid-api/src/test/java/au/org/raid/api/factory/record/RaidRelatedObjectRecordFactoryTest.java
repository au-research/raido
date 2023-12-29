package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidRelatedObjectRecordFactoryTest {
    private final RaidRelatedObjectRecordFactory factory = new RaidRelatedObjectRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var handle = "_handle";
        final var relatedObjectId = 123;
        final var relatedObjectTypeId = 456;

        final var result = factory.create(handle, relatedObjectId, relatedObjectTypeId);

        assertThat(result.getHandle(), is(handle));
        assertThat(result.getRelatedObjectId(), is(relatedObjectId));
        assertThat(result.getRelatedObjectTypeId(), is(relatedObjectTypeId));
    }
}