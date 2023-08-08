package raido.inttest.endpoint.raidv2;

import com.fasterxml.jackson.core.JsonProcessingException;
import feign.FeignException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import raido.apisvc.service.raid.ValidationFailureException;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.util.test.BddUtil.*;
import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static raido.idl.raidv2.model.ContributorPositionSchemeType.HTTPS_RAID_ORG_;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION;
import static raido.idl.raidv2.model.ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.RaidoMetaschemaV2.RAIDOMETADATASCHEMAV2;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static raido.inttest.endpoint.raidv1.LegacyRaidV1MintTest.INT_TEST_ID_URL;
import static raido.inttest.util.MinimalRaidTestData.REAL_TEST_ORCID;
import static raido.inttest.util.MinimalRaidTestData.REAL_TEST_ROR;

public class RaidoSchemaV2Test extends IntegrationTestCase {
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

    WHEN("raid primaryTitle is updated");
    var readMetadata = mapper.readValue((String) readResult.getMetadata(), RaidoMetadataSchemaV2.class);
    var readPrimaryTitle = readMetadata.getTitles().get(0);
    var newTitle =
      readPrimaryTitle.title(readPrimaryTitle.getTitle()+" updated");
    var update1 = raidApi.updateRaidoSchemaV2(
      new UpdateRaidoSchemaV2Request().metadata(
        mapRaidMetadataToRaido(readMetadata).
          titles(List.of(newTitle))
      ));
    assertThat(update1.getFailures()).isNullOrEmpty();
    assertThat(update1.getSuccess()).isTrue();

    THEN("version and schema version should be updated");
    var update1Read = raidApi.readRaidV2(new ReadRaidV2Request().handle(mintedRaid.getHandle()));
    var update1Metadata = mapper.readValue((String) update1Read.getMetadata(), RaidoMetadataSchemaV2.class);
    assertThat(update1Metadata.getAccess().getType()).isEqualTo(OPEN);
    assertThat(update1Metadata.getTitles().get(0).getTitle()).isEqualTo(
      initialTitle + " updated" );
    assertThat(update1Metadata.getMetadataSchema()).isEqualTo(RAIDOMETADATASCHEMAV2);

    WHEN("raid description is updated again");
    var existingDescription = update1Metadata.getDescriptions().get(0);
    var newDescription =
            existingDescription.description(existingDescription.getDescription() + " updated");

    var update2 = raidApi.updateRaidoSchemaV2(
            new UpdateRaidoSchemaV2Request().metadata(
                    mapRaidMetadataToRaido(update1Metadata).
                            descriptions(List.of(newDescription))
            ));
    assertThat(update2.getFailures()).isNullOrEmpty();
    assertThat(update2.getSuccess()).isTrue();

    THEN("version is incremented");
    var update2Read = raidApi.readRaidV2(new ReadRaidV2Request().handle(mintedRaid.getHandle()));
    var update2Metadata = mapper.readValue((String) update2Read.getMetadata(), RaidoMetadataSchemaV2.class);
    assertThat(update2Metadata.getAccess().getType()).isEqualTo(OPEN);
    assertThat(update2Metadata.getTitles().get(0).getTitle()).isEqualTo(
            initialTitle + " updated" );
    assertThat(update2Metadata.getMetadataSchema()).isEqualTo(RAIDOMETADATASCHEMAV2);
    assertThat(update2Metadata.getId().getVersion()).isEqualTo(3);

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
      isEqualTo(newTitle.getTitle());
    assertThat(v3Meta.getDescriptions().get(0).getDescription()).
      contains("stuff about the int test raid");
  }

  @Test
  @Disabled
  void updateFailsWithStaleVersion()
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

    WHEN("raid primaryTitle is updated");
    var readMetadata = mapper.readValue((String) readResult.getMetadata(), RaidoMetadataSchemaV2.class);
    var readPrimaryTitle = readMetadata.getTitles().get(0);
    var newTitle =
            readPrimaryTitle.title(readPrimaryTitle.getTitle()+" updated");
    var update1 = raidApi.updateRaidoSchemaV2(
            new UpdateRaidoSchemaV2Request().metadata(
                    mapRaidMetadataToRaido(readMetadata).
                            titles(List.of(newTitle))
            ));
    assertThat(update1.getFailures()).isNullOrEmpty();
    assertThat(update1.getSuccess()).isTrue();

    THEN("version and schema version should be updated");
    var update1Read = raidApi.readRaidV2(new ReadRaidV2Request().handle(mintedRaid.getHandle()));
    var update1Metadata = mapper.readValue((String) update1Read.getMetadata(), RaidoMetadataSchemaV2.class);
    assertThat(update1Metadata.getAccess().getType()).isEqualTo(OPEN);
    assertThat(update1Metadata.getTitles().get(0).getTitle()).isEqualTo(
            initialTitle + " updated" );
    assertThat(update1Metadata.getMetadataSchema()).isEqualTo(RAIDOMETADATASCHEMAV2);

    WHEN("raid is updated with old version");
    var existingDescription = update1Metadata.getDescriptions().get(0);
    var newDescription =
            existingDescription.description(existingDescription.getDescription() + " updated");

    try {
      raidApi.updateRaidoSchemaV2(
              new UpdateRaidoSchemaV2Request().metadata(
                      mapRaidMetadataToRaido(readMetadata).
                              descriptions(List.of(newDescription))
              ));
    } catch (FeignException e) {
      var body = e.responseBody()
              .map(byteBuffer -> new String(byteBuffer.array(), Charset.defaultCharset()))
              .orElse("{}");

      var failureResponse = mapper.readValue(body, FailureResponse.class);

      assertThat(e.status()).isEqualTo(400);
      assertThat(failureResponse.getTitle()).isEqualTo("Invalid version");
      assertThat(failureResponse.getDetail()).isEqualTo("Update failed with stale version: 1");
    }
  }

  public static ContributorBlock createDummyLeaderContributor(LocalDate today) {
    return new ContributorBlock().
      id(REAL_TEST_ORCID).
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
      id(REAL_TEST_ROR).
      identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_).
      roles(List.of(
        new OrganisationRole().
          roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_).
          role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION)
          .startDate(today)));
  }

  public static RaidoMetadataSchemaV2 mapRaidMetadataToRaido(
    PublicRaidMetadataSchemaV1 in
  ){
    return new RaidoMetadataSchemaV2().
      metadataSchema(RAIDOMETADATASCHEMAV2).
      id(in.getId()).
      dates(in.getDates()).
      titles(in.getTitles()).
      descriptions(in.getDescriptions()).
      alternateUrls(in.getAlternateUrls()).
      contributors(in.getContributors()).
      organisations(in.getOrganisations()).
      access(in.getAccess());
  }

  public static RaidoMetadataSchemaV2 mapRaidMetadataToRaido(
          RaidoMetadataSchemaV2 in
  ){
    return new RaidoMetadataSchemaV2().
            metadataSchema(RAIDOMETADATASCHEMAV2).
            id(in.getId()).
            dates(in.getDates()).
            titles(in.getTitles()).
            descriptions(in.getDescriptions()).
            alternateUrls(in.getAlternateUrls()).
            contributors(in.getContributors()).
            organisations(in.getOrganisations()).
            access(in.getAccess());
  }

}