package raido.inttest.endpoint.raidv2.stable;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Request;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.service.stub.util.IdFactory;
import raido.idl.raidv2.api.RaidoStableV1Api;
import raido.idl.raidv2.model.*;
import raido.inttest.JettyTestServer;
import raido.inttest.RaidApiExceptionDecoder;
import raido.inttest.config.IntTestProps;
import raido.inttest.config.IntegrationTestConfig;
import raido.inttest.service.auth.BootstrapAuthTokenService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static raido.idl.raidv2.model.RaidoMetaschema.RAIDOMETADATASCHEMAV1;
import static raido.inttest.endpoint.raidv2.stable.TestConstants.*;
import static raido.inttest.util.MinimalRaidTestData.REAL_TEST_ORCID;
import static raido.inttest.util.MinimalRaidTestData.REAL_TEST_ROR;

@SpringJUnitConfig(
  name="SpringJUnitConfigContext",
  value= IntegrationTestConfig.class )
public class AbstractStableIntegrationTest {
  protected String operatorToken;

  protected final IdFactory idFactory = new IdFactory("inttest");
  protected LocalDate today = LocalDate.now();

  private TestInfo testInfo;

  protected CreateRaidV1Request createRequest;

  protected RaidoStableV1Api raidApi;

  protected IdentifierParser identifierParser;

  @Autowired
  protected ObjectMapper mapper;
  @Autowired
  protected Contract feignContract;
  @Autowired
  protected IntTestProps props;
  @Autowired
  protected BootstrapAuthTokenService bootstrapTokenSvc;

  @RegisterExtension
  protected static JettyTestServer jettyTestServer = new JettyTestServer();

  @BeforeEach
  public void setupTestToken(){
    operatorToken = bootstrapTokenSvc.bootstrapToken(
      RAIDO_SP_ID, "intTestOperatorApiToken", OPERATOR);

    createRequest = newCreateRequest();
    raidApi = basicRaidStableClient();
    identifierParser = new IdentifierParser();
  }

  @BeforeEach
  public void init(TestInfo testInfo) {
    this.testInfo = testInfo;
  }
  protected RaidoStableV1Api basicRaidStableClient(String token){
    return Feign.builder()
      .options(
        new Request.Options(2, TimeUnit.SECONDS, 2, TimeUnit.SECONDS, false)
      )
      .client(new OkHttpClient())
      .encoder(new JacksonEncoder(mapper))
      .decoder(new JacksonDecoder(mapper))
      .errorDecoder(new RaidApiExceptionDecoder(mapper))
      .contract(feignContract)
      .requestInterceptor(request -> request.header(AUTHORIZATION, "Bearer " + token))
      .logger(new Slf4jLogger(RaidoStableV1Api.class))
      .logLevel(Logger.Level.FULL)
      .target(RaidoStableV1Api.class, props.getRaidoServerUrl());
  }


  protected RaidoStableV1Api basicRaidStableClient(){
    return basicRaidStableClient(operatorToken);
  }

  protected String getName(){
    return testInfo.getDisplayName();
  }

  protected CreateRaidV1Request newCreateRequest() {
    String initialTitle = getClass().getSimpleName() + "." + getName() +
      idFactory.generateUniqueId();

    return new CreateRaidV1Request()
      .metadataSchema(RAIDOMETADATASCHEMAV1)
      .titles(List.of(new Title()
        .type(PRIMARY_TITLE_TYPE)
        .schemeUri(TITLE_TYPE_SCHEME_URI)
        .title(initialTitle)
        .startDate(today)))
      .dates(new DatesBlock().startDate(today))
      .descriptions(List.of(new Description()
        .type(new DescType()
          .id(PRIMARY_DESCRIPTION_TYPE)
          .schemeUri(DESCRIPTION_TYPE_SCHEME_URI))
        .description("stuff about the int test raid")))
      .contributors(List.of(contributor(
        REAL_TEST_ORCID, LEADER_POSITION, SOFTWARE_CONTRIBUTOR_ROLE, today)))
      .organisations(List.of(organisation(
        REAL_TEST_ROR, LEAD_RESEARCH_ORGANISATION, today)))
      .access(new Access()
        .type(OPEN_ACCESS_TYPE)
        .schemeUri(ACCESS_TYPE_SCHEME_URI)
      );
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

  public Contributor contributor(
    final String orcid,
    final String position,
    String role,
    LocalDate startDate
  ) {
    return new Contributor()
      .id(orcid)
      .identifierSchemeUri(CONTRIBUTOR_SCHEME_URI)
      .positions(List.of(new ContribPosition()
        .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
        .id(position)
        .startDate(startDate)))
      .roles(List.of(
        new ContribRole()
          .schemeUri(CONTRIBUTOR_ROLE_SCHEME_URI)
          .id(role)));
  }

  public Organisation organisation(
    String ror,
    String role,
    LocalDate today
  ) {
    return new Organisation()
      .id(ror)
      .identifierSchemeUri(ORGANISATION_IDENTIFIER_SCHEME_URI).
      roles(List.of(
        new OrgRole()
          .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
          .id(role)
          .startDate(today)));
  }
}