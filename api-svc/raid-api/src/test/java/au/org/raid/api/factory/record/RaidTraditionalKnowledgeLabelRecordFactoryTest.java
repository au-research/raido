package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidTraditionalKnowledgeLabelRecordFactoryTest {
    private final RaidTraditionalKnowledgeLabelRecordFactory factory = new RaidTraditionalKnowledgeLabelRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var handle = "_handle";
        final var labelId = 123;
        final var schemaId = 456;

        final var result = factory.create(handle, labelId, schemaId);

        assertThat(result.getHandle(), is(handle));
        assertThat(result.getTraditionalKnowledgeLabelId(), is(labelId));
        assertThat(result.getTraditionalKnowledgeLabelSchemaId(), is(schemaId));
    }
}