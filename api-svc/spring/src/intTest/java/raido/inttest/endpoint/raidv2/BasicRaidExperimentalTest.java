package raido.inttest.endpoint.raidv2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import raido.apisvc.service.raid.MetadataService.Schema;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;
import raido.inttest.util.IdFactory;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.endpoint.raidv2.BasicRaidExperimental.RAIDO_SP_ID;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.apisvc.util.test.BddUtil.THEN;
import static raido.apisvc.util.test.BddUtil.WHEN;

public class BasicRaidExperimentalTest extends IntegrationTestCase {

  @Autowired private ObjectMapper mapper;
  
  @Test
  void happyDayMintListRead() {
    var raidApi = super.basicRaidExperimentalClient();
    var publicApi = publicExperimentalClient();
    String initialName = "int test" + IdFactory.generateUniqueId();


    EXPECT("minting a raid with minimal content should succeed");
    var mintResult = raidApi.mintRaidV1(new MintRaidRequestV1().
      servicePointId(RAIDO_SP_ID).
      name(initialName).
      confidential(false));
    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getHandle()).isNotBlank();
    assertThat(mintResult.getStartDate()).isNotNull();


    /* list by unique name to prevent eventual pagination issues */
    EXPECT("should be able to list the minted raid");
    var listResult = raidApi.listRaid(new RaidListRequest().
      servicePointId(RAIDO_SP_ID).name(initialName));
    assertThat(listResult).singleElement().satisfies(i->{
      assertThat(i.getHandle()).isEqualTo(mintResult.getHandle());
      assertThat(i.getName()).isEqualTo(initialName);
      assertThat(i.getStartDate()).isEqualTo(LocalDate.now());
      assertThat(i.getConfidential()).isFalse();
      assertThat(i.getCreateDate()).isNotNull();
    });


    EXPECT("should be able to read the minted raid via authz api");
    var readResult = raidApi.readRaidV1(new ReadRaidV1Request().
      handle(mintResult.getHandle()));
    assertThat(readResult).isNotNull();
    assertThat(readResult.getHandle()).isEqualTo(mintResult.getHandle());
    assertThat(readResult.getServicePointId()).isEqualTo(RAIDO_SP_ID);
    assertThat(readResult.getServicePointName()).isEqualTo("raido");
    assertThat(readResult.getStartDate()).isNotNull();


    EXPECT("should be able to update the minted");
    String updatedName = readResult.getName() + " updated";
    MintRaidRequestV1 updateRequest = new MintRaidRequestV1().
      handle(readResult.getHandle()).
      name(updatedName).
      servicePointId(readResult.getServicePointId()).
      startDate(readResult.getStartDate()).
      confidential(readResult.getConfidential());
    var updateResult = raidApi.updateRaidV1(updateRequest);
    assertThat(updateResult).isNotNull();
    assertThat(updateResult.getHandle()).isEqualTo(mintResult.getHandle());
    assertThat(updateResult.getStartDate()).
      isEqualTo(readResult.getStartDate());
    assertThat(updateResult.getName()).isEqualTo(updatedName);


    WHEN("list by initial name from before update");
    var listResult2 = raidApi.listRaid(new RaidListRequest().
      servicePointId(RAIDO_SP_ID).name(initialName));

    THEN("should find raid with updated name");
    assertThat(listResult2).singleElement().satisfies(i->{
      assertThat(i.getHandle()).isEqualTo(mintResult.getHandle());
      assertThat(i.getName()).isEqualTo(updatedName);
      assertThat(i.getStartDate()).isEqualTo(LocalDate.now());
    });


    EXPECT("should be able to read the minted raid via public api");
    var pubRead = publicApi.publicReadRaid(mintResult.getHandle());
    assertThat(pubRead).isNotNull();
    assertThat(pubRead.getHandle()).isEqualTo(mintResult.getHandle());
    assertThat(pubRead.getName()).isEqualTo(updatedName);
    assertThat(pubRead.getConfidential()).isFalse();
    assertThat(pubRead.getCreateDate()).isNotNull();


