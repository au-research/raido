package au.org.raid.inttest.endpoint.raidv2;

import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.IntegrationTestCase;
import au.org.raid.inttest.RaidApiValidationException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static au.org.raid.api.service.stub.InMemoryStubTestData.*;
import static au.org.raid.api.test.util.BddUtil.EXPECT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InvalidPidTest extends IntegrationTestCase {
    private static final String LEAD_RESEARCH_ORGANISATION =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/lead-research-organisation.json";

    private static final String ORGANISATION_ROLE_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1";

    private static final String ORGANISATION_SCHEME_URI =
            "https://ror.org/";

    private static final String ACCESS_TYPE_OPEN =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json";

    private static final String ACCESS_TYPE_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1";
    private static final String PRIMARY_TITLE_TYPE =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";

    private static final String TITLE_TYPE_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";

    private static final String PRIMARY_DESCRIPTION_TYPE =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json";

    private static final String DESCRIPTION_TYPE_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1";

    private static final String CONTRIBUTOR_SCHEME_URI =
            "https://orcid.org/";

    private static final String CONTRIBUTOR_POSITION_SCHEME_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1";

    private static final String LEADER_POSITION =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json";

    private static final String SUPERVISION_ROLE = "https://credit.niso.org/contributor-roles/supervision/";

    private static final String CONTRIBUTOR_ROLE_SCHEME_URI = "https://credit.niso.org/";

    public static List<Description> descriptions(String description) {
        final var descriptionType = new DescriptionTypeWithSchemaUri()
                .id(PRIMARY_DESCRIPTION_TYPE)
                .schemaUri(DESCRIPTION_TYPE_SCHEME_URI);

        return List.of(new Description()
                .type(descriptionType)
                .text(description)
        );
    }

    @Test
    void mintWithNonExistentPidsShouldFail() {
        var raidApi = basicRaidStableClient();
        String initialTitle = getClass().getSimpleName() + "." + getName() +
                idFactory.generateUniqueId();
        var today = LocalDate.now();

        EXPECT("minting a raid with non-existent PIDs should fail");
        assertThatThrownBy(() -> raidApi.createRaidV1(new CreateRaidV1Request()
                .title(titles(initialTitle))
                .date(new Dates().startDate(today.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .description(descriptions("used for testing non-existent pids"))
                .contributor(contributors(NONEXISTENT_TEST_ORCID))
                .organisation(organisations(NONEXISTENT_TEST_ROR))
                .relatedObject(relatedObjects(NONEXISTENT_TEST_DOI))
                .access(new Access()
                        .type(new AccessTypeWithSchemaUri()
                                .id(ACCESS_TYPE_OPEN)
                                .schemaUri(ACCESS_TYPE_SCHEME_URI))
                )
        )).isInstanceOfSatisfying(RaidApiValidationException.class, ex -> {
            assertThat(ex.getFailures()).anySatisfy(iFail -> {
                assertThat(iFail.getFieldId()).isEqualTo("contributors[0].id");
                assertThat(iFail.getMessage()).contains("uri not found");
            });
            assertThat(ex.getFailures()).anySatisfy(iFail -> {
                assertThat(iFail.getFieldId()).isEqualTo("organisations[0].id");
                assertThat(iFail.getMessage()).contains("uri not found");
            });
            assertThat(ex.getFailures()).anySatisfy(iFail -> {
                assertThat(iFail.getFieldId()).isEqualTo("relatedObjects[0].id");
                assertThat(iFail.getMessage()).contains("uri not found");
            });
        });

    }

    public List<Title> titles(
            String title
    ) {
        return List.of(new Title()
                .type(new TitleTypeWithSchemaUri()
                        .id(PRIMARY_TITLE_TYPE)
                        .schemaUri(TITLE_TYPE_SCHEME_URI)
                )
                .text(title)
                .startDate(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)));
    }

    public List<Contributor> contributors(
            String orcid
    ) {
        var today = LocalDate.now();
        return List.of(new Contributor()
                .id(orcid)
                .schemaUri(CONTRIBUTOR_SCHEME_URI)
                .position(List.of(new ContributorPositionWithSchemaUri()
                        .schemaUri(CONTRIBUTOR_POSITION_SCHEME_URI)
                        .id(LEADER_POSITION)
                        .startDate(today.format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .role(List.of(
                        new ContributorRoleWithSchemaUri()
                                .schemaUri(CONTRIBUTOR_ROLE_SCHEME_URI)
                                .id(SUPERVISION_ROLE))));
    }

    public List<Organisation> organisations(String ror) {
        return List.of(organisation(ror, LEAD_RESEARCH_ORGANISATION, LocalDate.now()));
    }

    public Organisation organisation(
            String ror,
            String role,
            LocalDate today
    ) {
        return new Organisation()
                .id(ror)
                .schemaUri(ORGANISATION_SCHEME_URI)
                .role(List.of(
                        new OrganisationRoleWithSchemaUri()
                                .schemaUri(ORGANISATION_ROLE_SCHEME_URI)
                                .id(role)
                                .startDate(today.format(DateTimeFormatter.ISO_LOCAL_DATE))));
    }

    public List<RelatedObject> relatedObjects(String doi) {
        return List.of(relatedObject(doi, "conference-paper.json"));
    }

    public RelatedObject relatedObject(String doi, String type) {
        return new RelatedObject()
                .id(doi)
                .schemaUri("https://doi.org/")
                .type(
                        new RelatedObjectType()
                                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/audiovisual.json")
                                .schemaUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1")
                )
                .category(
                        new RelatedObjectCategory()
                                .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/input.json")
                                .schemaUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/category/v1")
                );
    }
}