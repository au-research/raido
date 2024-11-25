package au.org.raid.inttest;

import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import au.org.raid.inttest.service.RaidApiValidationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class OrganisationControllerIntegrationTest extends AbstractIntegrationTest {
    private static final String ORCID = "https://orcid.org/0009-0006-4129-5257";
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("List all raids for an orcid ")
    void listRaidsWithAGivenOrcid() {
        createRequest.getContributor().forEach(contributor -> contributor.id(ORCID));

        raidApi.mintRaid(createRequest);

        final var api = testClient.contributorApi(raidAuToken);

        final var id = ORCID.substring(ORCID.lastIndexOf("/") + 1);

        try {
            final var raidList = api.findAllById(id).getBody();
            assert raidList != null;

            // find all raids in resultset that don't contain a contributor with the specified ORCID
            // there shouldn't be any
            final var erroneousRaids = raidList.stream()
                    .filter(raid -> !raid.getContributor().stream()
                            .map(Contributor::getId)
                            .toList()
                            .contains(ORCID))
                    .toList();

            assertThat(raidList).isNotEmpty();
            assertThat(erroneousRaids).isEmpty();
        } catch (RaidApiValidationException e) {
            fail(e.getMessage());
        }
    }

    private RaidUpdateRequest mapReadToUpdate(RaidDto read) {
        return new RaidUpdateRequest()
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