    WHEN("raid is made confidential");
    updateRequest.setConfidential(true);
    raidApi.updateRaidV1(updateRequest);

    THEN("public read endpoint should not return data");
    pubRead = publicApi.publicReadRaid(mintResult.getHandle());
    assertThat(pubRead).isNotNull();
    assertThat(pubRead.getHandle()).isEqualTo(mintResult.getHandle());
    assertThat(pubRead.getConfidential()).isTrue();
    assertThat(pubRead.getName()).isNull();
    assertThat(pubRead.getCreateDate()).isNotNull();
    assertThat(pubRead.getServicePointId()).isNull();
    assertThat(pubRead.getStartDate()).isNull();
    assertThat(pubRead.getMetadata()).isNull();
  }

  @Test
  void happyDayMintRaidoSchemaV1() throws JsonProcessingException {
    var raidApi = super.basicRaidExperimentalClient();
    var publicApi = publicExperimentalClient();
    String initialTitle = "intV2 test" + IdFactory.generateUniqueId();
    var today = LocalDate.now();

    EXPECT("minting a raid with minimal content should succeed");
    var mintResult = raidApi.mintRaidoSchemaV1(
      new MintRaidoSchemaV1Request().
        mintRequest(new MintRaidoSchemaV1RequestMintRequest().
          servicePointId(RAIDO_SP_ID)).
        metadata(new RaidoMetadataSchemaV1().
          metadataSchema(Schema.RAIDO_V1.getId()).
          titles(List.of(new TitleBlock().
            type(TitleType.PRIMARY_TITLE).
            title(initialTitle).
            startDate(today))).
          dates(new DatesBlock().startDate(today)).
          descriptions(List.of(new DescriptionBlock().
            type(DescriptionType.PRIMARY_DESCRIPTION).
            description("stuff about the int test raid"))).
          access(new AccessBlock().type(AccessType.OPEN))
        )
    );

    assertThat(mintResult).isNotNull();
    assertThat(mintResult.getHandle()).isNotBlank();
    assertThat(mintResult.getStartDate()).isNotNull();
    assertThat(mintResult.getMetadata()).isInstanceOf(String.class);
    var mintedMetadata = mapper.readValue(
      mintResult.getMetadata().toString(), RaidoMetadataSchemaV1.class);
    assertThat(mintedMetadata.getMetadataSchema()).
      isEqualTo(Schema.RAIDO_V1.getId());

    EXPECT("should be able to read the minted raid via authz api");
    var readResult = raidApi.readRaidV2(
      new ReadRaidV1Request().handle(mintResult.getHandle()) );
    assertThat(readResult).isNotNull();

    EXPECT("should be able to read the minted raid via public api");
    var pubReadObject = publicApi.publicReadRaidV2(mintResult.getHandle());
    assertThat(pubReadObject).isNotNull();
    assertThat(pubReadObject).isInstanceOf(PublicReadRaidResponseV2.class);
    var pubRead = (PublicReadRaidResponseV2) pubReadObject;
    assertThat(pubRead.getCreateDate()).isNotNull();
    assertThat(pubRead.getServicePointId()).isEqualTo(RAIDO_SP_ID);

    assertThat(mintResult.getMetadata()).isInstanceOf(String.class);
    var pubReadMeta = mapper.readValue(
      mintResult.getMetadata().toString(), RaidoMetadataSchemaV1.class);
    assertThat(pubReadMeta.getMetadataSchema()).
      isEqualTo(Schema.RAIDO_V1.getId());
    
    assertThat(pubReadMeta.getId()).isNotNull();
    assertThat(pubReadMeta.getId().getIdentifier()).
      isEqualTo(mintResult.getHandle());
    assertThat(pubReadMeta.getTitles().get(0).getTitle()).
      isEqualTo(initialTitle);
    assertThat(pubReadMeta.getAccess().getType()).isEqualTo(AccessType.OPEN);

  }
}