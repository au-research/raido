package raido.apisvc.service.raid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import raido.apisvc.factory.RaidRecordFactory;
import org.hamcrest.Matchers;
import org.jooq.JSONB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.repository.ServicePointRepository;
import raido.apisvc.repository.dto.Raid;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static raido.apisvc.service.raid.MetadataService.RAID_ID_TYPE_URI;

@ExtendWith(MockitoExtension.class)
class RaidServiceTest {
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

  @InjectMocks
  private RaidService raidService;

  private final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Test
  void mintRaidV1() throws IOException {
    final long servicePointId = 123;
    final var handle = "https://raid.org/10378.1/1696639";
    final var raidUrl = "https://demo.raido-infra.com/handle/" + handle;
    final var urlIndex = 456;
    final var createRaidRequest = createRaidRequest();
    final var registrationAgency = "registration-agency";
    final var identifierOwner = "identifier-owner";

    final var apidsResponse = new ApidsMintResponse();
    final var apidsIdentifier = new ApidsMintResponse.Identifier();
    final var apidsIdentifierProperty = new ApidsMintResponse.Identifier.Property();
    apidsIdentifier.handle = handle;
    apidsIdentifierProperty.index = urlIndex;
    apidsIdentifierProperty.value = raidUrl;
    apidsIdentifier.property = apidsIdentifierProperty;
    apidsResponse.identifier = apidsIdentifier;

    final var servicePointRecord = new ServicePointRecord();
    final var raidRecord = new RaidRecord();

    final var idBlock = new IdBlock()
      .identifier(handle)
      .identifierSchemeURI(RAID_ID_TYPE_URI)
      .identifierRegistrationAgency(registrationAgency)
      .identifierOwner(identifierOwner)
      .identifierServicePoint(servicePointId);

    when(servicePointRepository.findById(servicePointId)).thenReturn(Optional.of(servicePointRecord));
    when(apidsService.mintApidsHandleContentPrefix(any(Function.class))).thenReturn(apidsResponse);
    when(metadataService.createIdBlock(handle, servicePointRecord, raidUrl)).thenReturn(idBlock);
    when(raidRecordFactory.create(createRaidRequest, apidsResponse, servicePointRecord)).thenReturn(raidRecord);

    raidService.mintRaidSchemaV1(createRaidRequest, servicePointId);

    verify(raidRepository).insert(raidRecord);
  }

  @Test
  void readRaidV1() throws IOException {
    final String handle = "test-handle";
    final Long servicePointId = 999L;
    final RaidRecord raidRecord = new RaidRecord();
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    raidRecord.setMetadata(JSONB.valueOf(raidJson()));

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(raidRecord));

    final var expected = objectMapper.readValue(raidJson(), RaidSchemaV1.class);

    RaidSchemaV1 result = raidService.readRaidV1(handle);
    assertThat(result, Matchers.is(expected));
  }

  @Test
  void listRaidsV1() throws JsonProcessingException {
    final Long servicePointId = 999L;
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    List<Raid> data = List.of(
      new Raid(new RaidRecord().setMetadata(JSONB.valueOf(raidJson())), new ServicePointRecord().setId(servicePointId))
    );

    when(raidRepository.findAllByServicePointId(servicePointId)).thenReturn(data);

    List<RaidSchemaV1> results = raidService.listRaidsV1(servicePointId);
    assertThat(results.get(0), Matchers.is(objectMapper.readValue(raidJson(), RaidSchemaV1.class)));
  }

  @Test
  void readRaidV1_throwsResourceNoFoundException() {
    final String handle = "test-handle";
    final Long servicePointId = 999L;
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> raidService.readRaidV1(handle));
  }

  @Test
  void updateRaidSchemaV1() throws JsonProcessingException {
    final var handle = "10378.1/1696639";
    final var servicePointId = 999L;
    final var metadata = objectMapper.readValue(raidJson(), UpdateRaidV1Request.class);
    final var expected = objectMapper.readValue(raidJson(), RaidSchemaV1.class);

    final var existingRaid = new RaidRecord();
    final var updatedRaid = new RaidRecord()
      .setMetadata(JSONB.valueOf(raidJson()));

    final var servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(existingRaid));
    when(raidRecordFactory.merge(metadata, existingRaid)).thenReturn(updatedRaid);

    final var result = raidService.updateRaidV1(metadata);

    verify(raidRepository).findByHandle(handle);
    verify(raidRepository).update(updatedRaid);

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

    try {
      return Files.readString(Path.of(getClass().getResource("/fixtures/raid.json").getPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }



  private CreateRaidV1Request createRaidRequest() throws IOException {
    final String json = Files.readString(Path.of(Objects.requireNonNull(getClass().getResource("/fixtures/create-raid.json")).getFile()));
    return objectMapper.readValue(json, CreateRaidV1Request.class);
  }
}