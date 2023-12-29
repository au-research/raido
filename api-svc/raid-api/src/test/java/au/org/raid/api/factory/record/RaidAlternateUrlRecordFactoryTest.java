package au.org.raid.api.factory.record;

import au.org.raid.idl.raidv2.model.AlternateUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidAlternateUrlRecordFactoryTest {
    private final RaidAlternateUrlRecordFactory factory = new RaidAlternateUrlRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setAllFields() {
        final var url = "_url";
        final var handle = "_handle";

        final var alternateUrl = new AlternateUrl()
                .url(url);

        final var result = factory.create(alternateUrl, handle);

        assertThat(result.getUrl(), is(url));
        assertThat(result.getHandle(), is(handle));
    }
}