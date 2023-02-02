package raido.apisvc.endpoint.raidv2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import raido.apisvc.exception.CrossAccountAccessException;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.validation.RaidSchemaV1ValidationService;
import raido.apisvc.spring.RedactingExceptionResolver;
import raido.apisvc.spring.security.raidv2.AuthzTokenPayload;
import raido.idl.raidv2.model.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
      .setHandlerExceptionResolvers(new RedactingExceptionResolver(false))
      .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
      .build();
  }

  @Test
  void mintRaidV1_ReturnsBadRequest() throws Exception {
    final var servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var validationFailureMessage = "validation failure message";
    final var validationFailureType = "validation failure type";
    final var validationFailureFieldId = "validation failure id";

    final var raid = createRaidForPost(servicePointId, title, startDate);

    final var validationFailure = new ValidationFailure();
    validationFailure.setFieldId(validationFailureFieldId);
    validationFailure.setMessage(validationFailureMessage);
    validationFailure.setErrorType(validationFailureType);

    final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(validationService.validateForCreate(any(MetadataSchemaV1.class)))
        .thenReturn(List.of(validationFailure));

      mockMvc.perform(post("/raid/v1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(raid))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.detail[0].fieldId", Matchers.is(validationFailureFieldId)))
        .andExpect(jsonPath("$.error.detail[0].errorType", Matchers.is(validationFailureType)))
        .andExpect(jsonPath("$.error.detail[0].message", Matchers.is(validationFailureMessage)));

      verify(raidService, never()).mintRaidSchemaV1(any(CreateRaidV1Request.class));
      verify(raidService, never()).readRaidV1(anyString());
    }
  }

  @Test
  void mintRaidV1_ReturnsOk() throws Exception {
    final Long servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var handle = "test-handle";
    final var endDate = startDate.plusMonths(6);

    final var raidForPost = createRaidForPost(servicePointId, title, startDate);
    final var raidForGet = createRaidForGet(handle, servicePointId, title, startDate);


    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(raidService.mintRaidSchemaV1(any(CreateRaidV1Request.class))).thenReturn(handle);
      when(raidService.readRaidV1(handle)).thenReturn(raidForGet);

      mockMvc.perform(post("/raid/v1")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(raidForPost))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mintRequest.servicePointId", Matchers.is(servicePointId.intValue())))
        .andExpect(jsonPath("$.metadata.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$.metadata.id.identifierTypeUri", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$.metadata.id.globalUrl", Matchers.is("https://hdl.handle.net/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyUrl", Matchers.is("https://raid.org.au/handle/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyIdentifier", Matchers.is("raid.org.au")))
        .andExpect(jsonPath("$.metadata.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$.metadata.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$.metadata.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.descriptions[0].description", Matchers.is("Test description...")))
        .andExpect(jsonPath("$.metadata.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$.metadata.access.type", Matchers.is("Open")))
        .andExpect(jsonPath("$.metadata.access.accessStatement", Matchers.is("Test access statement...")))
        .andExpect(jsonPath("$.metadata.contributors[0].id", Matchers.is("0000-0000-0000-0001")))
        .andExpect(jsonPath("$.metadata.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].id", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$.metadata.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
    }
  }

  @Test
  void updateRaidV1_ReturnsBadRequest() throws Exception {
    final Long servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var handle = "test-handle";
    final var validationFailureMessage = "validation failure message";
    final var validationFailureType = "validation failure type";
    final var validationFailureFieldId = "validation failure id";

    final var input = createRaidForGet(handle, servicePointId, title, startDate);

    final var validationFailure = new ValidationFailure();
    validationFailure.setFieldId(validationFailureFieldId);
    validationFailure.setMessage(validationFailureMessage);
    validationFailure.setErrorType(validationFailureType);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(validationService.validateForUpdate(eq(handle), any(MetadataSchemaV1.class))).thenReturn(List.of(validationFailure));

      mockMvc.perform(put(String.format("/raid/v1/%s", handle))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(input))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error.status", Matchers.is(400)))
        .andExpect(jsonPath("$.error.message", Matchers.is("There were validation failures")))
        .andExpect(jsonPath("$.error.detail[0].fieldId", Matchers.is(validationFailureFieldId)))
        .andExpect(jsonPath("$.error.detail[0].errorType", Matchers.is(validationFailureType)))
        .andExpect(jsonPath("$.error.detail[0].message", Matchers.is(validationFailureMessage)));

      verify(raidService, never()).updateRaidV1(input.getMintRequest(), input.getMetadata());

    }
  }

  @Test
  void updateRaidV1_ReturnsOk() throws Exception {
    final Long servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var handle = "test-handle";
    final var endDate = startDate.plusMonths(6);

    final var input = createRaidForGet(handle, servicePointId, title, startDate);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);
      when(validationService.validateForUpdate(eq(handle), any(MetadataSchemaV1.class))).thenReturn(Collections.emptyList());

      when(raidService.updateRaidV1(input.getMintRequest(), input.getMetadata())).thenReturn(input);

      mockMvc.perform(put(String.format("/raid/v1/%s", handle))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(input))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mintRequest.servicePointId", Matchers.is(servicePointId.intValue())))
        .andExpect(jsonPath("$.metadata.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$.metadata.id.identifierTypeUri", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$.metadata.id.globalUrl", Matchers.is("https://hdl.handle.net/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyUrl", Matchers.is("https://raid.org.au/handle/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyIdentifier", Matchers.is("raid.org.au")))
        .andExpect(jsonPath("$.metadata.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$.metadata.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$.metadata.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.descriptions[0].description", Matchers.is("Test description...")))
        .andExpect(jsonPath("$.metadata.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$.metadata.access.type", Matchers.is("Open")))
        .andExpect(jsonPath("$.metadata.access.accessStatement", Matchers.is("Test access statement...")))
        .andExpect(jsonPath("$.metadata.contributors[0].id", Matchers.is("0000-0000-0000-0001")))
        .andExpect(jsonPath("$.metadata.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].id", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$.metadata.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
    }
  }

  @Test
  void updateRaidV1_Returns404IfNotFound() throws Exception {
    final Long servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var handle = "test-handle";

    final var input = createRaidForGet(handle, servicePointId, title, startDate);

    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(validationService.validateForUpdate(eq(handle), any(MetadataSchemaV1.class))).thenReturn(Collections.emptyList());

      doThrow(new ResourceNotFoundException(handle))
        .when(raidService).updateRaidV1(input.getMintRequest(), input.getMetadata());

      mockMvc.perform(put(String.format("/raid/v1/%s", handle))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(input))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error.message", Matchers.is("No RAiD found with handle test-handle")));
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

      mockMvc.perform(put(String.format("/raid/v1/%s", handle))
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(input))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.status", Matchers.is(HttpStatus.FORBIDDEN.value())))
        .andExpect(jsonPath("$.error.message", Matchers.is("disallowed cross-service point call")))
        .andExpect(jsonPath("$.error.detail", Matchers.is("You don't have permission to access RAiDs with a service point of 999")));
    }

    verifyNoInteractions(validationService);
    verifyNoInteractions(raidService);
  }

  @Test
  void readRaidV1_ReturnsOk() throws Exception {
    final var startDate = LocalDate.now().minusYears(1);
    final var endDate = startDate.plusMonths(6);
    final var title = "test-title";
    final var handle = "test-handle";
    final Long servicePointId = 123L;
    final var raid = createRaidForGet(handle, servicePointId, title, startDate);


    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(raidService.readRaidV1(handle)).thenReturn(raid);

      mockMvc.perform(get(String.format("/raid/v1/%s", handle))
          .characterEncoding("utf-8")
          .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mintRequest.servicePointId", Matchers.is(servicePointId.intValue())))
        .andExpect(jsonPath("$.metadata.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$.metadata.id.identifierTypeUri", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$.metadata.id.globalUrl", Matchers.is("https://hdl.handle.net/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyUrl", Matchers.is("https://raid.org.au/handle/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyIdentifier", Matchers.is("raid.org.au")))
        .andExpect(jsonPath("$.metadata.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$.metadata.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$.metadata.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.descriptions[0].description", Matchers.is("Test description...")))
        .andExpect(jsonPath("$.metadata.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$.metadata.access.type", Matchers.is("Open")))
        .andExpect(jsonPath("$.metadata.access.accessStatement", Matchers.is("Test access statement...")))
        .andExpect(jsonPath("$.metadata.contributors[0].id", Matchers.is("0000-0000-0000-0001")))
        .andExpect(jsonPath("$.metadata.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].id", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$.metadata.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
    }
  }

  @Test
  void readRaidV1_ReturnsNotFound() throws Exception {
    final var startDate = LocalDate.now().minusYears(1);
    final var endDate = startDate.plusMonths(6);
    final var title = "test-title";
    final var handle = "test-handle";
    final Long servicePointId = 123L;
    final var raid = createRaidForGet(handle, servicePointId, title, startDate);


    try (MockedStatic<AuthzUtil> authzUtil = Mockito.mockStatic(AuthzUtil.class)) {
      final AuthzTokenPayload authzTokenPayload = mock(AuthzTokenPayload.class);
      authzUtil.when(AuthzUtil::getAuthzPayload).thenReturn(authzTokenPayload);

      when(raidService.readRaidV1(handle)).thenReturn(raid);

      mockMvc.perform(get(String.format("/raid/v1/%s", handle))
          .characterEncoding("utf-8")
          .accept(MediaType.APPLICATION_JSON))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.mintRequest.servicePointId", Matchers.is(servicePointId.intValue())))
        .andExpect(jsonPath("$.metadata.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$.metadata.id.identifierTypeUri", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$.metadata.id.globalUrl", Matchers.is("https://hdl.handle.net/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyUrl", Matchers.is("https://raid.org.au/handle/" + handle)))
        .andExpect(jsonPath("$.metadata.id.raidAgencyIdentifier", Matchers.is("raid.org.au")))
        .andExpect(jsonPath("$.metadata.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$.metadata.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$.metadata.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.descriptions[0].description", Matchers.is("Test description...")))
        .andExpect(jsonPath("$.metadata.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$.metadata.access.type", Matchers.is("Open")))
        .andExpect(jsonPath("$.metadata.access.accessStatement", Matchers.is("Test access statement...")))
        .andExpect(jsonPath("$.metadata.contributors[0].id", Matchers.is("0000-0000-0000-0001")))
        .andExpect(jsonPath("$.metadata.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$.metadata.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$.metadata.organisations[0].id", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$.metadata.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
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

      mockMvc.perform(get(String.format("/raid/v1/%s", handle))
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.status", Matchers.is(HttpStatus.FORBIDDEN.value())))
        .andExpect(jsonPath("$.error.message", Matchers.is("disallowed cross-service point call")))
        .andExpect(jsonPath("$.error.detail", Matchers.is("You don't have permission to access RAiDs with a service point of 999")));
    }
  }

  @Test
  void listRaidsV1_ReturnsOk() throws Exception {
    final Long servicePointId = 999L;
    final var title = "test-title";
    final var startDate = LocalDate.now();
    final var handle = "test-handle";
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
        .andExpect(jsonPath("$[0].mintRequest.servicePointId", Matchers.is(servicePointId.intValue())))
        .andExpect(jsonPath("$[0].metadata.id.identifier", Matchers.is(handle)))
        .andExpect(jsonPath("$[0].metadata.id.identifierTypeUri", Matchers.is("https://raid.org")))
        .andExpect(jsonPath("$[0].metadata.id.globalUrl", Matchers.is("https://hdl.handle.net/" + handle)))
        .andExpect(jsonPath("$[0].metadata.id.raidAgencyUrl", Matchers.is("https://raid.org.au/handle/" + handle)))
        .andExpect(jsonPath("$[0].metadata.id.raidAgencyIdentifier", Matchers.is("raid.org.au")))
        .andExpect(jsonPath("$[0].metadata.titles[0].title", Matchers.is(title)))
        .andExpect(jsonPath("$[0].metadata.titles[0].type", Matchers.is(TitleType.PRIMARY_TITLE.getValue())))
        .andExpect(jsonPath("$[0].metadata.titles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.titles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.dates.startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.dates.endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.descriptions[0].description", Matchers.is("Test description...")))
        .andExpect(jsonPath("$[0].metadata.descriptions[0].type", Matchers.is("Primary Description")))
        .andExpect(jsonPath("$[0].metadata.access.type", Matchers.is("Open")))
        .andExpect(jsonPath("$[0].metadata.access.accessStatement", Matchers.is("Test access statement...")))
        .andExpect(jsonPath("$[0].metadata.contributors[0].id", Matchers.is("0000-0000-0000-0001")))
        .andExpect(jsonPath("$[0].metadata.contributors[0].identifierSchemeUri", Matchers.is("https://orcid.org/")))
        .andExpect(jsonPath("$[0].metadata.contributors[0].positions[0].positionSchemaUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$[0].metadata.contributors[0].positions[0].position", Matchers.is("Leader")))
        .andExpect(jsonPath("$[0].metadata.contributors[0].positions[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.contributors[0].positions[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.contributors[0].roles[0].roleSchemeUri", Matchers.is("https://credit.niso.org/")))
        .andExpect(jsonPath("$[0].metadata.contributors[0].roles[0].role", Matchers.is("project-administration")))
        .andExpect(jsonPath("$[0].metadata.organisations[0].roles[0].role", Matchers.is("Lead Research Organisation")))
        .andExpect(jsonPath("$[0].metadata.organisations[0].roles[0].roleSchemeUri", Matchers.is("https://raid.org/")))
        .andExpect(jsonPath("$[0].metadata.organisations[0].roles[0].startDate", Matchers.is(startDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.organisations[0].roles[0].endDate", Matchers.is(endDate.format(DateTimeFormatter.ISO_DATE))))
        .andExpect(jsonPath("$[0].metadata.organisations[0].id", Matchers.is("https://ror.org/038sjwq14")))
        .andExpect(jsonPath("$[0].metadata.organisations[0].identifierSchemeUri", Matchers.is("https://ror.org/")));
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

      mockMvc.perform(get("/raid/v1")
          .queryParam("servicePointId", servicePointId.toString())
          .characterEncoding("utf-8"))
        .andDo(print())
        .andExpect(status().isForbidden())
        .andExpect(jsonPath("$.error.status", Matchers.is(HttpStatus.FORBIDDEN.value())))
        .andExpect(jsonPath("$.error.message", Matchers.is("disallowed cross-service point call")))
        .andExpect(jsonPath("$.error.detail", Matchers.is("You don't have permission to access RAiDs with a service point of 999")));

      verifyNoInteractions(raidService);
    }
  }

  private RaidSchemaV1 createRaidForGet(final String handle, final long servicePointId, final String title, final LocalDate startDate) {
    var raid = createRaidForPost(servicePointId, title, startDate);

    final var idBlock = new IdBlock();
    idBlock.identifier(handle);
    idBlock.identifierTypeUri("https://raid.org");
    idBlock.globalUrl(String.format("https://hdl.handle.net/%s", handle));
    idBlock.raidAgencyUrl(String.format("https://raid.org.au/handle/%s", handle));
    idBlock.raidAgencyIdentifier("raid.org.au");

    raid.getMetadata().setId(idBlock);

    return raid;
  }

  private RaidSchemaV1 createRaidForPost(final long servicePointId, final String title, final LocalDate startDate) {
    final var endDate = startDate.plusMonths(6);

    final var titleBlock = new TitleBlock();
    titleBlock.setTitle(title);
    titleBlock.type(TitleType.PRIMARY_TITLE);
    titleBlock.startDate(startDate);
    titleBlock.endDate(endDate);

    final var datesBlock = new DatesBlock();
    datesBlock.startDate(startDate);
    datesBlock.endDate(endDate);

    final var descriptionBlock = new DescriptionBlock();
    descriptionBlock.description("Test description...");
    descriptionBlock.type(DescriptionType.PRIMARY_DESCRIPTION);

    final var accessBlock = new AccessBlock();
    accessBlock.type(AccessType.OPEN);
    accessBlock.accessStatement("Test access statement...");

    final var contributorPosition = new ContributorPosition();
    contributorPosition.position(ContributorPositionRaidMetadataSchemaType.LEADER);
    contributorPosition.positionSchemaUri(ContributorPositionSchemeType.HTTPS_RAID_ORG_);
    contributorPosition.startDate(startDate);
    contributorPosition.endDate(endDate);

    final var contributorRole = new ContributorRole();
    contributorRole.role(ContributorRoleCreditNisoOrgType.PROJECT_ADMINISTRATION);
    contributorRole.roleSchemeUri(ContributorRoleSchemeType.HTTPS_CREDIT_NISO_ORG_);

    final var contributorBlock = new ContributorBlock();
    contributorBlock.addPositionsItem(contributorPosition);
    contributorBlock.addRolesItem(contributorRole);
    contributorBlock.id("0000-0000-0000-0001");
    contributorBlock.identifierSchemeUri(ContributorIdentifierSchemeType.HTTPS_ORCID_ORG_);

    final var organisationRole = new OrganisationRole();
    organisationRole.startDate(startDate);
    organisationRole.endDate(endDate);
    organisationRole.role(OrganisationRoleType.LEAD_RESEARCH_ORGANISATION);
    organisationRole.roleSchemeUri(OrganisationRoleSchemeType.HTTPS_RAID_ORG_);

    final var organisationBlock = new OrganisationBlock();
    organisationBlock.id("https://ror.org/038sjwq14");
    organisationBlock.identifierSchemeUri(OrganisationIdentifierSchemeType.HTTPS_ROR_ORG_);
    organisationBlock.addRolesItem(organisationRole);

    final var raid = new RaidSchemaV1();

    final var mintRequest = new MintRequestSchemaV1();
    mintRequest.servicePointId(servicePointId);

    final var metadata = new MetadataSchemaV1();
    metadata.addTitlesItem(titleBlock);
    metadata.dates(datesBlock);
    metadata.addDescriptionsItem(descriptionBlock);
    metadata.access(accessBlock);
    metadata.addContributorsItem(contributorBlock);
    metadata.addOrganisationsItem(organisationBlock);

    raid.setMintRequest(mintRequest);
    raid.setMetadata(metadata);

    return raid;
  }
}