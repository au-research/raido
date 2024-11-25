package au.org.raid.api.controller;

import au.org.raid.api.endpoint.raidv2.RaidExceptionHandler;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.util.FileUtil;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.TitleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrganisationControllerTest {
    public static final String REGISTRATION_AGENCY_ID = "https://ror.org/038sjwq14";
    public static final String IDENTIFIER_OWNER_ID = "https://ror.org/02stey378";

    private static final String SERVICE_POINT_GROUP_ID = UUID.randomUUID().toString();
    private static final Long SERVICE_POINT_ID = 20_000_000L;
    private static final String PREFIX = "10378.1";
    private static final String SUFFIX = "1696639";

    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    private MockMvc mockMvc;

    @Mock
    private RaidIngestService raidIngestService;

    @InjectMocks
    private OrganisationController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RaidExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void listRaids_ReturnsOk() throws Exception {
        final var title = "C. Japonicum Genome";
        final var startDate = LocalDate.now();
        final var ror = "038sjwq14";
        final var handle = new IdentifierHandle("10378.1", "1696639");
        final var id = new IdentifierUrl("https://raid.org.au", handle);
        final var endDate = startDate.plusMonths(6);

        final var output = createRaidForGet(title, startDate);

//        try (MockedStatic<SecurityContextHolder> securityContextHolder = Mockito.mockStatic(SecurityContextHolder.class)) {
//
//            final var jwt = getJwt();
//
//            final var jwtAuthenticationToken = mock(JwtAuthenticationToken.class);
//            when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
//
//            final var securityContext = mock(SecurityContext.class);
//            when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
//
//            securityContextHolder.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(raidIngestService.findAllByOrganisation(ror)).thenReturn(Collections.singletonList(output));

            mockMvc.perform(get("/organisation/%s".formatted(ror)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].identifier.id", Matchers.is(id.formatUrl())))
                    .andExpect(jsonPath("$[0].identifier.schemaUri", Matchers.is(SchemaValues.RAID_SCHEMA_URI.getUri())))
                    .andExpect(jsonPath("$[0].identifier.registrationAgency.id", Matchers.is(REGISTRATION_AGENCY_ID)))
                    .andExpect(jsonPath("$[0].identifier.registrationAgency.schemaUri", Matchers.is(SchemaValues.ROR_SCHEMA_URI.getUri())))
                    .andExpect(jsonPath("$[0].identifier.owner.id", Matchers.is(IDENTIFIER_OWNER_ID)))
                    .andExpect(jsonPath("$[0].identifier.owner.schemaUri", Matchers.is(SchemaValues.ROR_SCHEMA_URI.getUri())))
                    .andExpect(jsonPath("$[0].identifier.owner.servicePoint", Matchers.is(SERVICE_POINT_ID.intValue())))
                    .andExpect(jsonPath("$[0].title[0].text", Matchers.is(title)))
                    .andExpect(jsonPath("$[0].title[0].type.id", Matchers.is(SchemaValues.PRIMARY_TITLE_TYPE.getUri())))
                    .andExpect(jsonPath("$[0].title[0].type.schemaUri", Matchers.is(SchemaValues.TITLE_TYPE_SCHEMA.getUri())))
                    .andExpect(jsonPath("$[0].title[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].title[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].date.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].date.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].description[0].text", Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. ")))
                    .andExpect(jsonPath("$[0].description[0].type.id", Matchers.is(SchemaValues.PRIMARY_DESCRIPTION_TYPE.getUri())))
                    .andExpect(jsonPath("$[0].description[0].type.schemaUri", Matchers.is( SchemaValues.DESCRIPTION_TYPE_SCHEMA.getUri())))
                    .andExpect(jsonPath("$[0].access.type.id", Matchers.is(SchemaValues.ACCESS_TYPE_OPEN.getUri())))
                    .andExpect(jsonPath("$[0].access.type.schemaUri", Matchers.is(SchemaValues.ACCESS_TYPE_SCHEMA.getUri())))
                    .andExpect(jsonPath("$[0].access.statement.text", Matchers.is("This RAiD is closed")))
                    .andExpect(jsonPath("$[0].contributor[0].id", Matchers.is("https://orcid.org/0000-0002-4368-8058")))
                    .andExpect(jsonPath("$[0].contributor[0].schemaUri", Matchers.is("https://orcid.org/")))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].schemaUri", Matchers.is(SchemaValues.CONTRIBUTOR_POSITION_SCHEMA_URI.getUri())))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].id", Matchers.is(SchemaValues.PRINCIPAL_INVESTIGATOR_CONTRIBUTOR_POSITION_ROLE.getUri())))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].contributor[0].role[0].schemaUri", Matchers.is(SchemaValues.CONTRIBUTOR_ROLE_SCHEMA_URI.getUri())))
                    .andExpect(jsonPath("$[0].contributor[0].role[0].id", Matchers.is("https://credit.niso.org/contributor-roles/formal-analysis/")))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].id", Matchers.is(SchemaValues.LEAD_RESEARCH_ORGANISATION_ROLE.getUri())))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].schemaUri", Matchers.is(SchemaValues.ORGANISATION_ROLE_SCHEMA_URI.getUri())))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].organisation[0].id", Matchers.is("https://ror.org/04qw24q55")))
                    .andExpect(jsonPath("$[0].organisation[0].schemaUri", Matchers.is(SchemaValues.ROR_SCHEMA_URI.getUri())));
//        }
    }

    private Jwt getJwt() {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim(JwtClaimNames.SUB, "user")
                .claim("scope", "read")
                .claim("service_point_group_id", SERVICE_POINT_GROUP_ID)
                .build();
    }

    private RaidDto createRaidForGet(final String title, final LocalDate startDate) throws IOException {
        final String json = FileUtil.resourceContent("/fixtures/raid.json");

        var raid = objectMapper.readValue(json, RaidDto.class);

        final var titleType = new TitleType()
                .id(SchemaValues.PRIMARY_TITLE_TYPE.getUri())
                .schemaUri(SchemaValues.TITLE_TYPE_SCHEMA.getUri());

        raid.getTitle().get(0)
                .startDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(startDate.plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE))
                .text(title)
                .type(titleType);

        raid.getDate()
                .startDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(startDate.plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE));

        raid.getContributor().get(0).getPosition().get(0)
                .startDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(startDate.plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE));

        raid.getOrganisation().get(0).getRole().get(0)
                .startDate(startDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                .endDate(startDate.plusMonths(6).format(DateTimeFormatter.ISO_LOCAL_DATE));

        return raid;
    }
}