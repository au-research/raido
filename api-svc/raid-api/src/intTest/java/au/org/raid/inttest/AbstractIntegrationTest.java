package au.org.raid.inttest;

import au.org.raid.idl.raidv2.api.RaidApi;
import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.config.IntegrationTestConfig;
import au.org.raid.inttest.service.RaidUpdateRequestFactory;
import au.org.raid.inttest.service.TestClient;
import au.org.raid.inttest.service.TokenService;
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
import java.util.UUID;

import static au.org.raid.inttest.service.TestConstants.*;

@SpringBootTest(classes = IntegrationTestConfig.class)
public class AbstractIntegrationTest {
    protected static final Long UQ_SERVICE_POINT_ID = 20000002L;
    protected LocalDate today = LocalDate.now();
    protected RaidCreateRequest createRequest;

    protected RaidApi raidApi;

    @Value("${raid.test.auth.admin.user}")
    protected String adminUser;

    @Value("${raid.test.auth.admin.password}")
    protected String adminPassword;

    @Value("${raid.test.auth.raid-au.user}")
    protected String raidAuUser;

    @Value("${raid.test.auth.raid-au.password}")
    protected String raidAuPassword;

    @Value("${raid.test.auth.uq.user}")
    private String uqUser;

    @Value("${raid.test.auth.uq.password}")
    private String uqPassword;
    protected String raidAuToken;

    protected String adminToken;
    protected String uqToken;

    @Autowired
    protected TestClient testClient;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected Contract feignContract;
    @Autowired
    protected RaidUpdateRequestFactory raidUpdateRequestFactory;

    @Autowired
    private TokenService tokenService;
    private TestInfo testInfo;

    @BeforeEach
    public void setupTestToken() {
        adminToken = tokenService.getToken(adminUser, adminPassword);
        raidAuToken = tokenService.getToken(raidAuUser, raidAuPassword);
        uqToken = tokenService.getToken(uqUser, uqPassword);
        createRequest = newCreateRequest();
        raidApi = testClient.raidApi(raidAuToken);
    }

    @BeforeEach
    public void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    protected String getName() {
        return testInfo.getDisplayName();
    }

    protected RaidCreateRequest newCreateRequest() {
        String initialTitle = UUID.randomUUID().toString();
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
                        REAL_TEST_ORCID, PRINCIPAL_INVESTIGATOR_POSITION, SOFTWARE_CONTRIBUTOR_ROLE, today, CONTRIBUTOR_EMAIL)))
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
                .subject(List.of(
                        new Subject()
                                .id("https://linked.data.gov.au/def/anzsrc-for/2020/3702")
                                .schemaUri("https://vocabs.ardc.edu.au/viewById/316")
                                .keyword(List.of(new SubjectKeyword()
                                        .language(new Language()
                                                .id("eng")
                                                .schemaUri("https://www.iso.org/standard/74575.html"))
                                                .text("ENES")
                                ))))
                .traditionalKnowledgeLabel(List.of(
                        new TraditionalKnowledgeLabel()
                                .id("https://localcontexts.org/label/tk-attribution/")
                                .schemaUri("https://localcontexts.org/labels/traditional-knowledge-labels/"),
                        new TraditionalKnowledgeLabel()
                                .id("https://localcontexts.org/label/bc-provenance/")
                                .schemaUri("https://localcontexts.org/labels/biocultural-labels/")
                ));
    }
    public Contributor contributor(
            final String orcid,
            final String position,
            final String role,
            final LocalDate startDate,
            final String email
    ) {
        return new Contributor()
//                .id(orcid)
                .contact(true)
                .leader(true)
                .email(email)
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