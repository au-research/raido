package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AlternateUrl;
import au.org.raid.idl.raidv2.model.AlternateUrlBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class AlternateUrlFactoryTest {
    private final AlternateUrlFactory alternateUrlFactory = new AlternateUrlFactory();

    @Test
    @DisplayName("If AlternateUrlBlock is null returns null")
    void returnsNull() {
        assertThat(alternateUrlFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If AlternateUrlBlock has empty fields returns empty fields")
    void emptyFields() {
        assertThat(alternateUrlFactory.create(new AlternateUrlBlock()), is(new AlternateUrl()));
    }

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var url = "_url";

        final var alternateUrl = new AlternateUrlBlock().url(url);

        final var expected = new AlternateUrl().url(url);

        assertThat(alternateUrlFactory.create(alternateUrl), is(expected));
    }
}