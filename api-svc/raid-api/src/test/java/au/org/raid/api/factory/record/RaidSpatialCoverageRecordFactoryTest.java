package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidSpatialCoverageRecordFactoryTest {
    private final RaidSpatialCoverageRecordFactory factory = new RaidSpatialCoverageRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setAllFields() {
        final var uri = "_uri";
        final var handle = "_handle";
        final var schemaId = 123;

        final var result = factory.create(uri, handle, schemaId);

        assertThat(result.getUri(), is(uri));
        assertThat(result.getHandle(), is(handle));
        assertThat(result.getSchemaId(), is(schemaId));
    }
}