package raido.loadtest.util;

import com.github.javafaker.Faker;
import raido.idl.raidv2.model.*;

import java.time.LocalDate;
import java.util.List;

import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static raido.idl.raidv2.model.ContributorPositionSchemeType.HTTPS_RAID_ORG_;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION;
import static raido.idl.raidv2.model.ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;

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
        createDummyLeaderContributor(RAID_PRODUCT_MANAGER, today) )).
      organisations(List.of(
        createDummyOrganisation(ARDC_ROR, today) )).
      access(new AccessBlock().type(OPEN));
  }

  public static ContributorBlock createDummyLeaderContributor(
    String orcid, 
    LocalDate today
  ) {
    return new ContributorBlock().
      id(orcid).
      identifierSchemeUri(HTTPS_ORCID_ORG_).
      positions(List.of(new ContributorPosition().
        positionSchemaUri(HTTPS_RAID_ORG_).
        position(LEADER).
        startDate(today))).
      roles(List.of(
        new ContributorRole().
          roleSchemeUri(HTTPS_CREDIT_NISO_ORG_).
          role(PROJECT_ADMINISTRATION)));
  }

  public static OrganisationBlock createDummyOrganisation(
    String ror,
    LocalDate today
  ) {
    return new OrganisationBlock().
      id(ror).
      identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_).
      roles(List.of(
        new OrganisationRole().
          roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_).
          role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION)
          .startDate(today)));
  }

}
