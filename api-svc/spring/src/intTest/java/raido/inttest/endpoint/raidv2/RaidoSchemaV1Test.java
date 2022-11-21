package raido.inttest.endpoint.raidv2;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;
import raido.inttest.util.IdFactory;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.apisvc.util.test.BddUtil.THEN;
import static raido.apisvc.util.test.BddUtil.WHEN;
import static raido.idl.raidv2.model.AccessType.CLOSED;
import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.RaidoMetaschema.PUBLICMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;

public class RaidoSchemaV1Test extends IntegrationTestCase {

  @Test
  void happyDayScenario() throws JsonProcessingException {
    var raidApi = super.basicRaidExperimentalClient();
    String initialTitle = "intV2 test" + IdFactory.generateUniqueId();
    var today = LocalDate.now();

    EXPECT("minting a raid with minimal content should succeed");
    var mintResult = raidApi.mintRaidoSchemaV1(
      new MintRaidoSchemaV1Request().
        mintRequest(new MintRaidoSchemaV1RequestMintRequest().
          servicePointId(RAIDO_SP_ID)).
        metadata(new MetadataSchemaV1().
          metadataSchema(PUBLICMETADATASCHEMAV1).
          titles(List.of(new TitleBlock().
            type(PRIMARY_TITLE).
            title(initialTitle).
            startDate(today))).
          dates(new DatesBlock().startDate(today)).
          descriptions(List.of(new DescriptionBlock().
            type(PRIMARY_DESCRIPTION).
            description("stuff about the int test raid"))).
          access(new AccessBlock().type(OPEN))
        )
    );
    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getSuccess()).isTrue();
    assertThat(mintResult.getRaid()).isNotNull();
    var mintedRaid = mintResult.getRaid();
    assertThat(mintedRaid.getHandle()).isNotBlank();
    assertThat(mintedRaid.getStartDate()).isNotNull();
    assertThat(mintedRaid.getMetadata()).isInstanceOf(String.class);
    var mintedMetadata = mapper.readValue(
      mintedRaid.getMetadata().toString(), MetadataSchemaV1.class);
    assertThat(mintedMetadata.getMetadataSchema()).
      isEqualTo(PUBLICMETADATASCHEMAV1);


    EXPECT("should be able to read the minted raid via authz api");
    var readResult = raidApi.readRaidV2(
      new ReadRaidV2Request().handle(mintedRaid.getHandle()));
    assertThat(readResult).isNotNull();


    EXPECT("should be able to read the minted raid via public api (v2) ");
    var pubReadObject = raidoApi.getPublicExperimintal().
      publicReadRaidV2(mintedRaid.getHandle());
    assertThat(pubReadObject).isNotNull();
    assertThat(pubReadObject).isInstanceOf(PublicReadRaidResponseV2.class);
    var pubRead = (PublicReadRaidResponseV2) pubReadObject;
    assertThat(pubRead.getCreateDate()).isNotNull();
    assertThat(pubRead.getServicePointId()).isEqualTo(RAIDO_SP_ID);
    assertThat(pubRead.getHandle()).isEqualTo(mintedRaid.getHandle());

    assertThat(pubRead.getMetadata()).isInstanceOf(LinkedHashMap.class);
    var pubReadMeta = mapper.convertValue(
      pubRead.getMetadata(), MetadataSchemaV1.class);
    assertThat(pubReadMeta.getMetadataSchema()).
      isEqualTo(PUBLICMETADATASCHEMAV1);

    assertThat(pubReadMeta.getId()).isNotNull();
    assertThat(pubReadMeta.getId().getIdentifier()).
      isEqualTo(mintedRaid.getHandle());
    assertThat(pubReadMeta.getAccess().getType()).isEqualTo(OPEN);
    assertThat(pubReadMeta.getTitles().get(0).getTitle()).
      isEqualTo(initialTitle);
    assertThat(pubReadMeta.getDescriptions().get(0).getDescription()).
      contains("stuff about the int test raid");

