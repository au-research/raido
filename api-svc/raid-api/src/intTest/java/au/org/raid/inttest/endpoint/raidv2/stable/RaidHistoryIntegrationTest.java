package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.api.service.Handle;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.idl.raidv2.model.TitleTypeWithSchemaUri;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.IntStream;

import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.PRIMARY_TITLE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

public class RaidHistoryIntegrationTest extends AbstractIntegrationTest {
    @Test
    @DisplayName("Changes are saved to history table")
    void changesSaved() {
        final var createResponse = raidApi.createRaidV1(createRequest);

        Handle handle = new Handle(Objects.requireNonNull(createResponse.getBody()).getIdentifier().getId());

        var raid = createResponse.getBody();

        IntStream.range(1,7).forEach(i -> {
            final var text = "Title %d".formatted(i);

            final var primaryTitle = getPrimaryTitle(raid);

            primaryTitle.setText(text);
            raid.getIdentifier().setVersion(i);

            raidApi.updateRaidV1(handle.getPrefix(), handle.getSuffix(), raidUpdateRequestFactory.create(raid));

            final var response = raidApi.readRaidV1(handle.getPrefix(), handle.getSuffix());

            final var raidDto = response.getBody();
            assert raidDto != null;

            assertThat(raidDto.getIdentifier().getVersion()).isEqualTo(i + 1);
            assertThat(getPrimaryTitle(raidDto).getText()).isEqualTo(text);
        });

        raid.setTraditionalKnowledgeLabel(Collections.emptyList());
        raid.getIdentifier().setVersion(7);
        raidApi.updateRaidV1(handle.getPrefix(), handle.getSuffix(), raidUpdateRequestFactory.create(raid));

        final var response = raidApi.readRaidV1(handle.getPrefix(), handle.getSuffix());
        final var raidDto = response.getBody();
        assert raidDto != null;

        assertThat(raidDto.getTraditionalKnowledgeLabel()).isEmpty();
    }

    private Title getPrimaryTitle(final RaidDto raidDto) {
        return raidDto.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No primary title :("));
    }

}
