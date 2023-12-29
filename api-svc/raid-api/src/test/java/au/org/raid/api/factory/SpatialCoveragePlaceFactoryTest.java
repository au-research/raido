package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SpatialCoveragePlaceFactoryTest {
    private final SpatialCoveragePlaceFactory factory = new SpatialCoveragePlaceFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var text = "_text";
        final var language = new Language();

        final var result = factory.create(text, language);

        assertThat(result.getText(), is(text));
        assertThat(result.getLanguage(), is(language));
    }
}