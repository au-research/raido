package au.org.raid.api.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HandleFactoryTest {
    private final HandleFactory handleFactory = new HandleFactory();

    @Test
    @DisplayName("Returns a new handle")
    void createHandle() {
        final var prefix = "12345.54321";
        final var suffix = "abc123";
        final var input = "https://raid.org/%s/%s".formatted(prefix, suffix);

        final var handle = handleFactory.create(input);

        assertThat(handle.getPrefix(), is(prefix));
        assertThat(handle.getSuffix(), is(suffix));
        assertThat(handle.toString(), is("%s/%s".formatted(prefix, suffix)));
    }
}