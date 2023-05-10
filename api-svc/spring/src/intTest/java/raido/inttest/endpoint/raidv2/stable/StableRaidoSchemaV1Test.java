package raido.inttest.endpoint.raidv2.stable;

import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.junit.jupiter.api.Test;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.idl.raidv2.api.RaidoStableV1Api;
import raido.idl.raidv2.model.*;
import raido.inttest.IntegrationTestCase;

import java.time.LocalDate;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.apisvc.util.test.BddUtil.*;
import static raido.idl.raidv2.model.AccessType.OPEN;
import static raido.idl.raidv2.model.ContributorPositionRaidMetadataSchemaType.LEADER;
import static raido.idl.raidv2.model.ContributorRoleCreditNisoOrgType.SOFTWARE;
import static raido.idl.raidv2.model.DescriptionType.PRIMARY_DESCRIPTION;
import static raido.idl.raidv2.model.OrganisationRoleType.LEAD_RESEARCH_ORGANISATION;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.idl.raidv2.model.TitleType.PRIMARY_TITLE;
import static raido.inttest.endpoint.raidv1.LegacyRaidV1MintTest.INT_TEST_ID_URL;
import static raido.inttest.util.MinimalRaidTestData.*;

public class StableRaidoSchemaV1Test extends IntegrationTestCase {

  public RaidoStableV1Api basicRaidStableClient(String token){
    return Feign.builder().
      client(new OkHttpClient()).
      encoder(new JacksonEncoder(mapper)).
      decoder(new JacksonDecoder(mapper)).
      contract(feignContract).
      requestInterceptor(request->
        request.header(AUTHORIZATION, "Bearer " + token) ).
      logger(new Slf4jLogger(RaidoStableV1Api.class)).
      logLevel(Logger.Level.FULL).
      target(RaidoStableV1Api.class, props.getRaidoServerUrl());
  }
  
  public RaidoStableV1Api basicRaidStableClient(){
    return basicRaidStableClient(operatorToken);
  }
  
  @Test
  void happyDayScenario() {
    var raidApi = basicRaidStableClient();
    String initialTitle = getClass().getSimpleName() + "." + getName() + 
      idFactory.generateUniqueId();
    var today = LocalDate.now();
    var idParser = new IdentifierParser();

    EXPECT("minting a raid with minimal content should succeed");
    RaidSchemaV1 mintResult = raidApi.createRaidV1(new CreateRaidV1Request().
      metadataSchema(RAIDOMETADATASCHEMAV1).
      titles(of(new TitleBlock().
        type(PRIMARY_TITLE).
        title(initialTitle).
        startDate(today))).
      dates(new DatesBlock().startDate(today)).
      descriptions(of(new DescriptionBlock().
        type(PRIMARY_DESCRIPTION).
        description("stuff about the int test raid"))).
      contributors(of(createContributor(
        DUMMY_ORCID, LEADER, SOFTWARE, today))).
      organisations(of(createOrganisation(
        DUMMY_ROR, LEAD_RESEARCH_ORGANISATION, today))).
      access(new AccessBlock().type(OPEN))
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

    
    EXPECT("should be able to read the minted raid via public api (v3)");
    var v3Read = raidoApi.getPublicExperimental().
      publicReadRaidV3(mintedId.handle().format());
    assertThat(v3Read).isNotNull();
    assertThat(v3Read.getCreateDate()).isNotNull();
    assertThat(v3Read.getServicePointId()).isEqualTo(RAIDO_SP_ID);

    assertThat(v3Read.getHandle()).isEqualTo(mintedHandle);

    PublicReadRaidMetadataResponseV1 v3MetaRead = v3Read.getMetadata();
    assertThat(v3MetaRead.getMetadataSchema()).isEqualTo(
      PublicRaidMetadataSchemaV1.class.getSimpleName());
    assertThat(v3MetaRead).isInstanceOf(PublicRaidMetadataSchemaV1.class);
    PublicRaidMetadataSchemaV1 v3Meta = (PublicRaidMetadataSchemaV1) v3MetaRead; 
    assertThat(v3Meta.getId().getIdentifier()).
      isEqualTo(INT_TEST_ID_URL + "/" + mintedHandle);
    var readId = idParser.parseUrlWithRuntimeException(
      v3Meta.getId().getIdentifier());
    assertThat(v3Read.getHandle()).isEqualTo(readId.handle().format());

    assertThat(v3Meta.getAccess().getType()).isEqualTo(OPEN);
    assertThat(v3Meta.getTitles().get(0).getTitle()).
      isEqualTo(initialTitle);
    assertThat(v3Meta.getDescriptions().get(0).getDescription()).
      contains("stuff about the int test raid");


    WHEN("raid primaryTitle is updated");
    var readPrimaryTitle = v3Meta.getTitles().get(0);
    var newTitle =
      readPrimaryTitle.title(readPrimaryTitle.getTitle()+" updated");
    var updateRaid = mapReadToUpdate(readResult);
    updateRaid.titles(of(newTitle));
    
    var updateResult = raidApi.updateRaidV1(
      mintedId.handle().prefix(), mintedId.handle().suffix(), updateRaid);

    
    THEN("should be able to read new value via publicRead");
    var readUpdatedData = (PublicRaidMetadataSchemaV1)
      raidoApi.getPublicExperimental().
        publicReadRaidV3(mintedHandle).getMetadata();

    assertThat(readUpdatedData.getAccess().getType()).isEqualTo(OPEN);
    assertThat(readUpdatedData.getTitles().get(0).getTitle()).isEqualTo(
      initialTitle + " updated" );
    
  }
  
  private UpdateRaidV1Request mapReadToUpdate(RaidSchemaV1 read){
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