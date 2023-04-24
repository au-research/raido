package raido.inttest.endpoint.raidv2;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import raido.apisvc.service.raid.ValidationFailureException;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;
import raido.apisvc.service.stub.util.IdFactory;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.apisvc.util.test.BddUtil.THEN;
import static raido.apisvc.util.test.BddUtil.WHEN;
import static raido.idl.raidv2.model.AccessType.CLOSED;
import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static raido.idl.raidv2.model.ContributorPositionSchemeType.HTTPS_RAID_ORG_;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION;
import static raido.idl.raidv2.model.ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static raido.inttest.endpoint.raidv1.LegacyRaidV1MintTest.INT_TEST_ID_URL;
import static raido.inttest.util.MinimalRaidTestData.DUMMY_ORCID;
import static raido.inttest.util.MinimalRaidTestData.DUMMY_ROR;

public class RaidoSchemaV1Test extends IntegrationTestCase {

  @Test
  void happyDayScenario() 
    throws JsonProcessingException, ValidationFailureException 
  {
    var raidApi = super.basicRaidExperimentalClient();
    String initialTitle = getClass().getSimpleName() + "." + getName() + 
      idFactory.generateUniqueId();
    var today = LocalDate.now();
    var idParser = new IdentifierParser();

    EXPECT("minting a raid with minimal content should succeed");
    var mintResult = raidApi.mintRaidoSchemaV1(
      new MintRaidoSchemaV1Request().
        mintRequest(new MintRaidoSchemaV1RequestMintRequest().
          servicePointId(RAIDO_SP_ID)).
        metadata(new RaidoMetadataSchemaV1().
          metadataSchema(RAIDOMETADATASCHEMAV1).
          titles(List.of(new TitleBlock().
            type(PRIMARY_TITLE).
            title(initialTitle).
            startDate(today))).
          dates(new DatesBlock().startDate(today)).
          descriptions(List.of(new DescriptionBlock().
            type(PRIMARY_DESCRIPTION).
            description("stuff about the int test raid"))).
          contributors(List.of(createDummyLeaderContributor(today))).
          organisations(List.of(createDummyOrganisation(today))).
          access(new AccessBlock().type(OPEN))
        )
    );
    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getFailures()).isNullOrEmpty();
    assertThat(mintResult.getSuccess()).isTrue();
    assertThat(mintResult.getRaid()).isNotNull();
    var mintedRaid = mintResult.getRaid();
    assertThat(mintedRaid.getHandle()).isNotBlank();
    assertThat(mintedRaid.getStartDate()).isNotNull();
    assertThat(mintedRaid.getMetadata()).isInstanceOf(String.class);
    var mintedMetadata = mapper.readValue(
      mintedRaid.getMetadata().toString(), RaidoMetadataSchemaV1.class);
    assertThat(mintedMetadata.getMetadataSchema()).
      isEqualTo(RAIDOMETADATASCHEMAV1);


    EXPECT("should be able to read the minted raid via authz api");
    var readResult = raidApi.readRaidV2(
      new ReadRaidV2Request().handle(mintedRaid.getHandle()));
    assertThat(readResult).isNotNull();


    EXPECT("should be able to read the minted raid via public api (v3)");
    var v3Read = raidoApi.getPublicExperimental().
      publicReadRaidV3(mintedRaid.getHandle());
    assertThat(v3Read).isNotNull();
    assertThat(v3Read.getCreateDate()).isNotNull();
    assertThat(v3Read.getServicePointId()).isEqualTo(RAIDO_SP_ID);

    assertThat(v3Read.getHandle()).isEqualTo(mintedRaid.getHandle());
    
    PublicReadRaidMetadataResponseV1 v3MetaRead = v3Read.getMetadata();
    assertThat(v3MetaRead.getMetadataSchema()).isEqualTo(
      PublicRaidMetadataSchemaV1.class.getSimpleName());
    assertThat(v3MetaRead).isInstanceOf(PublicRaidMetadataSchemaV1.class);
    PublicRaidMetadataSchemaV1 v3Meta = (PublicRaidMetadataSchemaV1) v3MetaRead; 
    assertThat(v3Meta.getId().getIdentifier()).
      isEqualTo(INT_TEST_ID_URL + "/" + mintedRaid.getHandle());
    var id = idParser.parseUrlWithException(v3Meta.getId().getIdentifier());
    assertThat(v3Read.getHandle()).isEqualTo(id.handle().format());
    
    assertThat(v3Meta.getAccess().getType()).isEqualTo(OPEN);
    assertThat(v3Meta.getTitles().get(0).getTitle()).
      isEqualTo(initialTitle);
    assertThat(v3Meta.getDescriptions().get(0).getDescription()).
      contains("stuff about the int test raid");


