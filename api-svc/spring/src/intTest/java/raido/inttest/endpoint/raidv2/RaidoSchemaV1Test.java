package raido.inttest.endpoint.raidv2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static raido.idl.raidv2.model.Metaschema.RAIDO_METADATA_SCHEMA_V1;

public class RaidoSchemaV1Test extends IntegrationTestCase {

  @Autowired private ObjectMapper mapper;
  
  @Test
  void happyDayScenario() throws JsonProcessingException {
    var raidApi = super.basicRaidExperimentalClient();
    var publicApi = publicExperimentalClient();
    String initialTitle = "intV2 test" + IdFactory.generateUniqueId();
    var today = LocalDate.now();

    EXPECT("minting a raid with minimal content should succeed");
    var mintResult = raidApi.mintRaidoSchemaV1(
      new MintRaidoSchemaV1Request().
        mintRequest(new MintRaidoSchemaV1RequestMintRequest().
          servicePointId(RAIDO_SP_ID)).
        metadata(new MetadataSchemaV1().
          metadataSchema(RAIDO_METADATA_SCHEMA_V1).
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
    assertThat(mintResult.getSuccess()).isTrue();
    assertThat(mintResult.getRaid()).isNotNull();
    var mintedRaid = mintResult.getRaid();
    assertThat(mintedRaid.getHandle()).isNotBlank();
    assertThat(mintedRaid.getStartDate()).isNotNull();
    assertThat(mintedRaid.getMetadata()).isInstanceOf(String.class);
    var mintedMetadata = mapper.readValue(
      mintedRaid.getMetadata().toString(), MetadataSchemaV1.class);
    assertThat(mintedMetadata.getMetadataSchema()).
      isEqualTo(RAIDO_METADATA_SCHEMA_V1);

    
    EXPECT("should be able to read the minted raid via authz api");
    var readResult = raidApi.readRaidV2(
      new ReadRaidV1Request().handle(mintedRaid.getHandle()) );
    assertThat(readResult).isNotNull();

    
    EXPECT("should be able to read the minted raid via public api");
    var pubReadObject = publicApi.publicReadRaidV2(mintedRaid.getHandle());
    assertThat(pubReadObject).isNotNull();
    assertThat(pubReadObject).isInstanceOf(PublicReadRaidResponseV2.class);
    var pubRead = (PublicReadRaidResponseV2) pubReadObject;
    assertThat(pubRead.getCreateDate()).isNotNull();
    assertThat(pubRead.getServicePointId()).isEqualTo(RAIDO_SP_ID);

    assertThat(mintedRaid.getMetadata()).isInstanceOf(String.class);
    var pubReadMeta = mapper.readValue(
      mintedRaid.getMetadata().toString(), MetadataSchemaV1.class);
    assertThat(pubReadMeta.getMetadataSchema()).
      isEqualTo(RAIDO_METADATA_SCHEMA_V1);
    
    assertThat(pubReadMeta.getId()).isNotNull();
    assertThat(pubReadMeta.getId().getIdentifier()).
      isEqualTo(mintedRaid.getHandle());
    assertThat(pubReadMeta.getTitles().get(0).getTitle()).
      isEqualTo(initialTitle);
    assertThat(pubReadMeta.getAccess().getType()).isEqualTo(AccessType.OPEN);

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
    
  }

  @Test
  void validateMintEmptyPrimaryTitle() throws JsonProcessingException {
    var raidApi = super.basicRaidExperimentalClient();
    var today = LocalDate.now();

    WHEN("minting a raid with minimal content with empty primaryTitle");
    var mintResult = raidApi.mintRaidoSchemaV1(
      new MintRaidoSchemaV1Request().
        mintRequest(new MintRaidoSchemaV1RequestMintRequest().
          servicePointId(RAIDO_SP_ID)).
        metadata(new MetadataSchemaV1().
          metadataSchema(RAIDO_METADATA_SCHEMA_V1).
          titles(List.of(new TitleBlock().
            type(TitleType.PRIMARY_TITLE).
            title(" ").
            startDate(null) )).
          dates(new DatesBlock().startDate(today)).
          access(new AccessBlock().type(AccessType.OPEN))
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