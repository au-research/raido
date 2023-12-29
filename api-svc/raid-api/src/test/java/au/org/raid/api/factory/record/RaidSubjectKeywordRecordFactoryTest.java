package au.org.raid.api.factory.record;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidSubjectKeywordRecordFactoryTest {
    private final RaidSubjectKeywordRecordFactory factory = new RaidSubjectKeywordRecordFactory();

    @Test
    @DisplayName("Sets all fields")
    void setAllFields() {
        final var raidSubjectId = 123;
        final var keyword = "_keyword";
        final var langaugeId = 456;

        final var result = factory.create(raidSubjectId, keyword, langaugeId);

        assertThat(result.getRaidSubjectId(), is(raidSubjectId));
        assertThat(result.getKeyword(), is(keyword));
        assertThat(result.getLanguageId(), is(langaugeId));
    }
}