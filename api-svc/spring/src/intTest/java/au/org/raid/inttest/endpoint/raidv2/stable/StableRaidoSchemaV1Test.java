package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.UpdateRaidV1Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StableRaidoSchemaV1Test extends AbstractIntegrationTest {

    @Test
    @DisplayName("Mint a raid")
    void mintRaid() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getIdentifier().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        assertThat(result.getTitle()).isEqualTo(createRequest.getTitle());
        assertThat(result.getDescription()).isEqualTo(createRequest.getDescription());
        assertThat(result.getAccess()).isEqualTo(createRequest.getAccess());
        assertThat(result.getContributor()).isEqualTo(createRequest.getContributor());
        assertThat(result.getOrganisation()).isEqualTo(createRequest.getOrganisation());
        assertThat(result.getDate()).isEqualTo(createRequest.getDate());
    }

    @Test
    @DisplayName("Update a raid")
    void updateRaid() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getIdentifier().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var readResult = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        final var updateRequest = mapReadToUpdate(readResult);

        final var title = updateRequest.getTitle().get(0).getText() + " updated";

        updateRequest.getTitle().get(0).setText(title);

        try {
            final var updateResult = raidApi.updateRaidV1(handle.prefix(), handle.suffix(), updateRequest);
            assertThat(updateResult.getTitle().get(0).getText()).isEqualTo(title);
            assertThat(updateResult.getIdentifier().getVersion()).isEqualTo(2);
        } catch (final Exception e) {
            fail("Update failed");
        }

        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());
        assertThat(result.getTitle().get(0).getText()).isEqualTo(title);
        assertThat(result.getIdentifier().getVersion()).isEqualTo(2);
    }

    @Test
    @DisplayName("Raid does not update if there are no changes")
    void updateRaidNoOp() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getIdentifier().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var readResult = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        final var updateRequest = mapReadToUpdate(readResult);

        try {
            final var updateResult = raidApi.updateRaidV1(handle.prefix(), handle.suffix(), updateRequest);
            assertThat(updateResult.getIdentifier().getVersion()).isEqualTo(1);
        } catch (final Exception e) {
            fail("Update failed");
        }

        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());
        assertThat(result.getIdentifier().getVersion()).isEqualTo(1);
    }


    @Test
    @DisplayName("Resource not found error returned when raid not found on update")
    void notFound() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getIdentifier().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var readResult = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        final var updateRequest = mapReadToUpdate(readResult);

        try {
            final var updateResult = raidApi.updateRaidV1(handle.prefix(), handle.suffix(), updateRequest);
            assertThat(updateResult.getIdentifier().getVersion()).isEqualTo(1);
        } catch (final Exception e) {
            fail("Update failed");
        }

        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());
        assertThat(result.getIdentifier().getVersion()).isEqualTo(1);
    }

    private UpdateRaidV1Request mapReadToUpdate(RaidDto read) {
        return new UpdateRaidV1Request()
                .identifier(read.getIdentifier())
                .title(read.getTitle())
                .date(read.getDate())
                .description(read.getDescription())
                .access(read.getAccess())
                .alternateUrl(read.getAlternateUrl())
                .contributor(read.getContributor())
                .organisation(read.getOrganisation())
                .subject(read.getSubject())
                .relatedRaid(read.getRelatedRaid())
                .relatedObject(read.getRelatedObject())
                .alternateIdentifier(read.getAlternateIdentifier())
                .spatialCoverage(read.getSpatialCoverage())
                .traditionalKnowledgeLabel(read.getTraditionalKnowledgeLabel());
    }
}