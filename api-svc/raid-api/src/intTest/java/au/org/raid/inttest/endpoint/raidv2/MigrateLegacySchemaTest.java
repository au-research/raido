package au.org.raid.inttest.endpoint.raidv2;

import au.org.raid.api.util.DateUtil;
import au.org.raid.db.jooq.enums.UserRole;
import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.IntegrationTestCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAID_AU_SP_ID;
import static au.org.raid.api.service.raid.MetadataService.RAID_ID_TYPE_URI;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.LEGACYMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static au.org.raid.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static au.org.raid.inttest.endpoint.raidv1.LegacyRaidV1MintTest.INT_TEST_ID_URL;
import static au.org.raid.inttest.endpoint.raidv2.RaidoSchemaV1Test.createDummyLeaderContributor;
import static org.assertj.core.api.Assertions.assertThat;

public class MigrateLegacySchemaTest extends IntegrationTestCase {

    /* we changed the name of this via the UI, so it doesn't line up with the
     flyway migration any more in demo/prod data.
     Int tests were failing when run on an imported DB.
     In future for this kind of thing, need to work off id, not name.
     But for this test specifically, we'll delete this functionality soon
     anyway. */
    public static final String NOTRE_DAME = "University of Notre Dame";

    @Test
    void happyDayScenario() throws JsonProcessingException {
        var today = LocalDate.now();
        var handle = "intTest%s/%s".formatted(
                DateUtil.formatCompactIsoDate(today),
                // duplicate handle if two run in same millisecond
                DateUtil.formatCompactTimeMillis(LocalDateTime.now()));
        var notreDame = findPublicServicePoint(NOTRE_DAME);

        GenerateApiTokenResponse notreDameAdmin = createApiKeyUser(
                notreDame.getId(), handle + "-notreDame", UserRole.SP_ADMIN);
        GenerateApiTokenResponse raidoAdmin = createApiKeyUser(
                RAID_AU_SP_ID, handle + "-raido", UserRole.SP_ADMIN);

        var basicApiAsNotreDame = basicRaidExperimentalClient(notreDameAdmin.getApiToken());
        var adminApiAsRaido = adminExperimentalClientAs(raidoAdmin.getApiToken());


        String initialTitle = "migration integration test " + handle;
        var initMetadata = new LegacyMetadataSchemaV1().
                metadataSchema(LEGACYMETADATASCHEMAV1).
                id(new IdBlock().
                        identifier(INT_TEST_ID_URL + "/" + handle).
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
                        url("https://example.com/some.url.related.to.the.raid")));

        var mintResult = adminApiAsRaido.migrateLegacyRaid(
                new MigrateLegacyRaidRequest().
                        mintRequest(new MigrateLegacyRaidRequestMintRequest().
                                servicePointId(notreDame.getId()).
                                contentIndex(1).
                                createDate(OffsetDateTime.now())).
                        metadata(initMetadata)).getBody();
        assertThat(mintResult.getFailures()).isNullOrEmpty();
        assertThat(mintResult.getSuccess()).isTrue();

        var pubRead = raidoApi.getPublicExperimental().publicReadRaidV3(handle).getBody();
        assertThat(pubRead).isNotNull();
        assertThat(pubRead.getCreateDate()).isNotNull();
        assertThat(pubRead.getServicePointId()).isEqualTo(notreDame.getId());
        assertThat(pubRead.getHandle()).isEqualTo(handle);
        assertThat(pubRead.getHandle()).isEqualTo(handle);
        var pubReadMeta = (PublicRaidMetadataSchemaV1) pubRead.getMetadata();
        assertThat(pubReadMeta.getAlternateUrls()).isNotEmpty();
        assertThat(pubReadMeta.getAlternateUrls()).satisfiesExactly(i ->
                assertThat(i.getUrl()).isEqualTo(
                        initMetadata.getAlternateUrls().get(0).getUrl()));
        // technically this is an invalid raid, contrib is supposed to be mandatory
        assertThat(pubReadMeta.getContributors()).isNullOrEmpty();

        var listResult = basicApiAsNotreDame.listRaidV2(new RaidListRequestV2().
                servicePointId(notreDame.getId()).primaryTitle(initialTitle)).getBody();
        assertThat(listResult).singleElement().satisfies(i -> {
            assertThat(i.getHandle()).isEqualTo(mintResult.getRaid().getHandle());
            assertThat(i.getPrimaryTitle()).isEqualTo(initialTitle);
            assertThat(i.getStartDate()).isEqualTo(LocalDate.now());
            assertThat(i.getCreateDate()).isNotNull();
            assertThat(i.getMetadataSchema()).isEqualTo(LEGACYMETADATASCHEMAV1);
        });


        var remintResult = adminApiAsRaido.migrateLegacyRaid(
                new MigrateLegacyRaidRequest().
                        mintRequest(new MigrateLegacyRaidRequestMintRequest().
                                servicePointId(notreDame.getId()).
                                contentIndex(1).
                                createDate(OffsetDateTime.now())).
                        metadata(initMetadata)).getBody();
        assertThat(remintResult.getFailures()).isNullOrEmpty();
        assertThat(remintResult.getSuccess()).isTrue();

        var remintMetadata = mapper.readValue(
                remintResult.getRaid().getMetadata().toString(),
                LegacyMetadataSchemaV1.class);

        var upgradeResult = basicApiAsNotreDame.upgradeLegacyToRaidoSchema(
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
                )).getBody();
        assertThat(upgradeResult.getFailures()).isNullOrEmpty();
        assertThat(upgradeResult.getSuccess()).isTrue();

        pubRead = raidoApi.getPublicExperimental().publicReadRaidV3(handle).getBody();
        pubReadMeta = (PublicRaidMetadataSchemaV1) pubRead.getMetadata();
        assertThat(pubReadMeta.getContributors()).isNotEmpty();

    /* "reproduce" because we don't actually want this behaviour, we'd prefer 
    that this failed - this "documents" the existing undesirable behaviour */
        remintResult = adminApiAsRaido.migrateLegacyRaid(
                new MigrateLegacyRaidRequest().
                        mintRequest(new MigrateLegacyRaidRequestMintRequest().
                                servicePointId(notreDame.getId()).
                                contentIndex(1).
                                createDate(OffsetDateTime.now())).
                        metadata(initMetadata)).getBody();
        assertThat(remintResult.getFailures()).isNullOrEmpty();
        assertThat(remintResult.getSuccess()).isTrue();


        pubRead = raidoApi.getPublicExperimental().publicReadRaidV3(handle).getBody();
        pubReadMeta = (PublicRaidMetadataSchemaV1) pubRead.getMetadata();
        assertThat(pubReadMeta.getContributors()).isNullOrEmpty();
    }
}