package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

public class BackwardsCompatabilityIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Read a raid created by V1 experimental api in api")
    void readExperimentalRaid() {
        final var today = LocalDate.now();
        try {
            final MintResponse mintResponse = experimentalApi.mintRaidoSchemaV1(TestData.mintRaidoSchemaV1Request(RAIDO_SP_ID));

            final var metadata = objectMapper.readValue((String)mintResponse.getRaid().getMetadata(), RaidoMetadataSchemaV1.class);

            final String[] split = mintResponse.getRaid().getHandle().split("/");
            final var prefix = split[0];
            final var suffix = split[1];

            final RaidDto raidDto = raidApi.readRaidV1(prefix, suffix);

            final var expected = new RaidDto()
                    .id(new Id()
                            .identifier(metadata.getId().getIdentifier())
                            .identifierSchemeUri(metadata.getId().getIdentifierSchemeURI())
                            .globalUrl(metadata.getId().getGlobalUrl())
                            .raidAgencyUrl(metadata.getId().getRaidAgencyUrl())
                            .identifierOwner(metadata.getId().getIdentifierOwner())
                            .identifierRegistrationAgency(metadata.getId().getIdentifierRegistrationAgency())
                            .version(metadata.getId().getVersion())
                            .identifierServicePoint(metadata.getId().getIdentifierServicePoint()))
                    .titles(List.of(
                            new Title()
                                    .title(metadata.getTitles().get(0).getTitle())
                                    .type(new TitleTypeWithSchemeUri()
                                            .id(TestConstants.PRIMARY_TITLE_TYPE)
                                            .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI))
                                    .startDate(metadata.getTitles().get(0).getStartDate())
                                    .endDate(metadata.getTitles().get(0).getEndDate()),
                            new Title()
                                    .title(metadata.getTitles().get(1).getTitle())
                                    .type(new TitleTypeWithSchemeUri()
                                            .id(TestConstants.ALTERNATIVE_TITLE_TYPE)
                                            .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI))
                                    .startDate(metadata.getTitles().get(1).getStartDate())
                                    .endDate(metadata.getTitles().get(1).getEndDate())))
                    .dates(new Dates()
                            .startDate(metadata.getDates().getStartDate())
                            .endDate(metadata.getDates().getEndDate()))
                    .descriptions(List.of(
                            new Description()
                                    .description(metadata.getDescriptions().get(0).getDescription())
                                    .type(new DescriptionTypeWithSchemeUri()
                                            .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                                            .schemeUri(TestConstants.DESCRIPTION_TYPE_SCHEME_URI)),
                            new Description()
                                    .description(metadata.getDescriptions().get(1).getDescription())
                                    .type(new DescriptionTypeWithSchemeUri()
                                            .id(TestConstants.ALTERNATIVE_DESCRIPTION_TYPE)
                                            .schemeUri(TestConstants.DESCRIPTION_TYPE_SCHEME_URI))))
                    .access(new Access()
                                    .type(new AccessTypeWithSchemeUri()
                                            .id(TestConstants.OPEN_ACCESS_TYPE)
                                            .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI)))
                    .alternateUrls(List.of(
                            new AlternateUrl().url(metadata.getAlternateUrls().get(0).getUrl()),
                            new AlternateUrl().url(metadata.getAlternateUrls().get(1).getUrl()),
                            new AlternateUrl().url(metadata.getAlternateUrls().get(2).getUrl())))
                    .contributors(List.of(
                            new Contributor()
                                    .id(metadata.getContributors().get(0).getId())
                                    .identifierSchemeUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEME_URI)
                                    .positions(List.of(
                                            new ContributorPositionWithSchemeUri()
                                                    .id(TestConstants.LEADER_POSITION)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_POSITION_SCHEME_URI)
                                                    .startDate(metadata.getContributors().get(0).getPositions().get(0).getStartDate())
                                                    .endDate(metadata.getContributors().get(0).getPositions().get(0).getEndDate()),
                                            new ContributorPositionWithSchemeUri()
                                                    .id(TestConstants.PRINCIPAL_INVESTIGATOR_POSITION)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_POSITION_SCHEME_URI)
                                                    .startDate(metadata.getContributors().get(0).getPositions().get(1).getStartDate())
                                                    .endDate(metadata.getContributors().get(0).getPositions().get(1).getEndDate())))
                                    .roles(List.of(
                                            new ContributorRoleWithSchemeUri()
                                                    .id(TestConstants.CONCEPTUALIZATION_CONTRIBUTOR_ROLE)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI),
                                            new ContributorRoleWithSchemeUri()
                                                    .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI))),
                            new Contributor()
                                    .id(metadata.getContributors().get(1).getId())
                                    .identifierSchemeUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEME_URI)
                                    .positions(List.of(
                                            new ContributorPositionWithSchemeUri()
                                                    .id(TestConstants.OTHER_PARTICIPANT_POSITION)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_POSITION_SCHEME_URI)
                                                    .startDate(metadata.getContributors().get(1).getPositions().get(0).getStartDate())
                                                    .endDate(metadata.getContributors().get(1).getPositions().get(0).getEndDate()),
                                            new ContributorPositionWithSchemeUri()
                                                    .id(TestConstants.CONTACT_PERSON_POSITION)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_POSITION_SCHEME_URI)
                                                    .startDate(metadata.getContributors().get(1).getPositions().get(1).getStartDate())
                                                    .endDate(metadata.getContributors().get(1).getPositions().get(1).getEndDate())))
                                    .roles(List.of(
                                            new ContributorRoleWithSchemeUri()
                                                    .id(TestConstants.WRITING_REVIEW_EDITING_CONTRIBUTOR_ROLE)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI),
                                            new ContributorRoleWithSchemeUri()
                                                    .id(TestConstants.DATA_CURATION_CONTRIBUTOR_ROLE)
                                                    .schemeUri(TestConstants.CONTRIBUTOR_ROLE_SCHEME_URI)))))
                    .organisations(List.of(
                            new Organisation()
                                    .id(metadata.getOrganisations().get(0).getId())
                                    .identifierSchemeUri(TestConstants.ORGANISATION_IDENTIFIER_SCHEME_URI)
                                    .roles(List.of(
                                            new OrganisationRoleWithSchemeUri()
                                                    .id(TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE)
                                                    .schemeUri(TestConstants.ORGANISATION_ROLE_SCHEME_URI)
                                                    .startDate(metadata.getOrganisations().get(0).getRoles().get(0).getStartDate())
                                                    .endDate(metadata.getOrganisations().get(0).getRoles().get(0).getEndDate()),
                                            new OrganisationRoleWithSchemeUri()
                                                    .id(TestConstants.PARTNER_ORGANISATION_ROLE)
                                                    .schemeUri(TestConstants.ORGANISATION_ROLE_SCHEME_URI)
                                                    .startDate(metadata.getOrganisations().get(0).getRoles().get(1).getStartDate())
                                                    .endDate(metadata.getOrganisations().get(0).getRoles().get(1).getEndDate()))),
                            new Organisation()
                                    .id(metadata.getOrganisations().get(1).getId())
                                    .identifierSchemeUri(TestConstants.ORGANISATION_IDENTIFIER_SCHEME_URI)
                                    .roles(List.of(
                                            new OrganisationRoleWithSchemeUri()
                                                    .id(TestConstants.OTHER_ORGANISATION_ROLE)
                                                    .schemeUri(TestConstants.ORGANISATION_ROLE_SCHEME_URI)
                                                    .startDate(metadata.getOrganisations().get(1).getRoles().get(0).getStartDate())
                                                    .endDate(metadata.getOrganisations().get(1).getRoles().get(0).getEndDate()),
                                            new OrganisationRoleWithSchemeUri()
                                                    .id(TestConstants.CONTRACTOR_ORGANISATION_ROLE)
                                                    .schemeUri(TestConstants.ORGANISATION_ROLE_SCHEME_URI)
                                                    .startDate(metadata.getOrganisations().get(1).getRoles().get(1).getStartDate())
                                                    .endDate(metadata.getOrganisations().get(1).getRoles().get(1).getEndDate())))))
                    .subjects(List.of(
                            new Subject()
                                    .id(metadata.getSubjects().get(0).getSubject())
                                    .schemeUri(metadata.getSubjects().get(0).getSubjectSchemeUri())
                                    .keyword(metadata.getSubjects().get(0).getSubjectKeyword()),
                            new Subject()
                                    .id(metadata.getSubjects().get(1).getSubject())
                                    .schemeUri(metadata.getSubjects().get(1).getSubjectSchemeUri())
                                    .keyword(metadata.getSubjects().get(1).getSubjectKeyword())))
                    .relatedObjects(List.of(
                            new RelatedObject()
                                    .id(metadata.getRelatedObjects().get(0).getRelatedObject())
                                    .identifierSchemeUri(metadata.getRelatedObjects().get(0).getRelatedObjectSchemeUri())
                                    .type(new RelatedObjectType()
                                            .id(metadata.getRelatedObjects().get(0).getRelatedObjectType())
                                            .schemeUri(metadata.getRelatedObjects().get(0).getRelatedObjectTypeSchemeUri()))
                                    .category(new RelatedObjectCategory()
                                            .id(TestConstants.INPUT_RELATED_OBJECT_CATEGORY)
                                            .schemeUri(TestConstants.RELATED_OBJECT_CATEGORY_SCHEME_URI)),
                            new RelatedObject()
                                    .id(metadata.getRelatedObjects().get(1).getRelatedObject())
                                    .identifierSchemeUri(metadata.getRelatedObjects().get(0).getRelatedObjectSchemeUri())
                                    .type(new RelatedObjectType()
                                            .id(metadata.getRelatedObjects().get(1).getRelatedObjectType())
                                            .schemeUri(metadata.getRelatedObjects().get(1).getRelatedObjectTypeSchemeUri()))
                                    .category(new RelatedObjectCategory()
                                            .id(TestConstants.OUTPUT_RELATED_OBJECT_CATEGORY)
                                            .schemeUri(TestConstants.RELATED_OBJECT_CATEGORY_SCHEME_URI))))
                    .alternateIdentifiers(List.of(
                            new AlternateIdentifier()
                                    .id(metadata.getAlternateIdentifiers().get(0).getAlternateIdentifier())
                                    .type(metadata.getAlternateIdentifiers().get(0).getAlternateIdentifierType()),
                            new AlternateIdentifier()
                                    .id(metadata.getAlternateIdentifiers().get(1).getAlternateIdentifier())
                                    .type(metadata.getAlternateIdentifiers().get(1).getAlternateIdentifierType())))
                    .spatialCoverages(List.of(
                            new SpatialCoverage()
                                    .id(metadata.getSpatialCoverages().get(0).getSpatialCoverage())
                                    .schemeUri(metadata.getSpatialCoverages().get(0).getSpatialCoverageSchemeUri())
                                    .place(metadata.getSpatialCoverages().get(0).getSpatialCoveragePlace()),
                            new SpatialCoverage()
                                    .id(metadata.getSpatialCoverages().get(1).getSpatialCoverage())
                                    .schemeUri(metadata.getSpatialCoverages().get(1).getSpatialCoverageSchemeUri())
                                    .place(metadata.getSpatialCoverages().get(1).getSpatialCoveragePlace())))
                    .traditionalKnowledgeLabels(List.of(
                            new TraditionalKnowledgeLabel()
                                    .schemeUri(metadata.getTraditionalKnowledgeLabels().get(0).getTraditionalKnowledgeLabelSchemeUri()),
                            new TraditionalKnowledgeLabel()
                                    .schemeUri(metadata.getTraditionalKnowledgeLabels().get(1).getTraditionalKnowledgeLabelSchemeUri())));

            assertThat(raidDto).isEqualTo(expected);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }


    @Test
    @DisplayName("Read a raid created by legacy api")
    void readLegacyRaid() {
        final var raidCreateModel = TestData.raidCreateModel();
        final var raidModel = legacyApi.rAiDPost(raidCreateModel);

        final String[] split = raidModel.getHandle().split("/");
        final var prefix = split[0];
        final var suffix = split[1];

        final RaidDto raidDto = raidApi.readRaidV1(prefix, suffix);

        assertThat(raidDto.getId()).isEqualTo(new Id()
                .identifier("http://localhost:8080/" + raidModel.getHandle())
                .globalUrl("https://hdl.handle.net/" + raidModel.getHandle())
                .raidAgencyUrl("http://localhost:8080/" + raidModel.getHandle())
                .identifierServicePoint(20000002L)
                .identifierSchemeUri("https://raid.org")
                .identifierRegistrationAgency("https://ror.org/038sjwq14")
                .identifierOwner("https://ror.org/00rqy9422")
                .version(1));

        assertThat(raidDto.getAccess()).isEqualTo(new Access()
                .type(new AccessTypeWithSchemeUri()
                        .id(TestConstants.CLOSED_ACCESS_TYPE)
                        .schemeUri(TestConstants.ACCESS_TYPE_SCHEME_URI)
                )
                .accessStatement(new AccessStatement().statement("RAiD minted via legacy V1 endpoint is closed by default")));

        assertThat(raidDto.getDates()).isEqualTo(new Dates()
                .startDate(LocalDate.parse(raidModel.getStartDate())));

        assertThat(raidDto.getTitles()).isEqualTo(List.of(
                new Title()
                        .startDate(LocalDate.parse(raidModel.getStartDate()))
                        .type(new TitleTypeWithSchemeUri()
                                .id(TestConstants.PRIMARY_TITLE_TYPE)
                                .schemeUri(TestConstants.TITLE_TYPE_SCHEME_URI))
                        .title(raidModel.getMeta().getName())));

        assertThat(raidDto.getDescriptions()).isEqualTo(List.of(
            new Description()
                    .type(new DescriptionTypeWithSchemeUri()
                            .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                            .schemeUri(TestConstants.DESCRIPTION_TYPE_SCHEME_URI))
                    .description(String.format("RAiD created by '%s' at '%s'", raidModel.getOwner(), raidModel.getCreationDate()))));
    }
}