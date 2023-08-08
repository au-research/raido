package raido.inttest.endpoint.raidv2.stable;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.service.stub.util.IdFactory;
import raido.idl.raidv2.api.BasicRaidExperimentalApi;
import raido.idl.raidv2.api.RaidoStableV1Api;
import raido.idl.raidv2.model.*;
import raido.inttest.JettyTestServer;
import raido.inttest.TestClient;
import raido.inttest.config.IntTestProps;
import raido.inttest.config.IntegrationTestConfig;
import raido.inttest.service.auth.BootstrapAuthTokenService;

import java.time.LocalDate;
import java.util.List;

import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static raido.db.jooq.api_svc.enums.UserRole.OPERATOR;
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
  protected BasicRaidExperimentalApi experimentalApi;

  protected IdentifierParser identifierParser;

  @Autowired
  protected TestClient testClient;
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
    raidApi = testClient.basicRaidStableClient(operatorToken);
    experimentalApi = testClient.basicRaidExperimentalClient((operatorToken));
    identifierParser = new IdentifierParser();
  }

  @BeforeEach
  public void init(TestInfo testInfo) {
    this.testInfo = testInfo;
  }
  protected String getName(){
    return testInfo.getDisplayName();
  }

  protected CreateRaidV1Request newCreateRequest() {
    String initialTitle = getClass().getSimpleName() + "." + getName() +
      idFactory.generateUniqueId();

    return new CreateRaidV1Request()
      .titles(List.of(new Title()
        .type(new TitleTypeWithSchemeUri()
          .id(PRIMARY_TITLE_TYPE)
          .schemeUri(TITLE_TYPE_SCHEME_URI))
        .title(initialTitle)
        .startDate(today)))
      .dates(new Dates().startDate(today))
      .descriptions(List.of(new Description()
        .type(new DescriptionTypeWithSchemeUri()
          .id(PRIMARY_DESCRIPTION_TYPE)
          .schemeUri(DESCRIPTION_TYPE_SCHEME_URI))
        .description("stuff about the int test raid")))
      .contributors(List.of(contributor(
        REAL_TEST_ORCID, LEADER_POSITION, SOFTWARE_CONTRIBUTOR_ROLE, today)))
      .organisations(List.of(organisation(
        REAL_TEST_ROR, LEAD_RESEARCH_ORGANISATION, today)))
      .access(new Access()
        .type(new AccessTypeWithSchemeUri()
          .id(OPEN_ACCESS_TYPE)
          .schemeUri(ACCESS_TYPE_SCHEME_URI)
        )
      );
  }

  private UpdateRaidV1Request mapReadToUpdate(RaidDto read){
    return new UpdateRaidV1Request().
      id(read.getId()).
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
      .positions(List.of(new ContributorPositionWithSchemeUri()
        .schemeUri(CONTRIBUTOR_POSITION_SCHEME_URI)
        .id(position)
        .startDate(startDate)))
      .roles(List.of(
        new ContributorRoleWithSchemeUri()
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
        new OrganisationRoleWithSchemeUri()
          .schemeUri(ORGANISATION_ROLE_SCHEME_URI)
          .id(role)
          .startDate(today)));
  }
}