package raido.inttest;

import net.bytebuddy.utility.RandomString;
import raido.idl.raidv2.model.*;

import java.time.LocalDate;
import java.util.List;

import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.DescriptionType.ALTERNATIVE_DESCRIPTION;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.ALTERNATIVE_TITLE;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;

public class TestData {
    public static RaidoMetadataSchemaV1 raidoMetadataSchemaV1() {
        var today = LocalDate.now();
        return new RaidoMetadataSchemaV1()
            .metadataSchema(RAIDOMETADATASCHEMAV1)
            .titles(List.of(
                    new TitleBlock()
                        .type(PRIMARY_TITLE)
                        .title(RandomString.make(12))
                        .startDate(today.minusYears(2))
                        .endDate(today.minusYears(1)),
                    new TitleBlock()
                        .type(ALTERNATIVE_TITLE)
                        .title(RandomString.make(12))
                        .startDate(today)
                )
            )
            .dates(new DatesBlock()
                .startDate(today.minusYears(3))
                .endDate(today.minusYears(2)))
            .descriptions(List.of(
                new DescriptionBlock()
                    .type(PRIMARY_DESCRIPTION)
                    .description(RandomString.make(50)),
                new DescriptionBlock()
                    .type(ALTERNATIVE_DESCRIPTION)
                    .description(RandomString.make(50))
            ))
            .access(new AccessBlock().type(OPEN))
            .alternateUrls(List.of(
                new AlternateUrlBlock()
                    .url(RandomString.make()),
                new AlternateUrlBlock()
                    .url(RandomString.make()),
                new AlternateUrlBlock()
                    .url(RandomString.make())
            ))
            .contributors(List.of(
                new ContributorBlock()
                    .id("https://orcid.org/0000-0002-6492-9025")
                    .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_)
                    .positions(List.of(
                        new ContributorPosition()
                            .startDate(today.minusYears(3))
                            .endDate(today.minusYears(2))
                            .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_)
                            .position(ContributorPositionRaidMetadataSchemaType.LEADER),
                        new ContributorPosition()
                            .startDate(today.minusYears(3))
                            .endDate(today.minusYears(2))
                            .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_)
                            .position(ContributorPositionRaidMetadataSchemaType.PRINCIPAL_INVESTIGATOR)
                    ))
                    .roles(List.of(
                        new ContributorRole()
                            .role(ContributorRoleCreditNisoOrgType.CONCEPTUALIZATION)
                            .roleSchemeUri(ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_),
                        new ContributorRole()
                            .role(ContributorRoleCreditNisoOrgType.SUPERVISION)
                            .roleSchemeUri(ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_)
                    )),
                new ContributorBlock()
                    .id("https://orcid.org/0000-0003-0635-1998")
                    .identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_)
                    .positions(List.of(
                        new ContributorPosition()
                            .startDate(today.minusYears(2))
                            .endDate(today.minusYears(1))
                            .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_)
                            .position(ContributorPositionRaidMetadataSchemaType.OTHER_PARTICIPANT),
                        new ContributorPosition()
                            .startDate(today.minusYears(2))
                            .endDate(today.minusYears(1))
                            .positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_)
                            .position(ContributorPositionRaidMetadataSchemaType.CONTACT_PERSON)
                    ))
                    .roles(List.of(
                        new ContributorRole()
                            .role(ContributorRoleCreditNisoOrgType.WRITING_REVIEW_EDITING)
                            .roleSchemeUri(ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_),
                        new ContributorRole()
                            .role(ContributorRoleCreditNisoOrgType.DATA_CURATION)
                            .roleSchemeUri(ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_)
                    ))
            ))
            .organisations(List.of(
                new OrganisationBlock()
                    .id("https://ror.org/01fykd523")
                    .identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_)
                    .roles(
                        List.of(
                            new OrganisationRole()
                                .startDate(today.minusYears(4))
                                .endDate(today.minusYears(3))
                                .role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION),
                            new OrganisationRole()
                                .startDate(today.minusYears(4))
                                .endDate(today.minusYears(3))
                                .role(OrganisationRoleType.PARTNER_ORGANISATION)
                        )),
                new OrganisationBlock()
                    .id("https://ror.org/04d4fer41")
                    .identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_)
                    .roles(
                        List.of(
                            new OrganisationRole()
                                .startDate(today.minusYears(4))
                                .endDate(today.minusYears(3))
                                .role(OrganisationRoleType.OTHER_ORGANISATION),
                            new OrganisationRole()
                                .startDate(today.minusYears(4))
                                .endDate(today.minusYears(3))
                                .role(OrganisationRoleType.CONTRACTOR)
                        ))
            ))
            .subjects(List.of(
                new SubjectBlock()
                    .subject("https://linked.data.gov.au/def/anzsrc-for/2020/4301")
                    .subjectSchemeUri("https://linked.data.gov.au/def/anzsrc-for/2020/")
                    .subjectKeyword("Archeology"),
                new SubjectBlock()
                    .subject("https://linked.data.gov.au/def/anzsrc-for/2020/430101")
                    .subjectSchemeUri("https://linked.data.gov.au/def/anzsrc-for/2020/")
                    .subjectKeyword("Archeological Science")
            ))
            .relatedObjects(List.of(
                new RelatedObjectBlock()
                    .relatedObject("https://doi.org/10.47366/sabia.v5n1a3")
                    .relatedObjectSchemeUri("https://doi.org/")
                    .relatedObjectType("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/journal-article.json")
                    .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                    .relatedObjectCategory("Input"),
                new RelatedObjectBlock()
                    .relatedObject("https://doi.org/10.1000/182")
                    .relatedObjectSchemeUri("https://doi.org/")
                    .relatedObjectType("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/type/v1/audiovisual.json")
                    .relatedObjectTypeSchemeUri("https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/")
                    .relatedObjectCategory("output")
            ))
            .alternateIdentifiers(List.of(
                new AlternateIdentifierBlock()
                    .alternateIdentifierType("Other")
                    .alternateIdentifier("Blah"),
                new AlternateIdentifierBlock()
                    .alternateIdentifierType("Alternate")
                    .alternateIdentifier("Blof")
            ))
            .spatialCoverages(List.of(
                new SpatialCoverageBlock()
                    .spatialCoverage("https://www.geonames.org/264371/athens.html")
                    .spatialCoverageSchemeUri("https://www.geonames.org/")
                    .spatialCoveragePlace("Athens"),
                new SpatialCoverageBlock()
                    .spatialCoverage("https://www.geonames.org/2158177/melbourne.html")
                    .spatialCoverageSchemeUri("https://www.geonames.org/")
                    .spatialCoveragePlace("Melbourne")
            ))
            .traditionalKnowledgeLabels(List.of(
                new TraditionalKnowledgeLabelBlock()
                    .traditionalKnowledgeLabelSchemeUri("https://localcontexts.org/labels/traditional-knowledge-labels/"),
                new TraditionalKnowledgeLabelBlock()
                    .traditionalKnowledgeLabelSchemeUri("https://localcontexts.org/labels/biocultural-labels/")
            ));
    }

    public static MintRaidoSchemaV1Request mintRaidoSchemaV1Request(
        final long servicePointId,
        final RaidoMetadataSchemaV1 metadata) {

        return new MintRaidoSchemaV1Request().
            mintRequest(new MintRaidoSchemaV1RequestMintRequest().
                servicePointId(servicePointId)).
            metadata(metadata);
    }

    public static MintRaidoSchemaV1Request mintRaidoSchemaV1Request(
        final long servicePointId) {

        return new MintRaidoSchemaV1Request().
            mintRequest(new MintRaidoSchemaV1RequestMintRequest().
                servicePointId(servicePointId)).
            metadata(raidoMetadataSchemaV1());
    }
}