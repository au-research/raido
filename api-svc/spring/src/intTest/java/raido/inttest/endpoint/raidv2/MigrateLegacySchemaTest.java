package raido.inttest.endpoint.raidv2;

import org.junit.jupiter.api.Test;
import raido.apisvc.util.DateUtil;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static raido.apisvc.service.raid.MetadataService.RAID_ID_TYPE_URI;
import static raido.apisvc.util.test.BddUtil.EXPECT;
import static raido.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;

public class MigrateLegacySchemaTest extends IntegrationTestCase {

  public static final String NOTRE_DAME = "University of Notre Dame Library";
  
//  @Autowired private RaidoApiUtil api;

  @Test
  void happyDayScenario(){
    
    var adminApi = super.adminExperimentalClientAs(adminToken);
    var today = LocalDate.now();
    var handle = "intTest%s/%s".formatted(
      DateUtil.formatCompactIsoDate(today), 
      // duplicate handle if two run in same millisecond
      DateUtil.formatCompactTimeMillis(LocalDateTime.now()) );
    var servicePoint = findServicePoint(adminApi, NOTRE_DAME);
    var basicApi = basicRaidExperimentalClientAs(
      servicePoint.getId(), handle, UserRole.SP_ADMIN);


    String initialTitle = "migration integration test " + handle;
    var initMetadata = new LegacyMetadataSchemaV1().
      metadataSchema(LEGACYMETADATASCHEMAV1).
      id(new IdBlock().
        identifier(handle).
        identifierTypeUri(RAID_ID_TYPE_URI).
        globalUrl("https://something.example.com")).
      descriptions(List.of(new DescriptionBlock().
        type(DescriptionType.PRIMARY_DESCRIPTION).
        description("some description of the thing"))).
      titles(List.of(new TitleBlock().
        type(PRIMARY_TITLE).
        title(initialTitle).
        startDate(today))).
      dates(new DatesBlock().startDate(today)).
      access(new AccessBlock().type(AccessType.OPEN)).
      alternateUrls(List.of(new AlternateUrlBlock().
        url("https://example.com/some.url.related.to.the.raid") ));
    
    EXPECT("migrating a raid with correct content should work");
    var mintResult = adminApi.migrateLegacyRaid(
      new MigrateLegacyRaidRequest().
        mintRequest(new MigrateLegacyRaidRequestMintRequest().
          servicePointId(servicePoint.getId()).
          contentIndex(1).
          createDate(OffsetDateTime.now()) ).
        metadata(initMetadata) );
    assertThat(mintResult.getFailures()).isNullOrEmpty();
    assertThat(mintResult.getSuccess()).isTrue();

    EXPECT("should be able to read the minted raid via public api");
    var pubRead = raidoApi.getPublicExperimental().
      publicReadRaidV3(handle);
    assertThat(pubRead).isNotNull();
    assertThat(pubRead.getCreateDate()).isNotNull();
    assertThat(pubRead.getServicePointId()).isEqualTo(servicePoint.getId());
    assertThat(pubRead.getHandle()).isEqualTo(handle);
    assertThat(pubRead.getHandle()).isEqualTo(handle);
    var pubReadMeta = (PublicRaidMetadataSchemaV1) pubRead.getMetadata(); 
    assertThat(pubReadMeta.getAlternateUrls()).isNotEmpty();
    assertThat(pubReadMeta.getAlternateUrls()).satisfiesExactly(i->
      assertThat(i.getUrl()).isEqualTo(
        initMetadata.getAlternateUrls().get(0).getUrl() ));

    EXPECT("should be able to list a migrated raid");
    var listResult = basicApi.listRaidV2(new RaidListRequestV2().
      servicePointId(servicePoint.getId()).primaryTitle(initialTitle));
    assertThat(listResult).singleElement().satisfies(i->{
      assertThat(i.getHandle()).isEqualTo(mintResult.getRaid().getHandle());
      assertThat(i.getPrimaryTitle()).isEqualTo(initialTitle);
      assertThat(i.getStartDate()).isEqualTo(LocalDate.now());
      assertThat(i.getCreateDate()).isNotNull();
      assertThat(i.getMetadataSchema()).isEqualTo(LEGACYMETADATASCHEMAV1);
    });
    
    
    EXPECT("should be able to re-migrate an existing raid");
    var remintResult = adminApi.migrateLegacyRaid(
      new MigrateLegacyRaidRequest().
        mintRequest(new MigrateLegacyRaidRequestMintRequest().
          servicePointId(servicePoint.getId()). 
          contentIndex(1).
          createDate(OffsetDateTime.now()) ).
        metadata(initMetadata) );
    assertThat(remintResult.getFailures()).isNullOrEmpty();
    assertThat(remintResult.getSuccess()).isTrue();
    
  }
  
}
