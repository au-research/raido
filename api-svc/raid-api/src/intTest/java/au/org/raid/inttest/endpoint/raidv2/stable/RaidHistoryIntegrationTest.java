package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.api.service.Handle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.IntStream;

import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.PRIMARY_TITLE_TYPE;

public class RaidHistoryIntegrationTest extends AbstractIntegrationTest {


    @Test
    @DisplayName("Changes are saved to history table")
    void changesSaved() {
        final var createResponse = raidApi.createRaidV1(createRequest);

        Handle handle = new Handle(Objects.requireNonNull(createResponse.getBody()).getIdentifier().getId());

        var raid = createResponse.getBody();

        IntStream.range(1,13).forEach(i -> {
            final var text = "Title %d".formatted(i);

            final var primaryTitle = raid.getTitle().stream()
                    .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No primary title :("));

            primaryTitle.setText(text);
            raid.getIdentifier().setVersion(i);

            raidApi.updateRaidV1(handle.getPrefix(), handle.getSuffix(), raidUpdateRequestFactory.create(raid));
        });


        raid.setTraditionalKnowledgeLabel(Collections.emptyList());
        raid.getIdentifier().setVersion(13);
        raidApi.updateRaidV1(handle.getPrefix(), handle.getSuffix(), raidUpdateRequestFactory.create(raid));
    }

}