    EXPECT("should be able to read the minted raid via public api (v3)");
    var v3Read = raidoApi.getPublicExperimintal().
      publicReadRaidV3(mintedRaid.getHandle());
    assertThat(v3Read).isNotNull();
    assertThat(v3Read.getCreateDate()).isNotNull();
    assertThat(v3Read.getServicePointId()).isEqualTo(RAIDO_SP_ID);
    assertThat(v3Read.getHandle()).isEqualTo(mintedRaid.getHandle());
    
    ReadRaidMetadataResponseV1 v3MetaRead = v3Read.getMetadata();
    assertThat(v3MetaRead.getMetadataSchema()).isEqualTo(
      PublicMetadataSchemaV1.class.getSimpleName());
    assertThat(v3MetaRead).isInstanceOf(PublicMetadataSchemaV1.class);
    PublicMetadataSchemaV1 v3Meta = (PublicMetadataSchemaV1) v3MetaRead; 
    assertThat(v3Meta.getId().getIdentifier()).
      isEqualTo(mintedRaid.getHandle());
    assertThat(pubReadMeta.getAccess().getType()).isEqualTo(OPEN);
    assertThat(pubReadMeta.getTitles().get(0).getTitle()).
      isEqualTo(initialTitle);
    assertThat(pubReadMeta.getDescriptions().get(0).getDescription()).
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
        pubReadMeta.titles(List.of(newTitle)) ));
    assertThat(updateResult.getFailures()).isNullOrEmpty();
    assertThat(updateResult.getSuccess()).isTrue();

    THEN("should be able to read new value via publicRead");
    var readUpdatedData = (PublicMetadataSchemaV1)
      raidoApi.getPublicExperimintal().
        publicReadRaidV3(mintedRaid.getHandle()).getMetadata();
    
    assertThat(readUpdatedData.getAccess().getType()).isEqualTo(OPEN);
    assertThat(readUpdatedData.getTitles().get(0).getTitle()).isEqualTo(
      initialTitle + " updated" );

    
    WHEN("raid is updated to closed");
    var closeResult = raidApi.updateRaidoSchemaV1(
      new UpdateRaidoSchemaV1Request().metadata(
        new MetadataSchemaV1().
          metadataSchema(PUBLICMETADATASCHEMAV1).
          id(readUpdatedData.getId()).
          dates(readUpdatedData.getDates()).
          titles(readUpdatedData.getTitles()).
          descriptions(readUpdatedData.getDescriptions()).
          alternateUrls(readUpdatedData.getAlternateUrls()).
          access(
            readUpdatedData.getAccess().
              type(CLOSED).
              accessStatement("closed by update") )
      ));
    assertThat(closeResult.getFailures()).isNullOrEmpty();
    assertThat(closeResult.getSuccess()).isTrue();

    THEN("publicRaid should now return closed");
    var readClosed = raidoApi.getPublicExperimintal().
      publicReadRaidV3(mintedRaid.getHandle());
    var readClosedMeta = (ClosedMetadataSchemaV1) readClosed.getMetadata(); 
    assertThat(readClosedMeta.getAccess().getType()).isEqualTo(CLOSED);
    
    // IMPROVE: maybe we ought to have at least one non-feign test, that
    // tests the raw data returned for a closed raid, to make sure there's 
    // no mistakes with how the data mapping works.
    // Since this now uses a generated "Closed" class, how can we be *sure*
    // that our API doesn't leak stuff for closed raids?
//    AND("titles should not be returned");
//    assertThat(readClosedMeta.getTitles()).isNullOrEmpty();
    
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
        metadata(new MetadataSchemaV1().
          metadataSchema(PUBLICMETADATASCHEMAV1).
          titles(List.of(new TitleBlock().
            type(PRIMARY_TITLE).
            title(" ").
            startDate(null))).
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