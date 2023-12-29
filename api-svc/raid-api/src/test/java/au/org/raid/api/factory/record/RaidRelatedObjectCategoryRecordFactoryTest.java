package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidRelatedObjectCategoryRecordFactoryTest {
    private final RaidRelatedObjectCategoryRecordFactory factory = new RaidRelatedObjectCategoryRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setAllFields() {
        final var raidRelatedObjectId = 123;
        final var relatedObjectCategoryId = 456;

        final var result = factory.create(raidRelatedObjectId, relatedObjectCategoryId);

        assertThat(result.getRaidRelatedObjectId(), is(raidRelatedObjectId));
        assertThat(result.getRelatedObjectCategoryId(), is(relatedObjectCategoryId));
    }
}