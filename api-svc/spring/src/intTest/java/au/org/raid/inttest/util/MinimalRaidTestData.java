package au.org.raid.inttest.util;

import au.org.raid.idl.raidv2.model.*;

import java.time.LocalDate;
import java.util.List;

import static au.org.raid.idl.raidv2.model.AccessType.OPEN;
import static au.org.raid.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;
import static au.org.raid.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static au.org.raid.idl.raidv2.model.ContributorPositionSchemeType.HTTPS_RAID_ORG_;
import static au.org.raid.idl.raidv2.model.ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_;
import static au.org.raid.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static au.org.raid.idl.raidv2.model.OrganisationRoleType.LEAD_RESEARCH_ORGANISATION;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static au.org.raid.inttest.endpoint.raidv2.RaidoSchemaV1Test.createDummyLeaderContributor;
import static java.util.List.of;

public class MinimalRaidTestData {
    public static String REAL_TEST_ORCID = "https://orcid.org/0000-0003-0635-1998";
    public static String REAL_TEST_ROR = "https://ror.org/015w2mp89";
    public static String REAL_TEST_DOI = "http://doi.org/10.47366/sabia.v5n1a3";


    public static RaidoMetadataSchemaV1 createMinimalSchemaV1(String title) {
        var today = LocalDate.now();

        return new RaidoMetadataSchemaV1().
                metadataSchema(RAIDOMETADATASCHEMAV1).
                titles(List.of(new TitleBlock().
                        type(PRIMARY_TITLE).
                        title(title).
                        startDate(today))).
                dates(new DatesBlock().startDate(today)).
                descriptions(List.of(new DescriptionBlock().
                        type(PRIMARY_DESCRIPTION).
                        description("stuff about the int test raid"))).
                contributors(List.of(createDummyLeaderContributor(today))).
                access(new AccessBlock().type(OPEN));
    }

    public static MintRaidoSchemaV1Request createMintRequest(
            RaidoMetadataSchemaV1 metadata,
            long servicePointId
    ) {
        return new MintRaidoSchemaV1Request().
                mintRequest(new MintRaidoSchemaV1RequestMintRequest().
                        servicePointId(servicePointId)).
                metadata(metadata);
    }

    public static TitleBlock title(
            String title,
            TitleType type,
            LocalDate startDate
    ) {
        return new TitleBlock().
                type(type).
                title(title).
                startDate(startDate);
    }

    public static List<TitleBlock> titles(
            String title
    ) {
        return of(new TitleBlock().
                type(PRIMARY_TITLE).
                title(title).
                startDate(LocalDate.now()));
    }

    public static DescriptionBlock description(
            String desc,
            DescriptionType type
    ) {
        return new DescriptionBlock().
                type(type).
                description(desc);
    }

    public static List<DescriptionBlock> descriptions(String desc) {
        return of(description(desc, PRIMARY_DESCRIPTION));
    }

    public static ContributorBlock contributor(
            String orcid,
            ContributorPositionRaidMetadataSchemaType position,
            ContributorRoleCreditNisoOrgType role,
            LocalDate startDate
    ) {
        return new ContributorBlock().
                id(orcid).
                identifierSchemeUri(HTTPS_ORCID_ORG_).
                positions(List.of(new ContributorPosition().
                        positionSchemaUri(HTTPS_RAID_ORG_).
                        position(position).
                        startDate(startDate))).
                roles(List.of(
                        new ContributorRole().
                                roleSchemeUri(HTTPS_CREDIT_NISO_ORG_).
                                role(role)));
    }

    public static List<ContributorBlock> contributors(
            String orcid
    ) {
        var today = LocalDate.now();
        return of(new ContributorBlock().
                id(orcid).
                identifierSchemeUri(HTTPS_ORCID_ORG_).
                positions(List.of(new ContributorPosition().
                        positionSchemaUri(HTTPS_RAID_ORG_).
                        position(LEADER).
                        startDate(today))).
                roles(List.of(
                        new ContributorRole().
                                roleSchemeUri(HTTPS_CREDIT_NISO_ORG_).
                                role(ContributorRoleCreditNisoOrgType.SUPERVISION))));
    }

    public static OrganisationBlock organisation(
            String ror,
            OrganisationRoleType role,
            LocalDate today
    ) {
        return new OrganisationBlock().
                id(ror).
                identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_).
                roles(List.of(
                        new OrganisationRole().
                                roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_).
                                role(role)
                                .startDate(today)));
    }

    public static List<OrganisationBlock> organisations(String ror) {
        return of(organisation(ror, LEAD_RESEARCH_ORGANISATION, LocalDate.now()));
    }

    public static RelatedObjectBlock relatedObject(String doi, String type) {
        return new RelatedObjectBlock().
                relatedObject(doi).
                relatedObjectSchemeUri("https://doi.org/").
                relatedObjectType(relatedObjectType(type)).
                relatedObjectTypeSchemeUri(
                        "https://github.com/au-research/raid-metadata" +
                                "/tree/main/scheme/related-object/related-object-type/").
                relatedObjectCategory("Input");
    }

    public static String relatedObjectType(String type) {
        return "https://github.com/au-research/raid-metadata" +
                "/blob/main/scheme/related-object/related-object-type/" +
                type;
    }

    public static List<RelatedObjectBlock> relatedObjects(String doi) {
        return of(relatedObject(doi, "conference-paper.json"));
    }

}