package au.org.raid.inttest;

import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.stub.util.IdFactory;
import au.org.raid.idl.raidv2.api.RaidApi;
import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.config.IntegrationTestConfig;
import au.org.raid.inttest.service.BootstrapAuthTokenService;
import au.org.raid.inttest.service.RaidUpdateRequestFactory;
import au.org.raid.inttest.service.TestClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static au.org.raid.inttest.service.TestConstants.*;

@SpringBootTest(classes = IntegrationTestConfig.class)
public class AbstractIntegrationTest {
    protected static final Long UQ_SERVICE_POINT_ID = 20000002L;
    protected final IdFactory idFactory = new IdFactory();
    protected LocalDate today = LocalDate.now();
    protected RaidCreateRequest createRequest;

    protected RaidApi raidApi;
    protected IdentifierParser identifierParser;

    @Value("${raid.test.api.raid-au-user-token}")
    private String raidAuUserToken;

    @Value("${raid.test.api.uq-admin-token}")
    protected String uqAdminToken;

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
        createRequest = newCreateRequest();
        raidApi = testClient.raidApi(raidAuUserToken);
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
                .type(new DescriptionType()
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
                        .type(new TitleType()
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
                        .statement(new AccessStatement()
                                .text("Embargoed")
                                .language(new Language()
                                        .id(LANGUAGE_ID)
                                        .schemaUri(LANGUAGE_SCHEMA_URI)))
                        .type(new AccessType()
                                .id(EMBARGOED_ACCESS_TYPE)
                                .schemaUri(ACCESS_TYPE_SCHEMA_URI))
                        .embargoExpiry(LocalDate.now().plusMonths(1)))
                .spatialCoverage(List.of(new SpatialCoverage()
                        .id(GEONAMES_MELBOURNE)
                        .place(List.of(new SpatialCoveragePlace()
                                .text("Melbourne")
                                .language(new Language()
                                        .id(LANGUAGE_ID)
                                        .schemaUri(LANGUAGE_SCHEMA_URI))))
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
                .schemaUri(CONTRIBUTOR_IDENTIFIER_SCHEMA_URI)
                .position(List.of(new ContributorPosition()
                        .schemaUri(CONTRIBUTOR_POSITION_SCHEMA_URI)
                        .id(position)
                        .startDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))))
                .role(List.of(
                        new ContributorRole()
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
                        new OrganisationRole()
                                .schemaUri(ORGANISATION_ROLE_SCHEMA_URI)
                                .id(role)
                                .startDate(today.format(DateTimeFormatter.ISO_LOCAL_DATE))));
    }
}