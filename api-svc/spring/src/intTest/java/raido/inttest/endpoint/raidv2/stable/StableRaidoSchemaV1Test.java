package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.Test;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;

import java.time.LocalDate;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.SOFTWARE;
import static raido.idl.raidv2.model.OrganisationRoleType.LEAD_RESEARCH_ORGANISATION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.inttest.util.MinimalRaidTestData.*;

public class StableRaidoSchemaV1Test extends IntegrationTestCase {
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


  @Test
  void happyDayScenario() {
    var raidApi = basicRaidStableClient();
    String initialTitle = getClass().getSimpleName() + "." + getName() + 
      idFactory.generateUniqueId();
    var today = LocalDate.now();
    var idParser = new IdentifierParser();

    EXPECT("minting a raid with minimal content should succeed");
    RaidDto mintResult = raidApi.createRaidV1(new CreateRaidV1Request()
      .metadataSchema(RAIDOMETADATASCHEMAV1)
      .titles(of(new Title()
        .type(PRIMARY_TITLE_TYPE)
        .schemeUri(TITLE_TYPE_SCHEME_URI)
        .title(initialTitle)
        .startDate(today)))
      .dates(new DatesBlock().startDate(today))
      .descriptions(of(new Description()
        .type(PRIMARY_DESCRIPTION_TYPE)
        .schemeUri(DESCRIPTION_TYPE_SCHEME_URI)
        .description("stuff about the int test raid")))
      .contributors(of(contributor(
        REAL_TEST_ORCID, LEADER, SOFTWARE, today)))
      .organisations(of(organisation(
        REAL_TEST_ROR, LEAD_RESEARCH_ORGANISATION, today)))
      .access(new Access()
        .type(ACCESS_TYPE_OPEN)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      )
    );
    
    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getId().getIdentifier()).isNotBlank();
    assertThat(mintResult.getDates().getStartDate()).isNotNull();
    assertThat(mintResult.getMetadataSchema()).
      isEqualTo(RAIDOMETADATASCHEMAV1);
    var mintedId = new IdentifierParser().parseUrlWithRuntimeException(
      mintResult.getId().getIdentifier());
    var mintedHandle = mintedId.handle().format();
    
    
    EXPECT("should be able to read the minted raid via authz api");
    var readResult = raidApi.readRaidV1(
      mintedId.handle().prefix(), mintedId.handle().suffix());
    assertThat(readResult).isNotNull();

    
//    EXPECT("should be able to read the minted raid via public api (v3)");
//    var v3Read = raidoApi.getPublicExperimental().
//      publicReadRaidV3(mintedId.handle().format());
//    assertThat(v3Read).isNotNull();
//    assertThat(v3Read.getCreateDate()).isNotNull();
//    assertThat(v3Read.getServicePointId()).isEqualTo(RAIDO_SP_ID);
//
//    assertThat(v3Read.getHandle()).isEqualTo(mintedHandle);
//
//    PublicReadRaidMetadataResponseV1 v3MetaRead = v3Read.getMetadata();
//    assertThat(v3MetaRead.getMetadataSchema()).isEqualTo(
//      PublicRaidMetadataSchemaV1.class.getSimpleName());
//    assertThat(v3MetaRead).isInstanceOf(PublicRaidMetadataSchemaV1.class);
//    PublicRaidMetadataSchemaV1 v3Meta = (PublicRaidMetadataSchemaV1) v3MetaRead;
//    assertThat(v3Meta.getId().getIdentifier()).
//      isEqualTo(INT_TEST_ID_URL + "/" + mintedHandle);
//    var readId = idParser.parseUrlWithRuntimeException(
//      v3Meta.getId().getIdentifier());
//    assertThat(v3Read.getHandle()).isEqualTo(readId.handle().format());
//
//    assertThat(v3Meta.getAccess().getType()).isEqualTo(OPEN);
//    assertThat(v3Meta.getTitles().get(0).getTitle()).
//      isEqualTo(initialTitle);
//    assertThat(v3Meta.getDescriptions().get(0).getDescription()).
//      contains("stuff about the int test raid");


//    WHEN("raid primaryTitle is updated");
//    var readPrimaryTitle = v3Meta.getTitles().get(0);
//    var newTitle =
//      readPrimaryTitle.title(readPrimaryTitle.getTitle()+" updated");
//    var updateRaid = mapReadToUpdate(readResult);
//    updateRaid.titles(of(newTitle));
//
//    var updateResult = raidApi.updateRaidV1(
//      mintedId.handle().prefix(), mintedId.handle().suffix(), updateRaid);
//
//
//    THEN("should be able to read new value via publicRead");
//    var readUpdatedData = (PublicRaidMetadataSchemaV1)
//      raidoApi.getPublicExperimental().
//        publicReadRaidV3(mintedHandle).getMetadata();
//
//    assertThat(readUpdatedData.getAccess().getType()).isEqualTo(OPEN);
//    assertThat(readUpdatedData.getTitles().get(0).getTitle()).isEqualTo(
//      initialTitle + " updated" );
    
  }
  
  private UpdateRaidV1Request mapReadToUpdate(RaidDto read){
    return new UpdateRaidV1Request().
      id(read.getId()).
      metadataSchema(read.getMetadataSchema()).
      titles(read.getTitles()).
      dates(read.getDates()).
      descriptions(read.getDescriptions()).
      access(read.getAccess()).
      alternateUrls(read.getAlternateUrls()).
      contributors(read.getContributors()).
      organisations(read.getOrganisations()).
      subjects(read.getSubjects()).
      relatedRaids(read.getRelatedRaids()).
      relatedObjects(read.getRelatedObjects()).
      alternateIdentifiers(read.getAlternateIdentifiers()).
      spatialCoverages(read.getSpatialCoverages()).
      traditionalKnowledgeLabels(read.getTraditionalKnowledgeLabels());
  }
}