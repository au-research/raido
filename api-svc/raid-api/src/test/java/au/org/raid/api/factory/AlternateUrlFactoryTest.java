package au.org.raid.api.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AlternateUrlFactoryTest {
    private final AlternateUrlFactory factory = new AlternateUrlFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var url = "_url";

        final var result = factory.create(url);

        assertThat(result.getUrl(), is(url));
    }
}