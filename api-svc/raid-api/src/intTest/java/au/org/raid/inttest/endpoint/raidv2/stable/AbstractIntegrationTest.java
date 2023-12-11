package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.api.Api;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.stub.util.IdFactory;
import au.org.raid.idl.raidv1.api.RaidV1Api;
import au.org.raid.idl.raidv2.api.BasicRaidExperimentalApi;
import au.org.raid.idl.raidv2.api.RaidoStableV1Api;
import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.TestClient;
import au.org.raid.inttest.auth.BootstrapAuthTokenService;
import au.org.raid.inttest.client.RaidApi;
import au.org.raid.inttest.config.IntegrationTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.db.jooq.enums.UserRole.OPERATOR;
import static au.org.raid.inttest.endpoint.raidv2.stable.TestConstants.*;
import static au.org.raid.inttest.util.MinimalRaidTestData.REAL_TEST_ORCID;
import static au.org.raid.inttest.util.MinimalRaidTestData.REAL_TEST_ROR;

@SpringBootTest(classes = Api.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = IntegrationTestConfig.class)
public class AbstractIntegrationTest {
    protected static final Long UQ_SERVICE_POINT_ID = 20000002L;
    protected final IdFactory idFactory = new IdFactory("inttest");
    protected String operatorToken;
    protected String raidV1TestToken;
    protected LocalDate today = LocalDate.now();
    protected RaidCreateRequest createRequest;

    protected RaidoStableV1Api raidApi;
    protected BasicRaidExperimentalApi experimentalApi;

    protected RaidV1Api legacyApi;

    protected IdentifierParser identifierParser;
    @Autowired
    protected RaidApi api;

    @Autowired
    protected TestClient testClient;
    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    protected Contract feignContract;
    @Autowired
    protected BootstrapAuthTokenService bootstrapTokenSvc;
    @Autowired
    protected RaidUpdateRequestFactory raidUpdateRequestFactory;
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

    protected RaidCreateRequest newCreateRequest() {
        String initialTitle = getClass().getSimpleName() + "." + getName() +
                idFactory.generateUniqueId();
        final var descriptions = new ArrayList<Description>();
        descriptions.add(new Description()
                .language(new Language()
                        .schemaUri(LANGUAGE_SCHEMA_URI)
                        .id(LANGUAGE_ID))
                .type(new DescriptionTypeWithSchemaUri()
                        .id(PRIMARY_DESCRIPTION_TYPE)
                        .schemaUri(DESCRIPTION_TYPE_SCHEMA_URI))
                .text("stuff about the int test raid")
                .language(new Language()
                        .schemaUri(LANGUAGE_SCHEMA_URI)
                        .id(LANGUAGE_ID)));


        return new RaidCreateRequest()
                .title(List.of(new Title()
                        .language(new Language()
                                .schemaUri(LANGUAGE_SCHEMA_URI)
                                .id(LANGUAGE_ID)
                        )
                        .type(new TitleTypeWithSchemaUri()
                                .id(PRIMARY_TITLE_TYPE)
                                .schemaUri(TITLE_TYPE_SCHEMA_URI))
                        .text(initialTitle)
                        .startDate(today.format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .date(new Date().startDate(today.format(DateTimeFormatter.ISO_LOCAL_DATE)))
                .description(descriptions)

                .contributor(List.of(contributor(
                        REAL_TEST_ORCID, LEADER_POSITION, SOFTWARE_CONTRIBUTOR_ROLE, today)))
                .organisation(List.of(organisation(
                        REAL_TEST_ROR, LEAD_RESEARCH_ORGANISATION, today)))
                .access(new Access()
                        .accessStatement(new AccessStatement()
                                .text("Embargoed")
                                .language(new Language()
                                        .id(LANGUAGE_ID)
                                        .schemaUri(LANGUAGE_SCHEMA_URI)))
                        .type(new AccessTypeWithSchemaUri()
                                .id(EMBARGOED_ACCESS_TYPE)
                                .schemaUri(ACCESS_TYPE_SCHEMA_URI))
                        .embargoExpiry(LocalDate.now().plusMonths(1)))
                .spatialCoverage(List.of(new SpatialCoverage()
                        .language(new Language()
                                .id(LANGUAGE_ID)
                                .schemaUri(LANGUAGE_SCHEMA_URI))
                        .id(GEONAMES_MELBOURNE)
                        .place("Melbourne")
                        .schemaUri(GEONAMES_SCHEMA_URI)))
                .traditionalKnowledgeLabel(List.of(
                        new TraditionalKnowledgeLabel()
                                .id("https://localcontexts.org/label/tk-attribution/")
                                .schemaUri("https://localcontexts.org/labels/traditional-knowledge-labels/"),
                        new TraditionalKnowledgeLabel()
                                .id("https://localcontexts.org/label/bc-provenance/")
                                .schemaUri("https://localcontexts.org/labels/biocultural-labels/")
                ));
    }

    private RaidUpdateRequest mapReadToUpdate(RaidDto read) {
        return new RaidUpdateRequest()
                .identifier(read.getIdentifier())
                .title(read.getTitle())
                .date(read.getDate())
                .description(read.getDescription())
                .access(read.getAccess())
                .alternateUrl(read.getAlternateUrl())
                .contributor(read.getContributor())
                .organisation(read.getOrganisation())
                .subject(read.getSubject())
                .relatedRaid(read.getRelatedRaid())
                .relatedObject(read.getRelatedObject())
                .alternateIdentifier(read.getAlternateIdentifier())
                .spatialCoverage(read.getSpatialCoverage())
                .traditionalKnowledgeLabel(read.getTraditionalKnowledgeLabel());
    }

    public Contributor contributor(
            final String orcid,
            final String position,
            String role,
            LocalDate startDate
    ) {
        return new Contributor()
                .id(orcid)
                .contact(true)
                .leader(true)
                .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .position(List.of(new ContributorPositionWithSchemaUri()
                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                        .id(position)
                        .startDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .role(List.of(
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
                .schemaUri(ORGANISATION_IDENTIFIER_SCHEMA_URI)
                .role(List.of(
                        new OrganisationRoleWithSchemaUri()
                                .schemaUri(ORGANISATION_ROLE_SCHEMA_URI)
                                .id(role)
                                .startDate(today.format(DateTimeFormatter.ISO_LOCAL_DATE))));
    }
}