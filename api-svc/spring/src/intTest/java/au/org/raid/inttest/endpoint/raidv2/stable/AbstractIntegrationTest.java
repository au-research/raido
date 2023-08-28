package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.stub.util.IdFactory;
import au.org.raid.idl.raidv1.api.RaidV1Api;
import au.org.raid.idl.raidv2.api.BasicRaidExperimentalApi;
import au.org.raid.idl.raidv2.api.RaidoStableV1Api;
import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.JettyTestServer;
import au.org.raid.inttest.TestClient;
import au.org.raid.inttest.auth.BootstrapAuthTokenService;
import au.org.raid.inttest.config.IntTestProps;
import au.org.raid.inttest.config.IntegrationTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.db.jooq.api_svc.enums.UserRole.OPERATOR;
import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.*;
import static au.org.raid.inttest.util.MinimalRaidTestData.REAL_TEST_ORCID;
import static au.org.raid.inttest.util.MinimalRaidTestData.REAL_TEST_ROR;

@SpringJUnitConfig(
        name = "SpringJUnitConfigContext",
        value = IntegrationTestConfig.class)
public class AbstractIntegrationTest {
    @RegisterExtension
    protected static JettyTestServer jettyTestServer = new JettyTestServer();
    protected final IdFactory idFactory = new IdFactory("inttest");
    protected String operatorToken;
    protected String raidV1TestToken;
    protected LocalDate today = LocalDate.now();
    protected CreateRaidV1Request createRequest;

    protected RaidoStableV1Api raidApi;
    protected BasicRaidExperimentalApi experimentalApi;

    protected RaidV1Api legacyApi;

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
    private TestInfo testInfo;

    @BeforeEach
    public void setupTestToken() {
        raidV1TestToken = bootstrapTokenSvc.initRaidV1TestToken();

        operatorToken = bootstrapTokenSvc.bootstrapToken(
                RAIDO_SP_ID, "intTestOperatorApiToken", OPERATOR);

        createRequest = newCreateRequest();
        raidApi = testClient.raidApi(operatorToken);
        experimentalApi = testClient.basicRaidExperimentalClient((operatorToken));
        legacyApi = testClient.legacyApi(raidV1TestToken);
        identifierParser = new IdentifierParser();
    }

    @BeforeEach
    public void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    protected String getName() {
        return testInfo.getDisplayName();
    }

    protected CreateRaidV1Request newCreateRequest() {
        String initialTitle = getClass().getSimpleName() + "." + getName() +
                idFactory.generateUniqueId();

        return new CreateRaidV1Request()
                .titles(List.of(new Title()
                        .language(new Language()
                                .schemaUri(LANGUAGE_SCHEMA_URI)
                                .id(LANGUAGE_ID)
                        )
                        .type(new TitleTypeWithSchemaUri()
                                .id(PRIMARY_TITLE_TYPE)
                                .schemaUri(TITLE_TYPE_SCHEMA_URI))
                        .title(initialTitle)
                        .startDate(today)))
                .dates(new Dates().startDate(today))
                .descriptions(List.of(new Description()
                        .language(new Language()
                                .schemaUri(LANGUAGE_SCHEMA_URI)
                                .id(LANGUAGE_ID))
                        .type(new DescriptionTypeWithSchemaUri()
                                .id(PRIMARY_DESCRIPTION_TYPE)
                                .schemaUri(DESCRIPTION_TYPE_SCHEMA_URI))
                        .description("stuff about the int test raid")
                        .language(new Language()
                                .schemaUri(LANGUAGE_SCHEMA_URI)
                                .id(LANGUAGE_ID))
                ))

                .contributors(List.of(contributor(
                        REAL_TEST_ORCID, LEADER_POSITION, SOFTWARE_CONTRIBUTOR_ROLE, today)))
                .organisations(List.of(organisation(
                        REAL_TEST_ROR, LEAD_RESEARCH_ORGANISATION, today)))
                .access(new Access()
                        .accessStatement(new AccessStatement()
                                .statement("Embargoed")
                                .language(new Language()
                                        .id(LANGUAGE_ID)
                                        .schemaUri(LANGUAGE_SCHEMA_URI)))
                        .type(new AccessTypeWithSchemaUri()
                                .id(EMBARGOED_ACCESS_TYPE)
                                .schemaUri(ACCESS_TYPE_SCHEMA_URI))
                        .embargoExpiry(LocalDate.now().plusMonths(1)))
                .spatialCoverages(List.of(new SpatialCoverage()
                        .language(new Language()
                                .id(LANGUAGE_ID)
                                .schemaUri(LANGUAGE_SCHEMA_URI))
                        .id(GEONAMES_MELBOURNE)
                        .place("Melbourne")
                        .schemaUri(GEONAMES_SCHEMA_URI)));
    }

    private UpdateRaidV1Request mapReadToUpdate(RaidDto read) {
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
                .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .positions(List.of(new ContributorPositionWithSchemaUri()
                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                        .id(position)
                        .startDate(startDate)))
                .roles(List.of(
                        new ContributorRoleWithSchemaUri()
                                .schemaUri(CONTRIBUTOR_ROLE_SCHEMA_URI)
                                .id(role)));
    }

    public Organisation organisation(
            String ror,
            String role,
            LocalDate today
    ) {
        return new Organisation()
                .id(ror)
                .schemaUri(ORGANISATION_IDENTIFIER_SCHEMA_URI).
                roles(List.of(
                        new OrganisationRoleWithSchemaUri()
                                .schemaUri(ORGANISATION_ROLE_SCHEMA_URI)
                                .id(role)
                                .startDate(today)));
    }
}