    /* list by unique name to prevent eventual pagination issues */
    EXPECT("should be able to list the minted raid");
    var listResult = raidApi.listRaidV2(new RaidListRequestV2().
      servicePointId(RAIDO_SP_ID).primaryTitle(initialTitle));
    assertThat(listResult).singleElement().satisfies(i->{
      assertThat(i.getHandle()).isEqualTo(mintedRaid.getHandle());
      assertThat(i.getPrimaryTitle()).isEqualTo(initialTitle);
      assertThat(i.getStartDate()).isEqualTo(LocalDate.now());
      assertThat(i.getCreateDate()).isNotNull();
    });

    
    WHEN("raid primaryTitle is updated");
    var readPrimaryTitle = v3Meta.getTitles().get(0);
    var newTitle = 
      readPrimaryTitle.title(readPrimaryTitle.getTitle()+" updated");
    var updateResult = raidApi.updateRaidoSchemaV1(
      new UpdateRaidoSchemaV1Request().metadata(
        mapRaidMetadataToRaido(v3Meta).
          titles(List.of(newTitle)) 
      ));
    assertThat(updateResult.getFailures()).isNullOrEmpty();
    assertThat(updateResult.getSuccess()).isTrue();

    THEN("should be able to read new value via publicRead");
    var readUpdatedData = (PublicRaidMetadataSchemaV1)
      raidoApi.getPublicExperimental().
        publicReadRaidV3(mintedRaid.getHandle()).getMetadata();
    
    assertThat(readUpdatedData.getAccess().getType()).isEqualTo(OPEN);
    assertThat(readUpdatedData.getTitles().get(0).getTitle()).isEqualTo(
      initialTitle + " updated" );

    
    WHEN("raid is updated to closed");
    var closeResult = raidApi.updateRaidoSchemaV1(
      new UpdateRaidoSchemaV1Request().metadata(
        mapRaidMetadataToRaido(readUpdatedData).
          access(
            readUpdatedData.getAccess().
              type(CLOSED).
              accessStatement("closed by update") )
      ));
    assertThat(closeResult.getFailures()).isNullOrEmpty();
    assertThat(closeResult.getSuccess()).isTrue();

    THEN("publicRaid should now return closed");
    var readClosed = raidoApi.getPublicExperimental().
      publicReadRaidV3(mintedRaid.getHandle());
    var readClosedMeta = (PublicClosedMetadataSchemaV1) 
      readClosed.getMetadata(); 
    assertThat(readClosedMeta.getAccess().getType()).isEqualTo(CLOSED);
    assertThat(readClosed.getHandle()).isEqualTo(id.handle().format());
    
    // IMPROVE: maybe we ought to have at least one non-feign test, that
    // tests the raw data returned for a closed raid, to make sure there's 
    // no mistakes with how the data mapping works.
    // Since this now uses a generated "Closed" class, how can we be *sure*
    // that our API doesn't leak stuff for closed raids?
//    AND("titles should not be returned");
//    assertThat(readClosedMeta.getTitles()).isNullOrEmpty();
    
  }

  public static ContributorBlock createDummyLeaderContributor(LocalDate today) {
    return new ContributorBlock().
      id(DUMMY_ORCID).
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

  public static OrganisationBlock createDummyOrganisation(LocalDate today) {
    return new OrganisationBlock().
      id(DUMMY_ROR).
      identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_).
      roles(List.of(
        new OrganisationRole().
          roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_).
          role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION)
          .startDate(today)));
  }

  public static RaidoMetadataSchemaV1 mapRaidMetadataToRaido(
    PublicRaidMetadataSchemaV1 in
  ){
    return new RaidoMetadataSchemaV1().
      metadataSchema(RAIDOMETADATASCHEMAV1).
      id(in.getId()).
      dates(in.getDates()).
      titles(in.getTitles()).
      descriptions(in.getDescriptions()).
      alternateUrls(in.getAlternateUrls()).
      contributors(in.getContributors()).
      organisations(in.getOrganisations()).
      access(in.getAccess());
  }

  @Test
  void validateMintEmptyPrimaryTitle() {
    var raidApi = super.basicRaidExperimentalClient();
    var today = LocalDate.now();

    WHEN("minting a raid with minimal content with empty primaryTitle");
    var mintResult = raidApi.mintRaidoSchemaV1(
      new MintRaidoSchemaV1Request().
        mintRequest(new MintRaidoSchemaV1RequestMintRequest().
          servicePointId(RAIDO_SP_ID)).
        metadata(new RaidoMetadataSchemaV1().
          metadataSchema(RAIDOMETADATASCHEMAV1).
          titles(List.of(new TitleBlock().
            type(PRIMARY_TITLE).
            title(" ").
            startDate(null))).
          contributors(List.of(createDummyLeaderContributor(today))).
          organisations(List.of(createDummyOrganisation(today))).
          dates(new DatesBlock().startDate(today)).
          access(new AccessBlock().type(OPEN))
        )
    );
    THEN("validation failure should result");
    assertThat(mintResult.getSuccess()).isFalse();
    assertThat(mintResult.getFailures()).satisfiesExactlyInAnyOrder(
      i->{
        assertThat(i.getFieldId()).isEqualTo("titles[0].title");
        assertThat(i.getErrorType()).isEqualTo("notSet");
      },
      i->{
        assertThat(i.getFieldId()).isEqualTo("titles[0].startDate");
        assertThat(i.getErrorType()).isEqualTo("notSet");
      }
    );
  }
}