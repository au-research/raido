package raido.apisvc.service.raid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.jooq.JSONB;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.factory.IdFactory;
import raido.apisvc.factory.RaidRecordFactory;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.repository.ServicePointRepository;
import raido.apisvc.repository.dto.Raid;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.service.raid.id.IdentifierHandle;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.apisvc.spring.config.environment.MetadataProps;
import raido.apisvc.util.FileUtil;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.Id;
import raido.idl.raidv2.model.RaidDto;
import raido.idl.raidv2.model.UpdateRaidV1Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static raido.apisvc.service.raid.MetadataService.RAID_ID_TYPE_URI;

@ExtendWith(MockitoExtension.class)
class RaidStableV1ServiceTest {
  @Mock
  private ApidsService apidsService;

  @Mock
  private MetadataService metadataService;

  @Mock
  private RaidRepository raidRepository;

  @Mock
  private ServicePointRepository servicePointRepository;

  @Mock
  private RaidRecordFactory raidRecordFactory;

  @Mock
  private IdentifierParser idParser;

  @Mock
  private MetadataProps metaProps;

  @Mock
  private TransactionTemplate transactionTemplate;

  @Mock
  private ObjectMapper mapper;

  @Mock
  private IdFactory idFactory;

  @InjectMocks
  private RaidStableV1Service raidService;

  private final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Test 
  void mintRaidV1() throws IOException {
    final long servicePointId = 123;
    final var handle = new IdentifierHandle("10378.1", "1696639");
    final var identifierUrl = new IdentifierUrl("https://raid.org.au", handle);
    final var urlIndex = 456;
    final var createRaidRequest = createRaidRequest();
    final var registrationAgency = "registration-agency";
    final var identifierOwner = "identifier-owner";

    final var apidsResponse = new ApidsMintResponse();
    final var apidsIdentifier = new ApidsMintResponse.Identifier();
    final var apidsIdentifierProperty = new ApidsMintResponse.Identifier.Property();
    apidsIdentifier.handle = handle.format();
    apidsIdentifierProperty.index = urlIndex;
    apidsIdentifierProperty.value = identifierUrl.formatUrl();
    apidsIdentifier.property = apidsIdentifierProperty;
    apidsResponse.identifier = apidsIdentifier;

    final var servicePointRecord = new ServicePointRecord();
    final var raidRecord = new RaidRecord();

    
    final var id = new Id()
      .identifier(identifierUrl.formatUrl())
      .identifierSchemeURI(RAID_ID_TYPE_URI)
      .identifierRegistrationAgency(registrationAgency)
      .identifierOwner(identifierOwner)
      .identifierServicePoint(servicePointId);

    ReflectionTestUtils.setField(metaProps, "handleUrlPrefix", identifierUrl.urlPrefix());
    when(servicePointRepository.findById(servicePointId)).thenReturn(Optional.of(servicePointRecord));
    when(apidsService.mintApidsHandleContentPrefix(any(Function.class))).thenReturn(apidsResponse);
    when(idParser.parseHandle(handle.format())).thenReturn(handle);
    when(idFactory.create(identifierUrl, servicePointRecord)).thenReturn(id);
    when(metadataService.getMetaProps()).thenReturn(metaProps);
    when(raidRecordFactory.create(createRaidRequest, apidsResponse, servicePointRecord)).thenReturn(raidRecord);

    raidService.mintRaidSchemaV1(createRaidRequest, servicePointId);

    verify(transactionTemplate).executeWithoutResult(any(Consumer.class));
  }

  @Test
  void readRaidV1() throws IOException {
    final var raidJson = raidJson();
    final String handle = "test-handle";
    final Long servicePointId = 999L;
    final RaidRecord raidRecord = new RaidRecord();
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    raidRecord.setMetadata(JSONB.valueOf(raidJson));

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(raidRecord));

    final var expected = objectMapper.readValue(raidJson(), RaidDto.class);

    when(mapper.readValue(raidJson, RaidDto.class)).thenReturn(expected);

    RaidDto result = raidService.read(handle);
    assertThat(result, Matchers.is(expected));
  }

  @Test
  void listRaidsV1() throws JsonProcessingException {
    final var raidJson = raidJson();
    final Long servicePointId = 999L;
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    List<Raid> data = List.of(
      new Raid(new RaidRecord().setMetadata(JSONB.valueOf(raidJson)), new ServicePointRecord().setId(servicePointId))
    );

    when(raidRepository.findAllByServicePointId(servicePointId)).thenReturn(data);

    final var expected = objectMapper.readValue(raidJson, RaidDto.class);

    when(mapper.readValue(raidJson, RaidDto.class)).thenReturn(expected);

    List<RaidDto> results = raidService.list(servicePointId);
    assertThat(results.get(0), Matchers.is(expected));
  }

  @Test
  @DisplayName("ResourceNotFoundException is thrown when RAiD not found")
  void readRaidV1_throwsResourceNoFoundException() {
    final String handle = "test-handle";
    final Long servicePointId = 999L;
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> raidService.read(handle));
  }

  @Test
  @DisplayName("Updating a RAiD saves changes and returns updated RAiD")
  void update() throws JsonProcessingException, ValidationFailureException {
    final var servicePointId = 999L;
    final var raidJson = raidJson();

    final var metadata = objectMapper.readValue(raidJson, UpdateRaidV1Request.class);
    final var expected = objectMapper.readValue(raidJson, RaidDto.class);

    final var existingRaid = new RaidRecord();
    final var updatedRaid = new RaidRecord()
      .setMetadata(JSONB.valueOf(raidJson));

    final var id = new IdentifierParser().parseUrlWithException(metadata.getId().getIdentifier());
    final var handle = id.handle().format();
    
    final var servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(existingRaid));
    when(raidRecordFactory.merge(metadata, existingRaid)).thenReturn(updatedRaid);
    when(idParser.parseUrlWithException(id.formatUrl())).thenReturn(id);
    when(mapper.readValue(raidJson, RaidDto.class)).thenReturn(expected);

    final var result = raidService.update(metadata);

    verify(raidRepository).findByHandle(handle);
    verify(transactionTemplate).execute(any(TransactionCallback.class));

    assertThat(result, Matchers.is(expected));
  }

//  @Test
//  void updateRaidSchemaV1_throwsResourceNotFoundException() throws JsonProcessingException {
//    final var handle = "10378.1/1696639";
//    final var servicePointId = 999L;
//    final var metadata = objectMapper.readValue(metadataJson(), MetadataSchemaV1.class);
//    final var mintRequest = new MintRequestSchemaV1().servicePointId(servicePointId);
//
//    when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());
//
//    assertThrows(ResourceNotFoundException.class, () -> raidService.updateRaidV1(mintRequest, metadata));
//
//    verify(raidRepository).findByHandle(handle);
//    verifyNoMoreInteractions(raidRepository);
//  }
  private String raidJson() {
      return FileUtil.resourceContent("/fixtures/raid.json");
  }



  private CreateRaidV1Request createRaidRequest() throws IOException {
    final String json = FileUtil.resourceContent("/fixtures/create-raid.json");
    return objectMapper.readValue(json, CreateRaidV1Request.class);
  }
}