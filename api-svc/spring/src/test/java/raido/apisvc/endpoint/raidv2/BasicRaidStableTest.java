package raido.apisvc.endpoint.raidv2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.jooq.exception.DataAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import raido.apisvc.exception.CrossAccountAccessException;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.validation.RaidSchemaV1ValidationService;
import raido.apisvc.spring.security.raidv2.AuthzTokenPayload;
import raido.idl.raidv2.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BasicRaidStableTest {

  private MockMvc mockMvc;

  @Mock
  private RaidService raidService;

  @Mock
  private RaidSchemaV1ValidationService validationService;

  @InjectMocks
  private BasicRaidStable controller;

  final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()).setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setControllerAdvice(new RaidExceptionHandler())
      .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
      .build();
  }

  @Test
  @Disabled
  void mintRaidV1_ReturnsRedactedInternalServerErrorOnDataAccessException() throws Exception {
    final var servicePointId = 999L;
    final var raid = createRaidForPost();

    final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
    when(authzTokenPayload.getServicePointId()).thenReturn(servicePointId);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      doThrow(DataAccessException.class)
        .when(raidService).mintRaidSchemaV1(any(CreateRaidV1Request.class), eq(servicePointId));

      mockMvc.perform(post("/raid/v1")
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

    final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(validationService.validateForCreate(any(CreateRaidV1Request.class)))
        .thenReturn(List.of(validationFailure));

      final MvcResult mvcResult = mockMvc.perform(post("/raid/v1")
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

      verify(raidService, never()).mintRaidSchemaV1(any(CreateRaidV1Request.class), eq(servicePointId));
      verify(raidService, never()).readRaidV1(anyString());
    }
  }

  @Test
  void mintRaidV1_ReturnsOk() throws Exception {
    final Long servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var handle = "10378.1/1696639";
    final var endDate = startDate.plusMonths(6);

    final var raidForPost = createRaidForPost();
    final var raidForGet = createRaidForGet(handle, servicePointId, title, startDate);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final var user = AuthzTokenPayload.AuthzTokenPayloadBuilder
        .anAuthzTokenPayload()
        .withAppUserId(0L)
        .withEmail("user-email")
        .withRole("user-role")
        .withSubject("user-subject")
        .withAppUserId(1L)
        .withServicePointId(servicePointId)
        .withClientId("user-client-id")
        .build();

      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(user);

      when(validationService.validateForCreate(any(CreateRaidV1Request.class))).thenReturn(Collections.emptyList());

      when(raidService.mintRaidSchemaV1(any(CreateRaidV1Request.class), eq(servicePointId))).thenReturn(handle);
      when(raidService.readRaidV1(handle)).thenReturn(raidForGet);

      mockMvc.perform(post("/raid/v1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(raidForPost))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$.id.identifierSchemeURI", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$.id.identifierRegistrationAgency", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$.id.identifierOwner", Matchers.is("https://ror.org/02stey378")))
        .andExpect(jsonPath("$.id.identifierServicePoint", Matchers.is(servicePointId.intValue())))
        .andExpect(jsonPath("$.id.globalUrl", Matchers.is("https://hdl.handle.net/" + handle)))
        .andExpect(jsonPath("$.id.raidAgencyUrl", Matchers.is("https://demo.raido-infra.com/handle/" + handle)))
        .andExpect(jsonPath("$.id.raidAgencyIdentifier", Matchers.is("demo.raido-infra.com")))
        .andExpect(jsonPath("$.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.descriptions[0].description", Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. ")))
        .andExpect(jsonPath("$.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$.access.type", Matchers.is("Closed")))
        .andExpect(jsonPath("$.access.accessStatement", Matchers.is("This RAiD is closed")))
        .andExpect(jsonPath("$.contributors[0].id", Matchers.is("0000-0002-4368-8058")))
        .andExpect(jsonPath("$.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.organisations[0].id", Matchers.is("https://ror.org/04qw24q55")))
        .andExpect(jsonPath("$.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
    }
  }

  @Test
  void updateRaidV1_ReturnsBadRequest() throws Exception {
    final var handle = "test-handle";
    final var validationFailureMessage = "validation failure message";
    final var validationFailureType = "validation failure type";
    final var validationFailureFieldId = "validation failure id";

    final var input = createRaidForPut();

    final var validationFailure = new ValidationFailure();
    validationFailure.setFieldId(validationFailureFieldId);
    validationFailure.setMessage(validationFailureMessage);
    validationFailure.setErrorType(validationFailureType);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(validationService.validateForUpdate(eq(handle), any(UpdateRaidV1Request.class))).thenReturn(List.of(validationFailure));

      final MvcResult mvcResult = mockMvc.perform(put(String.format("/raid/v1/%s", handle))
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

      verify(raidService, never()).updateRaidV1(input);
    }
  }

  @Test
  void updateRaidV1_ReturnsOk() throws Exception {
    final Long servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var handle = "test-handle";
    final var endDate = startDate.plusMonths(6);

    final var input = createRaidForPut();
    final var output = createRaidForGet(handle, servicePointId, title, startDate);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);
      when(validationService.validateForUpdate(handle, input)).thenReturn(Collections.emptyList());

      when(raidService.updateRaidV1(input)).thenReturn(output);

      mockMvc.perform(put(String.format("/raid/v1/%s", handle))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(input))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$.id.identifierSchemeURI", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$.id.identifierRegistrationAgency", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$.id.identifierOwner", Matchers.is("https://ror.org/02stey378")))
        .andExpect(jsonPath("$.id.identifierServicePoint", Matchers.is(servicePointId.intValue())))

        .andExpect(jsonPath("$.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.descriptions[0].description", Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. ")))
        .andExpect(jsonPath("$.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$.access.type", Matchers.is("Closed")))
        .andExpect(jsonPath("$.access.accessStatement", Matchers.is("This RAiD is closed")))
        .andExpect(jsonPath("$.contributors[0].id", Matchers.is("0000-0002-4368-8058")))
        .andExpect(jsonPath("$.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.organisations[0].id", Matchers.is("https://ror.org/04qw24q55")))
        .andExpect(jsonPath("$.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
    }
  }

//  @Test
//  void updateRaidV1_ReturnsBadRequestWhenServicePointIsMissing() throws Exception {
//    final Long servicePointId = 999L;
//    final var title = "test-title";
//    final var startDate = LocalDate.now();
//    final var handle = "test-handle";
//
//    final var errorType = "test type";
//    final var errorMessage = "test message";
//    final var errorField = "test field";
//
//    final var input = createRaidForPut(handle, servicePointId, title, startDate);
//
//    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
//      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
//      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);
//
//      when(validationService.validateMintRequest(input.getMintRequest())).thenReturn(List.of(new ValidationFailure()
//        .errorType(errorType)
//        .message(errorMessage)
//        .fieldId(errorField)
//      ));
//
//      final MvcResult mvcResult = mockMvc.perform(put(String.format("/raid/v1/%s", handle))
//          .contentType(MediaType.APPLICATION_JSON)
//          .content(objectMapper.writeValueAsString(input))
//          .characterEncoding("utf-8"))
//        .andDo(print())
//        .andExpect(status().isBadRequest())
//        .andReturn();
//
//      final ValidationFailureResponse validationFailureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ValidationFailureResponse.class);
//
//      assertThat(validationFailureResponse.getType(), Matchers.is("https://raid.org.au/errors#ValidationException"));
//      assertThat(validationFailureResponse.getTitle(), Matchers.is("There were validation failures."));
//      assertThat(validationFailureResponse.getStatus(), Matchers.is(400));
//      assertThat(validationFailureResponse.getDetail(), Matchers.is("Request had 1 validation failure(s). See failures for more details..."));
//      assertThat(validationFailureResponse.getInstance(), Matchers.is("https://raid.org.au"));
//
//      verifyNoInteractions(raidService);
//      verify(validationService, never()).validateForUpdate(eq(handle), any(MetadataSchemaV1.class));
//    }
//  }

  @Test
  void updateRaidV1_Returns404IfNotFound() throws Exception {
    final var handle = "test-handle";

    final var input = createRaidForPut();

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(validationService.validateForUpdate(eq(handle), any(UpdateRaidV1Request.class))).thenReturn(Collections.emptyList());

      doThrow(new ResourceNotFoundException(handle))
        .when(raidService).updateRaidV1(input);

      final MvcResult mvcResult = mockMvc.perform(put(String.format("/raid/v1/%s", handle))
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
      assertThat(failureResponse.getDetail(), Matchers.is("No RAiD was found with handle test-handle."));
      assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));
    }
  }

  @Test
  void updateRaidsV1_ReturnsForbiddenWithInvalidServicePoint() throws Exception {
    final Long servicePointId = 999L;
    final var handle = "test-handle";
    final var input = createRaidForGet(handle, servicePointId, "", LocalDate.now());

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      authzUtil.when(() -> AuthzUtil.guardOperatorOrAssociated(authzTokenPayload, servicePointId))
        .thenThrow(new CrossAccountAccessException(servicePointId));

      final MvcResult mvcResult = mockMvc.perform(put(String.format("/raid/v1/%s", handle))
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
      assertThat(failureResponse.getDetail(), Matchers.is("You don't have permission to access RAiDs with a service point of 999"));
      assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));

      verifyNoInteractions(raidService);
    }
  }

  @Test
  void readRaidV1_ReturnsOk() throws Exception {
    final var startDate = LocalDate.now().minusYears(1);
    final var title = "test-title";
    final var handle = "test-handle";
    final Long servicePointId = 123L;
    final var raid = createRaidForGet(handle, servicePointId, title, startDate);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(raidService.readRaidV1(handle)).thenReturn(raid);

      final MvcResult mvcResult = mockMvc.perform(get(String.format("/raid/v1/%s", handle))
          .characterEncoding("utf-8")
          .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();

      final RaidSchemaV1 result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), RaidSchemaV1.class);

      assertThat(result, Matchers.is(raid));
    }
  }

  @Test
  void readRaidV1_ReturnsNotFound() throws Exception {
    final var handle = "test-handle";

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      doThrow(new ResourceNotFoundException(handle)).when(raidService).readRaidV1(handle);

      final MvcResult mvcResult = mockMvc.perform(get(String.format("/raid/v1/%s", handle))
          .characterEncoding("utf-8")
          .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andReturn();

      final FailureResponse failureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FailureResponse.class);

      assertThat(failureResponse.getType(), Matchers.is("https://raid.org.au/errors#ResourceNotFoundException"));
      assertThat(failureResponse.getTitle(), Matchers.is("The resource was not found."));
      assertThat(failureResponse.getStatus(), Matchers.is(404));
      assertThat(failureResponse.getDetail(), Matchers.is("No RAiD was found with handle test-handle."));
      assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));
    }
  }

  @Test
  void readRaidsV1_ReturnsForbiddenWithInvalidServicePoint() throws Exception {
    final Long servicePointId = 999L;
    final var handle = "test-handle";
    final var raid = createRaidForGet(handle, servicePointId, "", LocalDate.now());

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(raidService.readRaidV1(handle)).thenReturn(raid);

      authzUtil.when(() -> AuthzUtil.guardOperatorOrAssociated(authzTokenPayload, servicePointId))
        .thenThrow(new CrossAccountAccessException(servicePointId));

      final MvcResult mvcResult = mockMvc.perform(get(String.format("/raid/v1/%s", handle))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andReturn();

      final FailureResponse failureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FailureResponse.class);

      assertThat(failureResponse.getType(), Matchers.is("https://raid.org.au/errors#CrossAccountAccessException"));
      assertThat(failureResponse.getTitle(), Matchers.is("You do not have permission to access this RAiD."));
      assertThat(failureResponse.getStatus(), Matchers.is(403));
      assertThat(failureResponse.getDetail(), Matchers.is("You don't have permission to access RAiDs with a service point of 999"));
      assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));

    }
  }

  @Test
  void listRaidsV1_ReturnsOk() throws Exception {
    final Long servicePointId = 999L;
    final var title = "C. Japonicum Genome";
    final var startDate = LocalDate.now();
    final var handle = "https://raid.org/10378.1/1696639";
    final var endDate = startDate.plusMonths(6);

    final var output = createRaidForGet(handle, servicePointId, title, startDate);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(raidService.listRaidsV1(servicePointId)).thenReturn(Collections.singletonList(output));

      mockMvc.perform(get("/raid/v1", handle)
          .queryParam("servicePointId", servicePointId.toString())
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$[0].id.identifierSchemeURI", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$[0].id.identifierRegistrationAgency", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$[0].id.identifierOwner", Matchers.is("https://ror.org/02stey378")))
        .andExpect(jsonPath("$[0].id.identifierServicePoint", Matchers.is(999)))
        .andExpect(jsonPath("$[0].titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$[0].titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$[0].titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].descriptions[0].description", Matchers.is("Genome sequencing and assembly project at WUR of the C. Japonicum. ")))
        .andExpect(jsonPath("$[0].descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$[0].access.type", Matchers.is("Closed")))
        .andExpect(jsonPath("$[0].access.accessStatement", Matchers.is("This RAiD is closed")))
        .andExpect(jsonPath("$[0].contributors[0].id", Matchers.is("0000-0002-4368-8058")))
        .andExpect(jsonPath("$[0].contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$[0].contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$[0].contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$[0].contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$[0].contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$[0].organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$[0].organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$[0].organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].organisations[0].id", Matchers.is("https://ror.org/04qw24q55")))
        .andExpect(jsonPath("$[0].organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
    }
  }

  @Test
  void listRaidsV1_ReturnsForbiddenWithInvalidServicePoint() throws Exception {
    final Long servicePointId = 999L;

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      authzUtil.when(() -> AuthzUtil.guardOperatorOrAssociated(authzTokenPayload, servicePointId))
        .thenThrow(new CrossAccountAccessException(servicePointId));

      final MvcResult mvcResult = mockMvc.perform(get("/raid/v1")
          .queryParam("servicePointId", servicePointId.toString())
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andReturn();

      final FailureResponse failureResponse = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), FailureResponse.class);

      assertThat(failureResponse.getType(), Matchers.is("https://raid.org.au/errors#CrossAccountAccessException"));
      assertThat(failureResponse.getTitle(), Matchers.is("You do not have permission to access this RAiD."));
      assertThat(failureResponse.getStatus(), Matchers.is(403));
      assertThat(failureResponse.getDetail(), Matchers.is("You don't have permission to access RAiDs with a service point of 999"));
      assertThat(failureResponse.getInstance(), Matchers.is("https://raid.org.au"));

      verifyNoInteractions(raidService);
    }
  }

  private RaidSchemaV1 createRaidForGet(final String handle, final long servicePointId, final String title, final LocalDate startDate) throws IOException {
    final String json = Files.readString(Path.of(Objects.requireNonNull(getClass().getResource("/fixtures/raid.json")).getPath()));

    var raid = objectMapper.readValue(json, RaidSchemaV1.class);

    raid.getTitles().get(0)
      .startDate(startDate)
      .endDate(startDate.plusMonths(6))
      .setTitle(title);

    raid.getId()
      .identifier(handle)
      .identifierServicePoint(servicePointId);

    raid.getDates()
      .startDate(startDate)
      .endDate(startDate.plusMonths(6));

    raid.getContributors().get(0).getPositions().get(0)
      .startDate(startDate)
      .endDate(startDate.plusMonths(6));

    raid.getOrganisations().get(0).getRoles().get(0)
      .startDate(startDate)
      .endDate(startDate.plusMonths(6));

    return raid;
  }

  private UpdateRaidV1Request createRaidForPut() throws IOException {
    final String json = Files.readString(Path.of(Objects.requireNonNull(getClass().getResource("/fixtures/raid.json")).getPath()));

    return objectMapper.readValue(json, UpdateRaidV1Request.class);
  }

  private CreateRaidV1Request createRaidForPost() throws IOException {
    final String json = Files.readString(Path.of(Objects.requireNonNull(getClass().getResource("/fixtures/create-raid.json")).getPath()));
    return objectMapper.readValue(json, CreateRaidV1Request.class);
  }
}