package au.org.raid.api.factory.record;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class RaidAlternateIdentifierRecordFactoryTest {
    private final RaidAlternateIdentifierRecordFactory factory = new RaidAlternateIdentifierRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = "alternate-id";
        final var type = "id-type";
        final var handle = "raid-handle";

        final var alternateIdentifier = new AlternateIdentifier()
                .id(id)
                .type(type);

        final var result = factory.create(alternateIdentifier, handle);

        assertThat(result.getId(), is(id));
        assertThat(result.getType(), is(type));
        assertThat(result.getHandle(), is(handle));
    }
}