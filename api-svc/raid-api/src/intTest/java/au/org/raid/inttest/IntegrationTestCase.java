package au.org.raid.inttest;

import au.org.raid.api.Api;
import au.org.raid.api.service.stub.util.IdFactory;
import au.org.raid.api.spring.config.environment.EnvironmentProps;
import au.org.raid.api.util.Nullable;
import au.org.raid.db.jooq.enums.UserRole;
import au.org.raid.idl.raidv1.api.RaidV1Api;
import au.org.raid.idl.raidv2.api.*;
import au.org.raid.idl.raidv2.model.*;
import au.org.raid.inttest.auth.BootstrapAuthTokenService;
import au.org.raid.inttest.config.IntTestProps;
import au.org.raid.inttest.config.IntegrationTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Contract;
import feign.Feign;
import feign.Logger;
import feign.Logger.Level;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import feign.slf4j.Slf4jLogger;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.api.spring.config.RaidWebSecurityConfig.RAID_V1_API;
import static au.org.raid.db.jooq.enums.IdProvider.RAIDO_API;
import static au.org.raid.db.jooq.enums.UserRole.OPERATOR;
import static au.org.raid.inttest.config.IntegrationTestConfig.REST_TEMPLATE_VALUES_ONLY_ENCODING;
import static au.org.raid.inttest.util.MinimalRaidTestData.REAL_TEST_ROR;
import static java.time.ZoneOffset.UTC;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest(classes = Api.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ContextConfiguration(classes = IntegrationTestConfig.class)
public abstract class IntegrationTestCase {
    // be careful, 25 char max column length
    public static final String INT_TEST_ROR = "https://ror.org/038sjwq14";
    protected static final IdFactory idFactory = new IdFactory("inttest");
//    @RegisterExtension
//    protected static JettyTestServer jettyTestServer = new JettyTestServer();
    @Autowired
    protected RestTemplate rest;
    @Autowired
    @Qualifier(REST_TEMPLATE_VALUES_ONLY_ENCODING)
    protected RestTemplate valuesEncodingRest;
    @Autowired
    protected IntTestProps props;
    @Autowired
    protected DSLContext db;
    @Autowired
    protected BootstrapAuthTokenService bootstrapTokenSvc;
    @Autowired
    protected Contract feignContract;
    @Autowired
    protected ObjectMapper mapper;
    @Autowired
    protected EnvironmentProps env;
    protected String raidV1TestToken;
    protected String operatorToken;
    protected RaidoApiUtil raidoApi;
    private TestInfo testInfo;

    public static OrganisationBlock createDummyOrganisation(LocalDate today) {
        return new OrganisationBlock().
                id(REAL_TEST_ROR).
                identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_).
                roles(List.of(
                        new OrganisationRole().
                                roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_).
                                role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION)
                                .startDate(today)));
    }

    /**
     * IMPROVE: do this in a beforeAll, so the time isn't counted as
     * part of the first tests execution time.  But need to figure out how to
     * obtain the test's spring context in a static context.
     */
    @BeforeEach
    public void setupTestToken() {
        if (raidV1TestToken != null) {
            return;
        }

        raidV1TestToken = bootstrapTokenSvc.initRaidV1TestToken();
        operatorToken = bootstrapTokenSvc.bootstrapToken(
                RAIDO_SP_ID, "intTestOperatorApiToken", OPERATOR);

        raidoApi = new RaidoApiUtil(publicExperimentalClient(), mapper);
    }

    @BeforeEach
    public void init(TestInfo testInfo) {
        this.testInfo = testInfo;
    }

    public String getName() {
        return testInfo.getDisplayName().
                // the brackets aren't filename safe
                        replaceAll("[()]", "");
    }

    /**
     * Once we figure out how to use the token statically, would like to figure
     * out how to integrate this into Spring better.  Would like to inject
     * the client as autowired, but not sure how the interceptor would work to
     * get the auth token, especially as we start wanting to work with multiple
     * users in the scope of a single test.
     */
    public RaidV1Api raidV1Client() {
        return Feign.builder().
                client(new OkHttpClient()).
                encoder(new JacksonEncoder(mapper)).
                decoder(new JacksonDecoder(mapper)).
                contract(feignContract).
                requestInterceptor(request ->
                        request.header(AUTHORIZATION, "Bearer " + raidV1TestToken)).
                logger(new Slf4jLogger(RaidV1Api.class)).
                logLevel(Level.FULL).
                target(RaidV1Api.class, props.getRaidoServerUrl() + RAID_V1_API);
    }

    /**
     * Acts "as" the bootstrap operator token.
     */
    public BasicRaidExperimentalApi basicRaidExperimentalClient() {
        return basicRaidExperimentalClient(operatorToken);
    }

    /**
     * Uses the bootstrapped `operatorToken` to create a new api-key with the
     * given params, then returns a newly generated token.
     */
    public GenerateApiTokenResponse createApiKeyUser(
            long servicePointId,
            String subject,
            UserRole role
    ) {
        var adminApi = adminExperimentalClientAs(operatorToken);
        LocalDateTime expiry = LocalDateTime.now().plusDays(30);

        var apiKey = adminApi.updateApiKey(new ApiKey().
                servicePointId(servicePointId).
                idProvider(RAIDO_API.getLiteral()).
                role(role.getLiteral()).
                subject(subject).
                enabled(true).
                tokenCutoff(expiry.atOffset(UTC))
        ).getBody();
        assert apiKey != null;
        return adminApi.generateApiToken(new GenerateApiTokenRequest().
                apiKeyId(apiKey.getId())).getBody();
    }

    public BasicRaidExperimentalApi basicRaidExperimentalClient(String token) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(mapper))
                .decoder(new ResponseEntityDecoder(new JacksonDecoder(mapper)))
                .contract(feignContract)
                .requestInterceptor(request ->
                        request.header(AUTHORIZATION, "Bearer " + token))
                .logger(new Slf4jLogger(BasicRaidExperimentalApi.class))
                .logLevel(Level.FULL)
                .target(BasicRaidExperimentalApi.class, props.getRaidoServerUrl());
    }

    public PublicExperimentalApi publicExperimentalClient() {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(mapper))
                .decoder(new ResponseEntityDecoder(new JacksonDecoder(mapper)))
                .contract(feignContract)
                .logger(new Slf4jLogger(PublicExperimentalApi.class))
                .logLevel(Level.FULL)
                .target(PublicExperimentalApi.class, props.getRaidoServerUrl());
    }

    public UnapprovedExperimentalApi unapprovedClient(String token) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(mapper))
                .decoder(new ResponseEntityDecoder(new JacksonDecoder(mapper)))
                .contract(feignContract)
                .requestInterceptor(request ->
                        request.header(AUTHORIZATION, "Bearer " + token))
                .logger(new Slf4jLogger(UnapprovedExperimentalApi.class))
                .logLevel(Level.FULL)
                .target(UnapprovedExperimentalApi.class, props.getRaidoServerUrl());
    }

    public AdminExperimentalApi adminExperimentalClientAs(String token) {
        return Feign.builder()
                .client(new OkHttpClient())
                .encoder(new JacksonEncoder(mapper))
                .decoder(new ResponseEntityDecoder(new JacksonDecoder(mapper)))
                .contract(feignContract)
                .requestInterceptor(request ->
                        request.header(AUTHORIZATION, "Bearer " + token))
                .logger(new Slf4jLogger(AdminExperimentalApi.class))
                .logLevel(Level.FULL)
                .target(AdminExperimentalApi.class, props.getRaidoServerUrl());
    }

    public String raidoApiServerUrl(String url) {
        //noinspection HttpUrlsUsage
        return String.format("http://%s%s", props.raidoApiServer, url);
    }

    protected String getTestPrefix(TestInfo testInfo) {
        return testInfo.getTestClass().orElseThrow().getSimpleName();
    }

    public PublicServicePoint findPublicServicePoint(String name) {
        return publicExperimentalClient().publicListServicePoint().getBody().stream().
                filter(i -> i.getName().contains(name)).
                findFirst().orElseThrow();
    }

    /**
     * Ok, I'm just being a crazy-person at this point.
     */
    public PublicServicePoint findOtherPublicServicePoint(Long anySpExceptThis) {
        return publicExperimentalClient().publicListServicePoint().getBody().stream().
                filter(i -> !i.getId().equals(anySpExceptThis)).
                findFirst().orElseThrow();
    }

    public ServicePoint createServicePoint(@Nullable String name) {

        var spName = name != null ? name :
                "%s-%s".formatted(
                        this.getClass().getSimpleName(),
                        idFactory.generateUniqueId());

        var adminApiAsOp = adminExperimentalClientAs(operatorToken);

        return adminApiAsOp.updateServicePoint(new ServicePoint().
                identifierOwner(INT_TEST_ROR).
                name(spName).
                adminEmail("").
                techEmail("").
                enabled(true).
                appWritesEnabled(true)).getBody();
    }

    public RaidoStableV1Api basicRaidStableClient(String token) {
        return Feign.builder().
                client(new OkHttpClient()).
                encoder(new JacksonEncoder(mapper)).
                decoder(new JacksonDecoder(mapper)).
                errorDecoder(new RaidApiExceptionDecoder(mapper)).
                contract(feignContract).
                requestInterceptor(request ->
                        request.header(AUTHORIZATION, "Bearer " + token)).
                logger(new Slf4jLogger(RaidoStableV1Api.class)).
                logLevel(Logger.Level.FULL).
                target(RaidoStableV1Api.class, props.getRaidoServerUrl());
    }

    public RaidoStableV1Api basicRaidStableClient() {
        return basicRaidStableClient(operatorToken);
    }
}