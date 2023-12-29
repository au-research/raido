package au.org.raid.api.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AlternateIdentifierFactoryTest {
    private final AlternateIdentifierFactory factory = new AlternateIdentifierFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = "_id";
        final var type = "_type";

        final var result = factory.create(id, type);

        assertThat(result.getId(), is(id));
        assertThat(result.getType(), is(type));
    }
}