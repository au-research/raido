package au.org.raid.inttest;

import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.Title;
import au.org.raid.inttest.service.Handle;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Objects;
import java.util.stream.IntStream;

import static au.org.raid.inttest.service.TestConstants.PRIMARY_TITLE_TYPE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Slf4j
public class RaidHistoryIntegrationTest extends AbstractIntegrationTest {
    @Test
    @DisplayName("Changes are saved to history table")
    void changesSaved() {
        createRequest.getTitle().get(0).setText("Version 1");

        RaidDto raid;
        Handle handle;

        try {
            final var createResponse = raidApi.mintRaid(createRequest);
            handle = new Handle(Objects.requireNonNull(createResponse.getBody()).getIdentifier().getId());
            raid = createResponse.getBody();
        } catch (Exception e) {
            log.error("Error: ", e);
            throw new RuntimeException(e);
        }

        IntStream.range(1,7).forEach(oldVersion -> {
            log.info("Update version {}", oldVersion);
            final var newVersion = oldVersion + 1;
            final var text = "Version %d".formatted(newVersion);

            final var primaryTitle = getPrimaryTitle(raid);

            primaryTitle.setText(text);
            raid.getIdentifier().setVersion(oldVersion);

            try {
                raidApi.updateRaid(handle.getPrefix(), handle.getSuffix(), raidUpdateRequestFactory.create(raid));
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Error: ", e);
                throw new RuntimeException(e);
            }

            final var response = raidApi.findRaidByName(handle.getPrefix(), handle.getSuffix(), newVersion);

            final var raidDto = response.getBody();
            assert raidDto != null;

            assertThat(raidDto.getIdentifier().getVersion()).isEqualTo(newVersion);
            assertThat(getPrimaryTitle(raidDto).getText()).isEqualTo(text);
        });

        final var version = 3;

        final var response = raidApi.findRaidByName(handle.getPrefix(), handle.getSuffix(), version);
        final var raidDto = response.getBody();
        assert raidDto != null;

        assertThat(raidDto.getIdentifier().getVersion()).isEqualTo(version);
        assertThat(raidDto.getTitle().get(0).getText()).isEqualTo("Version %d".formatted(version));

        final var historyResponse = raidApi.raidHistory(handle.getPrefix(), handle.getSuffix());

        final var history = historyResponse.getBody();

        assertThat(history).hasSize(7);
    }

    @Test
    @DisplayName("History of embargoed raid is not accessible by user from different service point")
    void embargoedRaid() {
        final var createResponse = raidApi.mintRaid(createRequest);

        Handle handle = new Handle(Objects.requireNonNull(createResponse.getBody()).getIdentifier().getId());

        final var otherClient = testClient.raidApi(uqToken);

        try {
            otherClient.raidHistory(handle.getPrefix(), handle.getSuffix());
            fail("Should return 403 status");
        } catch (Exception e) {
            assertThat(e).isInstanceOf(FeignException.Forbidden.class);
        }
    }

    private Title getPrimaryTitle(final RaidDto raidDto) {
        return raidDto.getTitle().stream()
                .filter(title -> title.getType().getId().equals(PRIMARY_TITLE_TYPE))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No primary title :("));
    }
}
