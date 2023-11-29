package au.org.raid.api.factory;

import au.org.raid.api.entity.ChangeType;
import au.org.raid.api.service.Handle;
import jakarta.json.Json;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RaidHistoryRecordFactoryTest {
    private final RaidHistoryRecordFactory raidHistoryRecordFactory = new RaidHistoryRecordFactory();

    @Test
    @DisplayName("Create returns new RaidHistoryRecord with fields initialised")
    void create() {
        final var revision = 123;
        final var changeType = ChangeType.PATCH;
        final var handleString = "123/abc";
        final var handle = new Handle(handleString);

        final var existing = Json.createReader(new StringReader("{}")).readObject();
        final var updated = Json.createReader(new StringReader("{\"abc\":123}")).readObject();

        final var diff = Json.createDiff(existing, updated);

        final var record = raidHistoryRecordFactory.create(handle, revision, changeType, diff);

        assertThat(record.getRevision(), is(revision));
        assertThat(record.getChangeType(), is("PATCH"));
        assertThat(record.getHandle(), is(handleString));
        assertThat(record.getDiff(), is("[{\"op\":\"add\",\"path\":\"/abc\",\"value\":123}]"));
    }
}