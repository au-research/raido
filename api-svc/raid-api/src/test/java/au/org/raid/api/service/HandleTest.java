package au.org.raid.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class HandleTest {
    @Test
    @DisplayName("toString() returns handle from uri")
    void toStringReturnsHandleFromUri() {
        final var uri = "https://raid.org/10.25.1.1/abcde";

        final var handle = new Handle(uri);

        assertThat(handle.toString(), is("10.25.1.1/abcde"));
        assertThat(handle.getPrefix(), is("10.25.1.1"));
        assertThat(handle.getSuffix(), is("abcde"));
    }

    @Test
    @DisplayName("toString() returns handle from uri")
    void toStringReturnsHandleFromPrefixAndSuffix() {
        final var prefix = "10.25.1.1";
        final var suffix = "abcde";

        final var handle = new Handle(prefix, suffix);

        assertThat(handle.getPrefix(), is(prefix));
        assertThat(handle.getSuffix(), is(suffix));
        assertThat(handle.toString(), is("10.25.1.1/abcde"));
    }
}