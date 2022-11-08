package raido.inttest.endpoint.raidv2;

import org.junit.jupiter.api.Test;
import raido.apisvc.util.DateUtil;
import raido.idl.raidv2.api.AdminExperimentalApi;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.service.raid.MetadataService.RAID_ID_TYPE_URI;
import static raido.apisvc.util.StringUtil.areEqual;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.idl.raidv2.model.Metaschema.RAIDO_METADATA_SCHEMA_V1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;

public class MigrateRaidoSchemaV1Test  extends IntegrationTestCase {

  public static final String NOTRE_DAME = "University of Notre Dame Library";

  public ServicePoint findServicePoint(
    AdminExperimentalApi adminApi, String name
  ){
    return adminApi.listServicePoint().stream().
      filter(i->areEqual(i.getName(), name)).
      findFirst().orElseThrow();
  }
  
  @Test
  void happyDayScenario(){
    var raidApi = super.basicRaidExperimentalClient();
    var publicApi = super.publicExperimentalClient();
    var adminApi = super.adminExperimentalClient();
    var today = LocalDate.now();
    var handle = "intTest%s/%s".formatted(
      DateUtil.formatCompactIsoDate(today), 
      // duplicate handle if two run in same millisecond
      DateUtil.formatCompactTimeMillis(LocalDateTime.now()) );
    var servicePoint = findServicePoint(adminApi, NOTRE_DAME);
    

    MetadataSchemaV1 metadata = new MetadataSchemaV1().
      metadataSchema(RAIDO_METADATA_SCHEMA_V1).
      id(new IdBlock().
        identifier(handle).
        identifierTypeUri(RAID_ID_TYPE_URI).
        globalUrl("https://something.example.com")).
      descriptions(List.of(new DescriptionBlock().
        type(DescriptionType.PRIMARY_DESCRIPTION).
        description("some description of the thing"))).
      titles(List.of(new TitleBlock().
        type(PRIMARY_TITLE).
        title("some title").
        startDate(today))).
      dates(new DatesBlock().startDate(today)).
      access(new AccessBlock().type(AccessType.OPEN));
    
    EXPECT("migrating a raid with correct content should work");
    var mintResult = adminApi.migrateLegacyRaid(
      new MigrateLegacyRaidRequest().
        mintRequest(new MigrateLegacyRaidRequestMintRequest().
          servicePointId(servicePoint.getId()).
          contentIndex(1).
          createDate(OffsetDateTime.now()) ).
        metadata(metadata) );
    assertThat(mintResult.getFailures()).isNullOrEmpty();
    assertThat(mintResult.getSuccess()).isTrue();

    EXPECT("should be able to read the minted raid via public api");
    var pubReadObject = publicApi.publicReadRaidV2(handle);
    assertThat(pubReadObject).isNotNull();
    assertThat(pubReadObject).isInstanceOf(PublicReadRaidResponseV2.class);
    var pubRead = (PublicReadRaidResponseV2) pubReadObject;
    assertThat(pubRead.getCreateDate()).isNotNull();
    assertThat(pubRead.getServicePointId()).isEqualTo(servicePoint.getId());
    assertThat(pubRead.getHandle()).isEqualTo(handle);

    EXPECT("should be re-migrate an existing raid");
    var remintResult = adminApi.migrateLegacyRaid(
      new MigrateLegacyRaidRequest().
        mintRequest(new MigrateLegacyRaidRequestMintRequest().
          servicePointId(servicePoint.getId()). 
          contentIndex(1).
          createDate(OffsetDateTime.now()) ).
        metadata(metadata) );
    assertThat(remintResult.getFailures()).isNullOrEmpty();
    assertThat(remintResult.getSuccess()).isTrue();
  }
  
}
