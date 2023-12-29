package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RelatedObjectRecordFactoryTest {
    private final RelatedObjectRecordFactory factory = new RelatedObjectRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var pid = "_pid";
        final var schemaId = 123;

        final var result = factory.create(pid, schemaId);

        assertThat(result.getPid(), is(pid));
        assertThat(result.getSchemaId(), is(schemaId));
    }
}