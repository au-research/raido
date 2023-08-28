package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.idl.raidv2.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class StableRaidoSchemaV1Test extends AbstractIntegrationTest {

    @Test
    @DisplayName("Mint a raid")
    void mintRaid() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getId().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        assertThat(result.getTitles()).isEqualTo(createRequest.getTitles());
        assertThat(result.getDescriptions()).isEqualTo(createRequest.getDescriptions());
        assertThat(result.getAccess()).isEqualTo(createRequest.getAccess());
        assertThat(result.getContributors()).isEqualTo(createRequest.getContributors());
        assertThat(result.getOrganisations()).isEqualTo(createRequest.getOrganisations());
        assertThat(result.getDates()).isEqualTo(createRequest.getDates());
    }

    @Test
    @DisplayName("Update a raid")
    void updateRaid() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getId().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var readResult = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        final var updateRequest = mapReadToUpdate(readResult);

        final var title = updateRequest.getTitles().get(0).getTitle() + " updated";

        updateRequest.getTitles().get(0).setTitle(title);

        try {
            final var updateResult = raidApi.updateRaidV1(handle.prefix(), handle.suffix(), updateRequest);
            assertThat(updateResult.getTitles().get(0).getTitle()).isEqualTo(title);
            assertThat(updateResult.getId().getVersion()).isEqualTo(2);
        } catch (final Exception e) {
            fail("Update failed");
        }

        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());
        assertThat(result.getTitles().get(0).getTitle()).isEqualTo(title);
        assertThat(result.getId().getVersion()).isEqualTo(2);
    }

    @Test
    @DisplayName("Raid does not update if there are no changes")
    void updateRaidNoOp() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getId().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var readResult = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        final var updateRequest = mapReadToUpdate(readResult);

        try {
            final var updateResult = raidApi.updateRaidV1(handle.prefix(), handle.suffix(), updateRequest);
            assertThat(updateResult.getId().getVersion()).isEqualTo(1);
        } catch (final Exception e) {
            fail("Update failed");
        }

        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());
        assertThat(result.getId().getVersion()).isEqualTo(1);
    }


    @Test
    @DisplayName("Resource not found error returned when raid not found on update")
    void notFound() {
        final var mintedRaid = raidApi.createRaidV1(createRequest);

        final var path = URI.create(mintedRaid.getId().getId()).getPath();

        final var handle = (IdentifierHandle) identifierParser.parseHandle(path);
        final var readResult = raidApi.readRaidV1(handle.prefix(), handle.suffix());

        final var updateRequest = mapReadToUpdate(readResult);

        try {
            final var updateResult = raidApi.updateRaidV1(handle.prefix(), handle.suffix(), updateRequest);
            assertThat(updateResult.getId().getVersion()).isEqualTo(1);
        } catch (final Exception e) {
            fail("Update failed");
        }

        final var result = raidApi.readRaidV1(handle.prefix(), handle.suffix());
        assertThat(result.getId().getVersion()).isEqualTo(1);
    }

    private UpdateRaidV1Request mapReadToUpdate(RaidDto read) {
        return new UpdateRaidV1Request()
                .id(read.getId())
                .titles(read.getTitles())
                .dates(read.getDates())
                .descriptions(read.getDescriptions())
                .access(read.getAccess())
                .alternateUrls(read.getAlternateUrls())
                .contributors(read.getContributors())
                .organisations(read.getOrganisations())
                .subjects(read.getSubjects())
                .relatedRaids(read.getRelatedRaids())
                .relatedObjects(read.getRelatedObjects())
                .alternateIdentifiers(read.getAlternateIdentifiers())
                .spatialCoverages(read.getSpatialCoverages())
                .traditionalKnowledgeLabels(read.getTraditionalKnowledgeLabels());
    }

    public Contributor contributor(
            final String orcid,
            final String position,
            String role,
            LocalDate startDate
    ) {
        return new Contributor()
                .id(orcid)
                .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .positions(List.of(new ContributorPositionWithSchemaUri()
                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                        .id(position)
                        .startDate(startDate)))
                .roles(List.of(
                        new ContributorRoleWithSchemaUri()
                                .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                .id(role)));
    }

    public Organisation organisation(
            String ror,
            String role,
            LocalDate today
    ) {
        return new Organisation()
                .id(ror)
                .schemaUri(ORGANISATION_IDENTIFIER_SCHEMA_URI)
                .roles(List.of(
                        new OrganisationRoleWithSchemaUri()
                                .schemaUri(ORGANISATION_ROLE_SCHEMA_URI)
                                .id(role)
                                .startDate(today)));
    }

}