package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.controller.RaidController;
import au.org.raid.api.exception.CrossAccountAccessException;
import au.org.raid.api.exception.ResourceNotFoundException;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.datacite.DataciteService;
import au.org.raid.api.service.raid.RaidService;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.api.util.FileUtil;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.api.validator.ValidationService;
import au.org.raid.idl.raidv2.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static au.org.raid.api.util.FileUtil.resourceContent;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RaidControllerTest {
    private static final String ACCESS_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1";

    private static final String ACCESS_TYPE_CLOSED =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json";

    private static final String PRIMARY_TITLE_TYPE =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json";

    private static final String TITLE_TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1";

    private static final String PRIMARY_DESCRIPTION_TYPE =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json";

    private static final String DESCRIPTION_TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1";
    final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    private MockMvc mockMvc;
    @Mock
    private RaidService raidService;
    @Mock
    private ValidationService validationService;
    @Mock
    private RaidIngestService raidIngestService;
    private DataciteService dataciteService;
    @Mock
    private RaidHistoryService raidHistoryService;
    @InjectMocks
    private RaidController controller;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RaidExceptionHandler())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void mintRaidV1_ReturnsRedactedInternalServerErrorOnDataAccessException() throws Exception {
        final var servicePointId = 999L;
        final var raid = createRaidForPost();

        final ApiToken apiToken = mock(ApiToken.class);
        when(apiToken.getServicePointId()).thenReturn(servicePointId);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);
            when(raidService.isEditable(any(ApiToken.class), anyLong())).thenReturn(true);

            doThrow(DataAccessException.class)
                    .when(raidService).mint(any(RaidCreateRequest.class), eq(servicePointId));

            mockMvc.perform(post("/raid/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(raid))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string(""))
                    .andReturn();
        }
    }

    @Test
    void mintRaidV1_ReturnsForbiddenWhenEditingInAppAndDisabledInServicePoint() throws Exception {
        final Long servicePointId = 999L;

        final var raidForPost = createRaidForPost();

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final var user = ApiToken.ApiTokenBuilder
                    .anApiToken()
                    .withAppUserId(0L)
                    .withEmail("user-email")
                    .withRole("user-role")
                    .withSubject("user-subject")
                    .withAppUserId(1L)
                    .withServicePointId(servicePointId)
                    .withClientId("user-client-id")
                    .build();

            authzUtil.when(AuthzUtil::getApiToken).thenReturn(user);

            when(raidService.isEditable(user, servicePointId)).thenReturn(false);

            mockMvc.perform(post("/raid/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(raidForPost))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.type", Matchers.is("https://raid.org.au/errors#InvalidAccessException")))
                    .andExpect(jsonPath("$.title", Matchers.is("Forbidden")))
                    .andExpect(jsonPath("$.status", Matchers.is(403)))
                    .andExpect(jsonPath("$.detail", Matchers.is("This service point does not allow Raids to be edited in the app.")))
                    .andExpect(jsonPath("$.instance", Matchers.is("https://raid.org.au")))
            ;
        }
    }

    @Test
    void mintRaidV1_ReturnsBadRequest() throws Exception {
        final var servicePointId = 999L;
        final var validationFailureMessage = "validation failure message";
        final var validationFailureType = "validation failure type";
        final var validationFailureFieldId = "validation failure id";

        final var raid = createRaidForPost();

        final var validationFailure = new ValidationFailure();
        validationFailure.setFieldId(validationFailureFieldId);
        validationFailure.setMessage(validationFailureMessage);
        validationFailure.setErrorType(validationFailureType);

        final ApiToken apiToken = mock(ApiToken.class);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);
            when(raidService.isEditable(any(ApiToken.class), anyLong())).thenReturn(true);


            when(validationService.validateForCreate(any(RaidCreateRequest.class)))
                    .thenReturn(List.of(validationFailure));

            final MvcResult mvcResult = mockMvc.perform(post("/raid/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(raid))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andReturn();

            final ValidationFailureResponse validationFailureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationFailureResponse.class);

            assertThat(validationFailureResponse.getType(), Matchers.is("https://raid.org.au/errors#ValidationException"));
            assertThat(validationFailureResponse.getTitle(), Matchers.is("There were validation failures."));
            assertThat(validationFailureResponse.getStatus(), Matchers.is(400));
            assertThat(validationFailureResponse.getDetail(), Matchers.is("Request had 1 validation failure(s). See failures for more details..."));
            assertThat(validationFailureResponse.getInstance(), Matchers.is("https://raid.org.au"));
            assertThat(validationFailureResponse.getFailures().get(0).getFieldId(), Matchers.is(validationFailureFieldId));
            assertThat(validationFailureResponse.getFailures().get(0).getErrorType(), Matchers.is(validationFailureType));
            assertThat(validationFailureResponse.getFailures().get(0).getMessage(), Matchers.is(validationFailureMessage));

            verify(raidService, never()).mint(any(RaidCreateRequest.class), eq(servicePointId));
            verify(raidService, never()).findByHandle(anyString());
        }
    }

    @Test
    void mintRaidV1_ReturnsOk() throws Exception {
        final Long servicePointId = 999L;
        final var title = "test-title";
        final var startDate = LocalDate.now();
        final var handle = new IdentifierHandle("10378.1", "1696639");
        final var id = new IdentifierUrl("https://raid.org.au", handle);
        final var endDate = startDate.plusMonths(6);

        final var raidForPost = createRaidForPost();
        final var raidForGet = createRaidForGet(title, startDate);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final var user = ApiToken.ApiTokenBuilder
                    .anApiToken()
                    .withAppUserId(0L)
                    .withEmail("user-email")
                    .withRole("user-role")
                    .withSubject("user-subject")
                    .withAppUserId(1L)
                    .withServicePointId(servicePointId)
                    .withClientId("user-client-id")
                    .build();

            authzUtil.when(AuthzUtil::getApiToken).thenReturn(user);
            when(raidService.isEditable(any(ApiToken.class), anyLong())).thenReturn(true);


            when(validationService.validateForCreate(any(RaidCreateRequest.class))).thenReturn(Collections.emptyList());

            when(raidService.mint(any(RaidCreateRequest.class), eq(servicePointId))).thenReturn(raidForGet);

            mockMvc.perform(post("/raid/")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(raidForPost))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.identifier.id", Matchers.is(id.formatUrl())))
                    .andExpect(jsonPath("$.identifier.schemaUri", Matchers.is("https://raid.org")))
                    .andExpect(jsonPath("$.identifier.registrationAgency.id", Matchers.is("https://ror.org/038sjwq14")))
                    .andExpect(jsonPath("$.identifier.registrationAgency.schemaUri", Matchers.is("https://ror.org/")))
                    .andExpect(jsonPath("$.identifier.owner.id", Matchers.is("https://ror.org/02stey378")))
                    .andExpect(jsonPath("$.identifier.owner.schemaUri", Matchers.is("https://ror.org/")))
                    .andExpect(jsonPath("$.identifier.owner.servicePoint", Matchers.is(20000001)))
                    .andExpect(jsonPath("$.identifier.raidAgencyUrl", Matchers.is(id.formatUrl())))
                    .andExpect(jsonPath("$.title[0].text", Matchers.is(title)))
                    .andExpect(jsonPath("$.title[0].type.id", Matchers.is("https://github.com/au-research/raid-metadata/blob/main/scheme/title/type/v1/primary.json")))
                    .andExpect(jsonPath("$.title[0].type.schemaUri", Matchers.is("https://github.com/au-research/raid-metadata/tree/main/scheme/title/type/v1")))
                    .andExpect(jsonPath("$.title[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.title[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.date.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.date.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.description[0].text", Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. ")))
                    .andExpect(jsonPath("$.description[0].type.id", Matchers.is(PRIMARY_DESCRIPTION_TYPE)))
                    .andExpect(jsonPath("$.description[0].type.schemaUri", Matchers.is(DESCRIPTION_TYPE_SCHEMA_URI)))
                    .andExpect(jsonPath("$.access.type.id", Matchers.is(SchemaValues.ACCESS_TYPE_EMBARGOED.getUri())))
                    .andExpect(jsonPath("$.access.embargoExpiry", Matchers.is("2024-01-01")))
                    .andExpect(jsonPath("$.access.type.schemaUri", Matchers.is(ACCESS_SCHEMA_URI)))
                    .andExpect(jsonPath("$.access.statement.text", Matchers.is("This RAiD is closed")))
                    .andExpect(jsonPath("$.contributor[0].id", Matchers.is("https://orcid.org/0000-0002-4368-8058")))
                    .andExpect(jsonPath("$.contributor[0].schemaUri", Matchers.is("https://orcid.org/")))
                    .andExpect(jsonPath("$.contributor[0].position[0].schemaUri", Matchers.is("https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1")))
                    .andExpect(jsonPath("$.contributor[0].position[0].id", Matchers.is("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json")))
                    .andExpect(jsonPath("$.contributor[0].position[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.contributor[0].position[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.contributor[0].role[0].schemaUri", Matchers.is("https://credit.niso.org")))
                    .andExpect(jsonPath("$.contributor[0].role[0].id", Matchers.is("https://credit.niso.org/contributor-roles/formal-analysis/")))
                    .andExpect(jsonPath("$.organisation[0].role[0].id", Matchers.is("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/lead-research-organisation.json")))
                    .andExpect(jsonPath("$.organisation[0].role[0].schemaUri", Matchers.is("https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1")))
                    .andExpect(jsonPath("$.organisation[0].role[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.organisation[0].role[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.organisation[0].id", Matchers.is("https://ror.org/04qw24q55")))
                    .andExpect(jsonPath("$.organisation[0].schemaUri", Matchers.is("https://ror.org/")));
        }
    }

    @Test
    void updateRaidV1_ReturnsBadRequest() throws Exception {
        final var prefix = "10.38";
        final var suffix = "99999";
        final var handle = String.join("/", prefix, suffix);
        final var validationFailureMessage = "validation failure message";
        final var validationFailureType = "validation failure type";
        final var validationFailureFieldId = "validation failure id";

        final var input = createRaidForPut();

        final var validationFailure = new ValidationFailure();
        validationFailure.setFieldId(validationFailureFieldId);
        validationFailure.setMessage(validationFailureMessage);
        validationFailure.setErrorType(validationFailureType);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);
            when(raidService.isEditable(any(ApiToken.class), anyLong())).thenReturn(true);


            when(validationService.validateForUpdate(eq(handle), any(RaidUpdateRequest.class))).thenReturn(List.of(validationFailure));

            final MvcResult mvcResult = mockMvc.perform(put(String.format("/raid/%s/%s", prefix, suffix))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andReturn();

            final ValidationFailureResponse validationFailureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationFailureResponse.class);

            assertThat(validationFailureResponse.getType(), Matchers.is("https://raid.org.au/errors#ValidationException"));
            assertThat(validationFailureResponse.getTitle(), Matchers.is("There were validation failures."));
            assertThat(validationFailureResponse.getStatus(), Matchers.is(400));
            assertThat(validationFailureResponse.getDetail(), Matchers.is("Request had 1 validation failure(s). See failures for more details..."));
            assertThat(validationFailureResponse.getInstance(), Matchers.is("https://raid.org.au"));
            assertThat(validationFailureResponse.getFailures().get(0).getFieldId(), Matchers.is(validationFailureFieldId));
            assertThat(validationFailureResponse.getFailures().get(0).getErrorType(), Matchers.is(validationFailureType));
            assertThat(validationFailureResponse.getFailures().get(0).getMessage(), Matchers.is(validationFailureMessage));

            verify(raidService, never()).update(input);
        }
    }

    @Test
    void updateRaidV1_ReturnsForbiddenWhenEditingInAppAndDisabledInServicePoint() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final var servicePointId = 20000001L;

        final var input = createRaidForPut();

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken user = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(user);

            when(raidService.isEditable(user, servicePointId)).thenReturn(false);

            mockMvc.perform(put(String.format("/raid/%s/%s", prefix, suffix))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.type", Matchers.is("https://raid.org.au/errors#InvalidAccessException")))
                    .andExpect(jsonPath("$.title", Matchers.is("Forbidden")))
                    .andExpect(jsonPath("$.status", Matchers.is(403)))
                    .andExpect(jsonPath("$.detail", Matchers.is("This service point does not allow Raids to be edited in the app.")))
                    .andExpect(jsonPath("$.instance", Matchers.is("https://raid.org.au")));
        }
    }

    @Test
    void updateRaidV1_ReturnsOk() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final Long servicePointId = 20000001L;
        final var title = "test-title";
        final var startDate = LocalDate.now();
        final var handle = new IdentifierHandle(prefix, suffix);
        final var id = new IdentifierUrl("https://raid.org.au", handle);
        final var endDate = startDate.plusMonths(6);

        final var input = createRaidForPut();
        final var output = createRaidForGet(title, startDate);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);
            when(raidService.isEditable(any(ApiToken.class), anyLong())).thenReturn(true);

            when(validationService.validateForUpdate(String.join("/", prefix, suffix), input))
                    .thenReturn(Collections.emptyList());

            when(raidService.update(input)).thenReturn(output);

            mockMvc.perform(put(String.format("/raid/%s/%s", prefix, suffix))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.identifier.id", Matchers.is(id.formatUrl())))
                    .andExpect(jsonPath("$.identifier.schemaUri", Matchers.is("https://raid.org")))
                    .andExpect(jsonPath("$.identifier.registrationAgency.id", Matchers.is("https://ror.org/038sjwq14")))
                    .andExpect(jsonPath("$.identifier.registrationAgency.schemaUri", Matchers.is("https://ror.org/")))
                    .andExpect(jsonPath("$.identifier.owner.id", Matchers.is("https://ror.org/02stey378")))
                    .andExpect(jsonPath("$.identifier.owner.schemaUri", Matchers.is("https://ror.org/")))
                    .andExpect(jsonPath("$.identifier.owner.servicePoint", Matchers.is(servicePointId.intValue())))
                    .andExpect(jsonPath("$.title[0].text", Matchers.is(title)))
                    .andExpect(jsonPath("$.title[0].type.id", Matchers.is(PRIMARY_TITLE_TYPE)))
                    .andExpect(jsonPath("$.title[0].type.schemaUri", Matchers.is(TITLE_TYPE_SCHEMA_URI)))
                    .andExpect(jsonPath("$.title[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.title[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.date.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.date.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.description[0].text", Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. ")))
                    .andExpect(jsonPath("$.description[0].type.id", Matchers.is(PRIMARY_DESCRIPTION_TYPE)))
                    .andExpect(jsonPath("$.description[0].type.schemaUri", Matchers.is(DESCRIPTION_TYPE_SCHEMA_URI)))
                    .andExpect(jsonPath("$.access.type.id", Matchers.is(SchemaValues.ACCESS_TYPE_EMBARGOED.getUri())))
                    .andExpect(jsonPath("$.access.type.schemaUri", Matchers.is(ACCESS_SCHEMA_URI)))
                    .andExpect(jsonPath("$.access.statement.text", Matchers.is("This RAiD is closed")))
                    .andExpect(jsonPath("$.contributor[0].id", Matchers.is("https://orcid.org/0000-0002-4368-8058")))
                    .andExpect(jsonPath("$.contributor[0].schemaUri", Matchers.is("https://orcid.org/")))
                    .andExpect(jsonPath("$.contributor[0].position[0].schemaUri", Matchers.is("https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1")))
                    .andExpect(jsonPath("$.contributor[0].position[0].id", Matchers.is("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json")))
                    .andExpect(jsonPath("$.contributor[0].position[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.contributor[0].position[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.contributor[0].role[0].schemaUri", Matchers.is("https://credit.niso.org")))
                    .andExpect(jsonPath("$.contributor[0].role[0].id", Matchers.is("https://credit.niso.org/contributor-roles/formal-analysis/")))
                    .andExpect(jsonPath("$.organisation[0].role[0].id", Matchers.is("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/lead-research-organisation.json")))
                    .andExpect(jsonPath("$.organisation[0].role[0].schemaUri", Matchers.is("https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1")))
                    .andExpect(jsonPath("$.organisation[0].role[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.organisation[0].role[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$.organisation[0].id", Matchers.is("https://ror.org/04qw24q55")))
                    .andExpect(jsonPath("$.organisation[0].schemaUri", Matchers.is("https://ror.org/")));
        }
    }

    @Test
    void updateRaidV1_Returns404IfNotFound() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final var handle = String.join("/", prefix, suffix);
        final var input = createRaidForPut();

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);
            when(raidService.isEditable(any(ApiToken.class), anyLong())).thenReturn(true);

            when(validationService.validateForUpdate(eq(handle), any(RaidUpdateRequest.class))).thenReturn(Collections.emptyList());

            doThrow(new ResourceNotFoundException(handle))
                    .when(raidService).update(input);

            final MvcResult mvcResult = mockMvc.perform(put(String.format("/raid/%s/%s", prefix, suffix))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();

            final FailureResponse failureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FailureResponse.class);

            assertThat(failureResponse.getType(), Matchers.is("https://raid.org.au/errors#ResourceNotFoundException"));
            assertThat(failureResponse.getTitle(), Matchers.is("The resource was not found."));
            assertThat(failureResponse.getStatus(), Matchers.is(404));
            assertThat(failureResponse.getDetail(), Matchers.is("No RAiD was found with handle 10378.1/1696639."));
            assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));
        }
    }

    @Test
    void updateRaidsV1_ReturnsForbiddenWithInvalidServicePoint() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final Long servicePointId = 20000001L;
        final var input = createRaidForGet("", LocalDate.now());

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            authzUtil.when(() -> AuthzUtil.guardOperatorOrAssociated(apiToken, servicePointId))
                    .thenThrow(new CrossAccountAccessException(servicePointId));

            final MvcResult mvcResult = mockMvc.perform(put(String.format("/raid/%s/%s", prefix, suffix))
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(input))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andReturn();

            final FailureResponse failureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FailureResponse.class);

            assertThat(failureResponse.getType(), Matchers.is("https://raid.org.au/errors#CrossAccountAccessException"));
            assertThat(failureResponse.getTitle(), Matchers.is("You do not have permission to access this RAiD."));
            assertThat(failureResponse.getStatus(), Matchers.is(403));
            assertThat(failureResponse.getDetail(), Matchers.is("You don't have permission to access RAiDs with a service point of " + servicePointId));
            assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));

            verifyNoInteractions(raidService);
        }
    }

    @Test
    void readRaidV1_ReturnsOk() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final var startDate = LocalDate.now().minusYears(1);
        final var title = "test-title";
        final var raid = createRaidForGet(title, startDate);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            when(apiToken.getServicePointId()).thenReturn(20000001L);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            when(raidService.findByHandle(String.join("/", prefix, suffix))).thenReturn(Optional.of(raid));

            final MvcResult mvcResult = mockMvc.perform(get(String.format("/raid/%s/%s", prefix, suffix))
                            .characterEncoding("utf-8")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            final RaidDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RaidDto.class);

            assertThat(result, Matchers.is(raid));
        }
    }

    @Test
    @DisplayName("Returns raid at given version")
    void readRaidV1_ReturnsRaidAtVersion() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final var startDate = LocalDate.now().minusYears(1);
        final var title = "test-title";
        final var raid = createRaidForGet(title, startDate);
        final var version = 9;

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            when(apiToken.getServicePointId()).thenReturn(20000001L);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            when(raidService.findByHandle(String.join("/", prefix, suffix))).thenReturn(Optional.of(raid));
            when(raidHistoryService.findByHandleAndVersion(String.join("/", prefix, suffix), version))
                    .thenReturn(Optional.of(raid));

            final MvcResult mvcResult = mockMvc.perform(get(String.format("/raid/%s/%s", prefix, suffix))
                            .queryParam("version", String.valueOf(version))
                            .characterEncoding("utf-8")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andReturn();

            final RaidDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RaidDto.class);

            assertThat(result, Matchers.is(raid));
        }
    }

    @Test
    @DisplayName("Returns 404 when no raid found at given version")
    void readRaidV1_ReturnsNotFoundIfVersionNotFound() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final var startDate = LocalDate.now().minusYears(1);
        final var title = "test-title";
        final var raid = createRaidForGet(title, startDate);
        final var version = 9;

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            when(apiToken.getServicePointId()).thenReturn(20000001L);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            when(raidService.findByHandle(String.join("/", prefix, suffix))).thenReturn(Optional.of(raid));
            when(raidHistoryService.findByHandleAndVersion(String.join("/", prefix, suffix), version))
                    .thenThrow(ResourceNotFoundException.class);

            mockMvc.perform(get(String.format("/raid/%s/%s", prefix, suffix))
                            .queryParam("version", String.valueOf(version))
                            .characterEncoding("utf-8")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();
        }
    }

    @Test
    void readRaidV1_ReturnsNotFound() throws Exception {
        final var prefix = "10.38";
        final var suffix = "99999";
        final var handle = String.join("/", prefix, suffix);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            doThrow(new ResourceNotFoundException(handle)).when(raidService).findByHandle(handle);

            final MvcResult mvcResult = mockMvc.perform(get(String.format("/raid/%s/%s", prefix, suffix))
                            .characterEncoding("utf-8")
                            .accept(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andReturn();

            final FailureResponse failureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FailureResponse.class);

            assertThat(failureResponse.getType(), Matchers.is("https://raid.org.au/errors#ResourceNotFoundException"));
            assertThat(failureResponse.getTitle(), Matchers.is("The resource was not found."));
            assertThat(failureResponse.getStatus(), Matchers.is(404));
            assertThat(failureResponse.getDetail(), Matchers.is("No RAiD was found with handle 10.38/99999."));
            assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));
        }
    }

    @Test
    @DisplayName("Requesting a closed raid with a different service point returns forbidden response")
    void forbiddenWithClosedRaidAndDifferentServicePoint() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final Long servicePointId = 20000001L;
        final var raid = createRaidForGet("", LocalDate.now());

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final var apiToken = ApiToken.ApiTokenBuilder.anApiToken()
                    .withAppUserId(1L)
                    .withServicePointId(999L)
                    .withClientId("_clientId")
                    .withSubject("_subject")
                    .withEmail("_email")
                    .withRole("_role")
                    .build();

            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            when(raidService.findByHandle(String.join("/", prefix, suffix))).thenReturn(Optional.of(raid));

            mockMvc.perform(get(String.format("/raid/%s/%s", prefix, suffix))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.identifier.id", Matchers.is("https://raid.org.au/10378.1/1696639")))
                    .andExpect(jsonPath("$.access.type.id", Matchers.is(SchemaValues.ACCESS_TYPE_EMBARGOED.getUri())))
                    .andExpect(jsonPath("$.access.statement.text", Matchers.is("This RAiD is closed")))
                    .andExpect(jsonPath("$.access.statement.language.id", Matchers.is("eng")))
                    .andExpect(jsonPath("$.access.statement.language.schemaUri", Matchers.is("https://www.iso.org/standard/39534.html")))
                    .andReturn();
        }
    }

    @Test
    @DisplayName("Requesting an embargoed raid with a different service point returns forbidden response")
    void forbiddenWithEmbargoedRaidAndDifferentServicePoint() throws Exception {
        final var prefix = "10378.1";
        final var suffix = "1696639";
        final Long servicePointId = 20000001L;
        final var raid = embargoedRaid();

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final var apiToken = ApiToken.ApiTokenBuilder.anApiToken()
                    .withAppUserId(1L)
                    .withServicePointId(999L)
                    .withClientId("_clientId")
                    .withSubject("_subject")
                    .withEmail("_email")
                    .withRole("_role")
                    .build();

            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            when(raidService.findByHandle(String.join("/", prefix, suffix))).thenReturn(Optional.of(raid));

            mockMvc.perform(get(String.format("/raid/%s/%s", prefix, suffix))
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.identifier.id", Matchers.is("https://raid.org.au/10378.1/1696639")))
                    .andExpect(jsonPath("$.access.type.id", Matchers.is(SchemaValues.ACCESS_TYPE_EMBARGOED.getUri())))
                    .andExpect(jsonPath("$.access.embargoExpiry", Matchers.is("2024-12-31")))
                    .andExpect(jsonPath("$.access.statement.text", Matchers.is("This RAiD is embargoed")))
                    .andExpect(jsonPath("$.access.statement.language.id", Matchers.is("eng")))
                    .andExpect(jsonPath("$.access.statement.language.schemaUri", Matchers.is("https://www.iso.org/standard/39534.html")))
                    .andReturn();
        }
    }

    @Test
    void listRaidsV1_ReturnsOk() throws Exception {
        final Long servicePointId = 20000001L;
        final var title = "C. Japonicum Genome";
        final var startDate = LocalDate.now();
        final var handle = new IdentifierHandle("10378.1", "1696639");
        final var id = new IdentifierUrl("https://raid.org.au", handle);
        final var endDate = startDate.plusMonths(6);

        final var output = createRaidForGet(title, startDate);

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            when(raidIngestService.findAllByServicePointId(servicePointId)).thenReturn(Collections.singletonList(output));

            mockMvc.perform(get("/raid/", handle)
                            .queryParam("servicePointId", servicePointId.toString())
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].identifier.id", Matchers.is(id.formatUrl())))
                    .andExpect(jsonPath("$[0].identifier.schemaUri", Matchers.is("https://raid.org")))
                    .andExpect(jsonPath("$[0].identifier.registrationAgency.id", Matchers.is("https://ror.org/038sjwq14")))
                    .andExpect(jsonPath("$[0].identifier.registrationAgency.schemaUri", Matchers.is("https://ror.org/")))
                    .andExpect(jsonPath("$[0].identifier.owner.id", Matchers.is("https://ror.org/02stey378")))
                    .andExpect(jsonPath("$[0].identifier.owner.schemaUri", Matchers.is("https://ror.org/")))
                    .andExpect(jsonPath("$[0].identifier.owner.servicePoint", Matchers.is(20000001)))
                    .andExpect(jsonPath("$[0].title[0].text", Matchers.is(title)))
                    .andExpect(jsonPath("$[0].title[0].type.id", Matchers.is(PRIMARY_TITLE_TYPE)))
                    .andExpect(jsonPath("$[0].title[0].type.schemaUri", Matchers.is(TITLE_TYPE_SCHEMA_URI)))
                    .andExpect(jsonPath("$[0].title[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].title[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].date.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].date.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].description[0].text", Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. ")))
                    .andExpect(jsonPath("$[0].description[0].type.id", Matchers.is(PRIMARY_DESCRIPTION_TYPE)))
                    .andExpect(jsonPath("$[0].description[0].type.schemaUri", Matchers.is(DESCRIPTION_TYPE_SCHEMA_URI)))
                    .andExpect(jsonPath("$[0].access.type.id", Matchers.is(SchemaValues.ACCESS_TYPE_EMBARGOED.getUri())))
                    .andExpect(jsonPath("$[0].access.type.schemaUri", Matchers.is(ACCESS_SCHEMA_URI)))
                    .andExpect(jsonPath("$[0].access.statement.text", Matchers.is("This RAiD is closed")))
                    .andExpect(jsonPath("$[0].contributor[0].id", Matchers.is("https://orcid.org/0000-0002-4368-8058")))
                    .andExpect(jsonPath("$[0].contributor[0].schemaUri", Matchers.is("https://orcid.org/")))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].schemaUri", Matchers.is("https://github.com/au-research/raid-metadata/tree/main/scheme/contributor/position/v1")))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].id", Matchers.is("https://github.com/au-research/raid-metadata/blob/main/scheme/contributor/position/v1/leader.json")))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].contributor[0].position[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].contributor[0].role[0].schemaUri", Matchers.is("https://credit.niso.org")))
                    .andExpect(jsonPath("$[0].contributor[0].role[0].id", Matchers.is("https://credit.niso.org/contributor-roles/formal-analysis/")))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].id", Matchers.is("https://github.com/au-research/raid-metadata/blob/main/scheme/organisation/role/v1/lead-research-organisation.json")))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].schemaUri", Matchers.is("https://github.com/au-research/raid-metadata/tree/main/scheme/organisation/role/v1")))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].organisation[0].role[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
                    .andExpect(jsonPath("$[0].organisation[0].id", Matchers.is("https://ror.org/04qw24q55")))
                    .andExpect(jsonPath("$[0].organisation[0].schemaUri", Matchers.is("https://ror.org/")));
        }
    }

    @Test
    @DisplayName("Raid history endpoint returns list of changes")
    void raidHistory() throws Exception {
        final var prefix = "_prefix";
        final var suffix = "_suffix";
        final var handle = prefix + "/" + suffix;
        final var version = 1;
        final var diff = "_diff";
        final var timestamp = LocalDateTime.now().atOffset(ZoneOffset.UTC);

        final var raid = createRaidForGet("title", LocalDate.now());
        raid.getAccess().getType().setId(SchemaValues.ACCESS_TYPE_OPEN.getUri());

        final var raidChange = new RaidChange()
                .handle(handle)
                .version(version)
                .diff(diff)
                .timestamp(timestamp);

        when(raidService.findByHandle(handle)).thenReturn(Optional.of(raid));

        when(raidHistoryService.findAllChangesByHandle(handle)).thenReturn(List.of(raidChange));

        mockMvc.perform(get(String.format("/raid/%s/%s/history", prefix, suffix), handle)
                        .characterEncoding("utf-8"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].handle", Matchers.is(handle)))
                .andExpect(jsonPath("$[0].version", Matchers.is(version)))
                .andExpect(jsonPath("$[0].diff", Matchers.is(diff)))
                .andExpect(jsonPath("$[0].timestamp", Matchers.is(timestamp.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME))));
    }

    @Test
    @DisplayName("Raid history returns 403 if raid is embargoed and user is from different service point")
    void raidHistoryEmbargoed() throws Exception {
        final var prefix = "_prefix";
        final var suffix = "_suffix";
        final var handle = prefix + "/" + suffix;
        final var raid = createRaidForGet("title", LocalDate.now());
        final Long servicePointId = 20000001L;

        when(raidService.findByHandle(handle)).thenReturn(Optional.of(raid));

        try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
            final ApiToken apiToken = mock(ApiToken.class);
            authzUtil.when(AuthzUtil::getApiToken).thenReturn(apiToken);

            authzUtil.when(() -> AuthzUtil.guardOperatorOrAssociated(apiToken, servicePointId))
                    .thenThrow(new CrossAccountAccessException(servicePointId));

            mockMvc.perform(get(String.format("/raid/%s/%s/history", prefix, suffix), handle)
                            .characterEncoding("utf-8"))
                    .andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    private RaidDto createRaidForGet(final String title, final LocalDate startDate) throws IOException {
        final String json = FileUtil.resourceContent("/fixtures/raid.json");

        var raid = objectMapper.readValue(json, RaidDto.class);

        final var titleType = new TitleType()
                .id(PRIMARY_TITLE_TYPE)
                .schemaUri(TITLE_TYPE_SCHEMA_URI);

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

    private RaidDto embargoedRaid() throws IOException {
        final String json = resourceContent("/fixtures/embargoed-raid.json");
        return objectMapper.readValue(json, RaidDto.class);
    }

    private RaidUpdateRequest createRaidForPut() throws IOException {
        final String json = resourceContent("/fixtures/raid.json");

        return objectMapper.readValue(json, RaidUpdateRequest.class);
    }

    private RaidCreateRequest createRaidForPost() throws IOException {
        final String json = resourceContent("/fixtures/create-raid.json");
        return objectMapper.readValue(json, RaidCreateRequest.class);
    }
}