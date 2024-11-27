package au.org.raid.inttest;

import au.org.raid.idl.raidv2.model.Organisation;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import au.org.raid.inttest.service.RaidApiValidationException;
import au.org.raid.inttest.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ContributorControllerIntegrationTest extends AbstractIntegrationTest {
    private static final String ROR = "https://ror.org/038sjwq14";
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("List all raids for an orcid ")
    void listRaidsWithAGivenOrcid() {
        final var user = userService.createUser(
                "raid-au",
                "raid-searcher", "service-point-user"
        );

        try {
            createRequest.getOrganisation().forEach(organisation -> organisation.id(ROR));
            raidApi.mintRaid(createRequest);
            final var api = testClient.organisationApi(user.getToken());
            final var id = ROR.substring(ROR.lastIndexOf("/") + 1);
            final var raidList = api.findAllById(id).getBody();
            assert raidList != null;

            // find all raids in resultset that don't contain a contributor with the specified ORCID
            // there shouldn't be any
            final var erroneousRaids = raidList.stream()
                    .filter(raid -> !raid.getOrganisation().stream()
                            .map(Organisation::getId)
                            .toList()
                            .contains(ROR))
                    .toList();

            assertThat(raidList).isNotEmpty();
            assertThat(erroneousRaids).isEmpty();
        } catch (RaidApiValidationException e) {
            fail(e.getMessage());
        } finally {
            userService.deleteUser(user.getId());
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