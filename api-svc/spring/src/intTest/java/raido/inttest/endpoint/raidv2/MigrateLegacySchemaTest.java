package raido.inttest.endpoint.raidv2;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static raido.inttest.endpoint.raidv2.RaidoSchemaV1Test.createDummyLeaderContributor;

public class MigrateLegacySchemaTest extends IntegrationTestCase {

  public static final String NOTRE_DAME = "University of Notre Dame Library";
  
//  @Autowired private RaidoApiUtil api;

  @Test
  void happyDayScenario() throws JsonProcessingException {
    
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
        identifierSchemeURI(RAID_ID_TYPE_URI).
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
    var pubRead = raidoApi.getPublicExperimental().publicReadRaidV3(handle);
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
    // technically this is an invalid raid, contrib is supposed to be mandatory
    assertThat(pubReadMeta.getContributors()).isNullOrEmpty();

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
    
    EXPECT("should be able to upgrade a legacy raid to raido schema");
    var remintMetadata = mapper.readValue(
      remintResult.getRaid().getMetadata().toString(), 
      LegacyMetadataSchemaV1.class );
    
    var upgradeResult = basicApi.upgradeLegacyToRaidoSchema(
      new UpdateRaidoSchemaV1Request().metadata(
        new RaidoMetadataSchemaV1().
          metadataSchema(RAIDOMETADATASCHEMAV1).
          id(remintMetadata.getId()).
          dates(remintMetadata.getDates()).
          access(remintMetadata.getAccess()).
          titles(remintMetadata.getTitles()).
          descriptions(remintMetadata.getDescriptions()).
          alternateUrls(remintMetadata.getAlternateUrls()).
          contributors(List.of(createDummyLeaderContributor(today)))
//            .organisations(List.of(createDummyOrganisation(today)))

      ) );
    assertThat(upgradeResult.getFailures()).isNullOrEmpty();
    assertThat(upgradeResult.getSuccess()).isTrue();

    EXPECT("upgraded raid should have a contributor");
    pubRead = raidoApi.getPublicExperimental().publicReadRaidV3(handle);
    pubReadMeta = (PublicRaidMetadataSchemaV1) pubRead.getMetadata();
    assertThat(pubReadMeta.getContributors()).isNotEmpty();

    /* "reproduce" because we don't actually want this behaviour, we'd prefer 
    that this failed - this "documents" the existing undesirable behaviour */
    EXPECT("reproduce that upgraded raids are able to re-migrated");
    remintResult = adminApi.migrateLegacyRaid(
      new MigrateLegacyRaidRequest().
        mintRequest(new MigrateLegacyRaidRequestMintRequest().
          servicePointId(servicePoint.getId()).
          contentIndex(1).
          createDate(OffsetDateTime.now()) ).
        metadata(initMetadata) );
    assertThat(remintResult.getFailures()).isNullOrEmpty();
    assertThat(remintResult.getSuccess()).isTrue();


    EXPECT("reproduce that upgraded re-migrated raid had contributors stomped");
    pubRead = raidoApi.getPublicExperimental().publicReadRaidV3(handle);
    pubReadMeta = (PublicRaidMetadataSchemaV1) pubRead.getMetadata();
    assertThat(pubReadMeta.getContributors()).isNullOrEmpty();
    
  }

  
  
}
