package au.org.raid.api.factory.record;

import au.org.raid.idl.raidv2.model.Title;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidTitleRecordFactoryTest {
    private final RaidTitleRecordFactory factory = new RaidTitleRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var startDate = "2021";
        final var endDate = "2022";
        final var text = "_text";
        final var handle = "_handle";
        final var titleTypeId = 123;
        final var languageId = 456;

        final var title = new Title()
                .startDate(startDate)
                .endDate(endDate)
                .text(text);

        final var result = factory.create(title, handle, titleTypeId, languageId);

        assertThat(result.getStartDate(), is(startDate));
        assertThat(result.getEndDate(), is(endDate));
        assertThat(result.getText(), is(text));
        assertThat(result.getHandle(), is(handle));
        assertThat(result.getTitleTypeId(), is(titleTypeId));
        assertThat(result.getLanguageId(), is(languageId));
    }
}