package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.TestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.db.jooq.enums.UserRole.OPERATOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
@Disabled
public class BackwardsCompatabilityIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Read a raid created by V1 experimental api in api")
    void readExperimentalRaid() {
        final var today = LocalDate.now();
        try {
            final MintResponse mintResponse = experimentalApi.mintRaidoSchemaV1(TestData.mintRaidoSchemaV1Request(RAIDO_SP_ID)).getBody();

            final var metadata = objectMapper.readValue((String) mintResponse.getRaid().getMetadata(), RaidoMetadataSchemaV1.class);

            final String[] split = mintResponse.getRaid().getHandle().split("/");
            final var prefix = split[0];
            final var suffix = split[1];

            final RaidDto raidDto = raidApi.readRaidV1(prefix, suffix).getBody();

            final var expected = new RaidDto();
            expected.identifier(new Id()
                    .id(metadata.getId().getIdentifier())
                    .schemaUri(metadata.getId().getIdentifierSchemeURI())
                    .globalUrl(metadata.getId().getGlobalUrl())
                    .raidAgencyUrl(metadata.getId().getRaidAgencyUrl())
                    .owner(new Owner()
                            .id(metadata.getId().getIdentifierOwner())
                            .schemaUri("https://ror.org/")
                            .servicePoint(metadata.getId().getIdentifierServicePoint())
                    )
                    .registrationAgency(new RegistrationAgency()
                            .id(metadata.getId().getIdentifierRegistrationAgency())
                            .schemaUri("https://ror.org/"))
                    .license("Creative Commons CC-0")
                    .version(metadata.getId().getVersion()));
            expected.title(List.of(
                    new Title()
                            .text(metadata.getTitles().get(0).getTitle())
                            .type(new TitleTypeWithSchemaUri()
                                    .id(TestConstants.PRIMARY_TITLE_TYPE)
                                    .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI))
                            .startDate(metadata.getTitles().get(0).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                            .endDate(metadata.getTitles().get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                    new Title()
                            .text(metadata.getTitles().get(1).getTitle())
                            .type(new TitleTypeWithSchemaUri()
                                    .id(TestConstants.ALTERNATIVE_TITLE_TYPE)
                                    .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI))
                            .startDate(metadata.getTitles().get(1).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
            ));
            expected.date(new Date()
                    .startDate(metadata.getDates().getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                    .endDate(metadata.getDates().getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            expected.description(List.of(
                    new Description()
                            .text(metadata.getDescriptions().get(0).getDescription())
                            .type(new DescriptionTypeWithSchemaUri()
                                    .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                                    .schemaUri(TestConstants.DESCRIPTION_TYPE_SCHEMA_URI)),
                    new Description()
                            .text(metadata.getDescriptions().get(1).getDescription())
                            .type(new DescriptionTypeWithSchemaUri()
                                    .id(TestConstants.ALTERNATIVE_DESCRIPTION_TYPE)
                                    .schemaUri(TestConstants.DESCRIPTION_TYPE_SCHEMA_URI))));
            expected.access(new Access()
                    .type(new AccessTypeWithSchemaUri()
                            .id(TestConstants.OPEN_ACCESS_TYPE)
                            .schemaUri(TestConstants.ACCESS_TYPE_SCHEMA_URI)));
            expected.alternateUrl(List.of(
                    new AlternateUrl().url(metadata.getAlternateUrls().get(0).getUrl()),
                    new AlternateUrl().url(metadata.getAlternateUrls().get(1).getUrl()),
                    new AlternateUrl().url(metadata.getAlternateUrls().get(2).getUrl())));
            expected.contributor(List.of(
                    new Contributor()
                            .id(metadata.getContributors().get(0).getId())
                            .schemaUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .position(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .id(TestConstants.LEADER_POSITION)
                                            .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(metadata.getContributors().get(0).getPositions().get(0).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getContributors().get(0).getPositions().get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                                    new ContributorPositionWithSchemaUri()
                                            .id(TestConstants.PRINCIPAL_INVESTIGATOR_POSITION)
                                            .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(metadata.getContributors().get(0).getPositions().get(1).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getContributors().get(0).getPositions().get(1).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))
                            .role(List.of(
                                    new ContributorRoleWithSchemaUri()
                                            .id(TestConstants.CONCEPTUALIZATION_CONTRIBUTOR_ROLE)
                                            .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI),
                                    new ContributorRoleWithSchemaUri()
                                            .id(TestConstants.SUPERVISION_CONTRIBUTOR_ROLE)
                                            .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI))),
                    new Contributor()
                            .id(metadata.getContributors().get(1).getId())
                            .schemaUri(TestConstants.CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                            .position(List.of(
                                    new ContributorPositionWithSchemaUri()
                                            .id(TestConstants.OTHER_PARTICIPANT_POSITION)
                                            .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(metadata.getContributors().get(1).getPositions().get(0).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getContributors().get(1).getPositions().get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                                    new ContributorPositionWithSchemaUri()
                                            .id(TestConstants.CONTACT_PERSON_POSITION)
                                            .schemaUri(TestConstants.CONTRIBUTOR_POSITION_SCHEMA_URI)
                                            .startDate(metadata.getContributors().get(1).getPositions().get(1).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getContributors().get(1).getPositions().get(1).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))
                            .role(List.of(
                                    new ContributorRoleWithSchemaUri()
                                            .id(TestConstants.WRITING_REVIEW_EDITING_CONTRIBUTOR_ROLE)
                                            .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI),
                                    new ContributorRoleWithSchemaUri()
                                            .id(TestConstants.DATA_CURATION_CONTRIBUTOR_ROLE)
                                            .schemaUri(TestConstants.CONTRIBUTOR_ROLE_SCHEMA_URI)))));
            expected.organisation(List.of(
                    new Organisation()
                            .id(metadata.getOrganisations().get(0).getId())
                            .schemaUri(TestConstants.ORGANISATION_IDENTIFIER_SCHEMA_URI)
                            .role(List.of(
                                    new OrganisationRoleWithSchemaUri()
                                            .id(TestConstants.LEAD_RESEARCH_ORGANISATION_ROLE)
                                            .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                                            .startDate(metadata.getOrganisations().get(0).getRoles().get(0).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getOrganisations().get(0).getRoles().get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                                    new OrganisationRoleWithSchemaUri()
                                            .id(TestConstants.PARTNER_ORGANISATION_ROLE)
                                            .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                                            .startDate(metadata.getOrganisations().get(0).getRoles().get(1).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getOrganisations().get(0).getRoles().get(1).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)))),
                    new Organisation()
                            .id(metadata.getOrganisations().get(1).getId())
                            .schemaUri(TestConstants.ORGANISATION_IDENTIFIER_SCHEMA_URI)
                            .role(List.of(
                                    new OrganisationRoleWithSchemaUri()
                                            .id(TestConstants.OTHER_ORGANISATION_ROLE)
                                            .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                                            .startDate(metadata.getOrganisations().get(1).getRoles().get(0).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getOrganisations().get(1).getRoles().get(0).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE)),
                                    new OrganisationRoleWithSchemaUri()
                                            .id(TestConstants.CONTRACTOR_ORGANISATION_ROLE)
                                            .schemaUri(TestConstants.ORGANISATION_ROLE_SCHEMA_URI)
                                            .startDate(metadata.getOrganisations().get(1).getRoles().get(1).getStartDate().format(DateTimeFormatter.ISO_LOCAL_DATE))
                                            .endDate(metadata.getOrganisations().get(1).getRoles().get(1).getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE))))));
            expected.subject(List.of(
                    new Subject()
                            .id(metadata.getSubjects().get(0).getSubject())
                            .schemaUri(metadata.getSubjects().get(0).getSubjectSchemeUri())
                            .keyword(List.of(new SubjectKeyword().text(metadata.getSubjects().get(0).getSubjectKeyword()))),
                    new Subject()
                            .id(metadata.getSubjects().get(1).getSubject())
                            .schemaUri(metadata.getSubjects().get(1).getSubjectSchemeUri())
                            .keyword(List.of(new SubjectKeyword().text(metadata.getSubjects().get(1).getSubjectKeyword())))));
            expected.relatedObject(List.of(
                    new RelatedObject()
                            .id(metadata.getRelatedObjects().get(0).getRelatedObject())
                            .schemaUri(metadata.getRelatedObjects().get(0).getRelatedObjectSchemeUri())
                            .type(new RelatedObjectType()
                                    .id(metadata.getRelatedObjects().get(0).getRelatedObjectType())
                                    .schemaUri(metadata.getRelatedObjects().get(0).getRelatedObjectTypeSchemeUri()))
                            .category(List.of(new RelatedObjectCategory()
                                    .id(TestConstants.INPUT_RELATED_OBJECT_CATEGORY)
                                    .schemaUri(TestConstants.RELATED_OBJECT_CATEGORY_SCHEMA_URI))),
                    new RelatedObject()
                            .id(metadata.getRelatedObjects().get(1).getRelatedObject())
                            .schemaUri(metadata.getRelatedObjects().get(0).getRelatedObjectSchemeUri())
                            .type(new RelatedObjectType()
                                    .id(metadata.getRelatedObjects().get(1).getRelatedObjectType())
                                    .schemaUri(metadata.getRelatedObjects().get(1).getRelatedObjectTypeSchemeUri()))
                            .category(List.of(new RelatedObjectCategory()
                                    .id(TestConstants.OUTPUT_RELATED_OBJECT_CATEGORY)
                                    .schemaUri(TestConstants.RELATED_OBJECT_CATEGORY_SCHEMA_URI)))));
            expected.alternateIdentifier(List.of(
                    new AlternateIdentifier()
                            .id(metadata.getAlternateIdentifiers().get(0).getAlternateIdentifier())
                            .type(metadata.getAlternateIdentifiers().get(0).getAlternateIdentifierType()),
                    new AlternateIdentifier()
                            .id(metadata.getAlternateIdentifiers().get(1).getAlternateIdentifier())
                            .type(metadata.getAlternateIdentifiers().get(1).getAlternateIdentifierType())));
            expected.spatialCoverage(List.of(
                    new SpatialCoverage()
                            .id(metadata.getSpatialCoverages().get(0).getSpatialCoverage())
                            .schemaUri(metadata.getSpatialCoverages().get(0).getSpatialCoverageSchemeUri())
                            .place(List.of(new SpatialCoveragePlace()
                                    .text(metadata.getSpatialCoverages().get(0).getSpatialCoveragePlace()))),
                    new SpatialCoverage()
                            .id(metadata.getSpatialCoverages().get(1).getSpatialCoverage())
                            .schemaUri(metadata.getSpatialCoverages().get(1).getSpatialCoverageSchemeUri())
                            .place(List.of(
                                    new SpatialCoveragePlace()
                                            .text(metadata.getSpatialCoverages().get(1).getSpatialCoveragePlace())
                            ))));
            expected.traditionalKnowledgeLabel(List.of(
                    new TraditionalKnowledgeLabel()
                            .schemaUri(metadata.getTraditionalKnowledgeLabels().get(0).getTraditionalKnowledgeLabelSchemeUri()),
                    new TraditionalKnowledgeLabel()
                            .schemaUri(metadata.getTraditionalKnowledgeLabels().get(1).getTraditionalKnowledgeLabelSchemeUri())));

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


        final var token = bootstrapTokenSvc.bootstrapToken(
                UQ_SERVICE_POINT_ID, "RdmUqApiToken", OPERATOR);

        final var raidApi = testClient.raidApi(token);

        final RaidDto raidDto = raidApi.readRaidV1(prefix, suffix).getBody();

        assertThat(raidDto.getIdentifier()).isEqualTo(new Id()
                .id("http://localhost:8080/" + raidModel.getHandle())
                .globalUrl("https://hdl.handle.net/" + raidModel.getHandle())
                .raidAgencyUrl("http://localhost:8080/" + raidModel.getHandle())
                .schemaUri("https://raid.org/")
                .registrationAgency(new RegistrationAgency()
                        .id("https://ror.org/038sjwq14")
                        .schemaUri("https://ror.org/"))
                .owner(new Owner()
                        .id("https://ror.org/00rqy9422")
                        .schemaUri("https://ror.org/")
                        .servicePoint(20000002L))
                .license("Creative Commons CC-0")
                .version(1));

        assertThat(raidDto.getAccess()).isEqualTo(new Access()
                .type(new AccessTypeWithSchemaUri()
                        .id(TestConstants.CLOSED_ACCESS_TYPE)
                        .schemaUri(TestConstants.ACCESS_TYPE_SCHEMA_URI)
                )
                .statement(new AccessStatement().text("RAiD minted via legacy V1 endpoint is closed by default")));

        assertThat(raidDto.getDate()).isEqualTo(new Date()
                .startDate(raidModel.getStartDate()));

        assertThat(raidDto.getTitle()).isEqualTo(List.of(
                new Title()
                        .startDate(raidModel.getStartDate())
                        .type(new TitleTypeWithSchemaUri()
                                .id(TestConstants.PRIMARY_TITLE_TYPE)
                                .schemaUri(TestConstants.TITLE_TYPE_SCHEMA_URI))
                        .text(raidModel.getMeta().getName())));

        assertThat(raidDto.getDescription()).isEqualTo(List.of(
                new Description()
                        .type(new DescriptionTypeWithSchemaUri()
                                .id(TestConstants.PRIMARY_DESCRIPTION_TYPE)
                                .schemaUri(TestConstants.DESCRIPTION_TYPE_SCHEMA_URI))
                        .text(String.format("RAiD created by '%s' at '%s'", raidModel.getOwner(), raidModel.getCreationDate()))));
    }
}