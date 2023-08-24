package raido.loadtest.util;

import au.org.raid.idl.raidv2.model.*;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.List;

import static au.org.raid.idl.raidv2.model.AccessType.OPEN;
import static au.org.raid.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;
import static au.org.raid.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static au.org.raid.idl.raidv2.model.ContributorPositionSchemeType.HTTPS_RAID_ORG_;
import static au.org.raid.idl.raidv2.model.ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION;
import static au.org.raid.idl.raidv2.model.ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_;
import static au.org.raid.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static au.org.raid.idl.raidv2.model.OrganisationRoleType.LEAD_RESEARCH_ORGANISATION;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.TitleType.PRIMARY_TITLE;

public class RaidoMetadata {
  public static final String RAID_PRODUCT_MANAGER = 
    "https://orcid.org/0000-0002-6492-9025";
  public static final String ARDC_ROR = "https://ror.org/038sjwq14";


  public static RaidoMetadataSchemaV1 createRaidoMetadata(
    ){
    var today = LocalDate.now();

    /* not a static because I don't think it's thread-safe
    https://github.com/DiUS/java-faker/issues/465 */
    var faker = new Faker();
    
    return new RaidoMetadataSchemaV1().
      metadataSchema(RAIDOMETADATASCHEMAV1).
      titles(List.of(new TitleBlock().
        type(PRIMARY_TITLE).
        title(faker.book().title()).
        startDate(today))).
      dates(new DatesBlock().startDate(today)).
      descriptions(List.of(new DescriptionBlock().
        type(PRIMARY_DESCRIPTION).
        description(faker.ancient().primordial()))).
      contributors(List.of(
        createContributor(RAID_PRODUCT_MANAGER,
          LEADER, PROJECT_ADMINISTRATION, today ))).
      organisations(List.of(
        createDummyOrganisation(
          ARDC_ROR, LEAD_RESEARCH_ORGANISATION, today ))).
      access(new AccessBlock().type(OPEN));
  }

  public static ContributorBlock createContributor(
    String orcid,
    ContributorPositionRaidMetadataSchemaType position,
    ContributorRoleCreditNisoOrgType role,
    LocalDate today
  ) {
    return new ContributorBlock().
      id(orcid).
      identifierSchemeUri(HTTPS_ORCID_ORG_).
      positions(List.of(new ContributorPosition().
        positionSchemaUri(HTTPS_RAID_ORG_).
        position(position).
        startDate(today))).
      roles(List.of(
        new ContributorRole().
          roleSchemeUri(HTTPS_CREDIT_NISO_ORG_).
          role(role)));
  }

  public static OrganisationBlock createDummyOrganisation(
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

}