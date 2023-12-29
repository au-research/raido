package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidDescriptionRecordFactoryTest {
    private final RaidDescriptionRecordFactory factory = new RaidDescriptionRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var handle = "_handle";
        final var text = "_text";
        final var descriptionTypeId = 123;
        final var languageId = 456;

        final var result = factory.create(handle, text, descriptionTypeId, languageId);

        assertThat(result.getHandle(), is(handle));
        assertThat(result.getText(), is(text));
        assertThat(result.getDescriptionTypeId(), is(descriptionTypeId));
        assertThat(result.getLanguageId(), is(languageId));
    }
}