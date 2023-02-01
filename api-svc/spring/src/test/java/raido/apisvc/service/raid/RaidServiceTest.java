package raido.apisvc.service.raid;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.jooq.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.repository.dto.Raid;
import raido.apisvc.service.apids.ApidsService;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.db.jooq.api_svc.enums.Metaschema;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.db.jooq.api_svc.tables.records.ServicePointRecord;
import raido.idl.raidv2.model.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Function;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static raido.apisvc.service.raid.MetadataService.RAID_ID_TYPE_URI;
import static raido.db.jooq.api_svc.tables.Raid.RAID;
import static raido.db.jooq.api_svc.tables.ServicePoint.SERVICE_POINT;

@ExtendWith(MockitoExtension.class)
class RaidServiceTest {

  @Mock
  private DSLContext db;

  @Mock
  private ApidsService apidsService;

  @Mock
  private MetadataService metadataService;

  @Mock
  private RaidRepository raidRepository;

  @InjectMocks
  private RaidService raidService;

  private final ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
    .setSerializationInclusion(JsonInclude.Include.NON_NULL);

  @Test
  void mintRaidV1() throws ValidationFailureException, JsonProcessingException {
    final long servicePointId = 123;
    final var handle = "10378.1/1696639";
    final var raidUrl = "https://demo.raido-infra.com/handle/" + handle;
    final var urlIndex = 456;
    final var primaryTitle = "C. Japonicum Genome";
    final var metadata = createMetadataJson();
    final var metaschema = Metaschema.raido_metadata_schema_v1;
    final var startDate = LocalDate.of(2020, 11, 1);
    final var confidential = true;

    final var mintRequest = new MintRequestSchemaV1();
    mintRequest.servicePointId(servicePointId);

    final var createRaidSchemaV1 = new CreateRaidV1Request();
    createRaidSchemaV1.setMintRequest(mintRequest);

    createRaidSchemaV1.setMetadata(metadata);
    final var apidsResponse = new ApidsMintResponse();
    final var apidsIdentifier = new ApidsMintResponse.Identifier();
    final var apidsIdentifierProperty = new ApidsMintResponse.Identifier.Property();
    apidsIdentifier.handle = handle;
    apidsIdentifierProperty.index = urlIndex;
    apidsIdentifierProperty.value = raidUrl;

    apidsIdentifier.property = apidsIdentifierProperty;

    apidsResponse.identifier = apidsIdentifier;

    final var idBlock = new IdBlock().
      identifier(handle).
      identifierTypeUri(RAID_ID_TYPE_URI).
      globalUrl(String.format("https://hdl.handle.net/%s", handle)).
      raidAgencyUrl(raidUrl).
      raidAgencyIdentifier("demo.raido-infra.com");

    when(apidsService.mintApidsHandleContentPrefix(any(Function.class))).thenReturn(apidsResponse);
    when(metadataService.createIdBlock(handle, raidUrl)).thenReturn(idBlock);

    raidService.mintRaidSchemaV1(createRaidSchemaV1);

    verify(raidRepository).save(handle, servicePointId, raidUrl, urlIndex, primaryTitle, metadata.id(idBlock), metaschema, startDate, confidential);
  }

  @Test
  void readRaidV1() throws JsonProcessingException {
    final String handle = "test-handle";
    final Long servicePointId = 999L;
    final RaidRecord raidRecord = new RaidRecord();
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    raidRecord.setMetadata(JSONB.valueOf(metadataJson()));

    final Raid data = new Raid(
      raidRecord, new ServicePointRecord()
    );

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(data));

    final var expectedMetadata = objectMapper.readValue(metadataJson(), MetadataSchemaV1.class);

    RaidSchemaV1 result = raidService.readRaidV1(handle);
    assertThat(result.getMintRequest().getServicePointId(), Matchers.is(data.servicePoint().getId()));
    assertThat(result.getMetadata(), Matchers.is(expectedMetadata));
  }

  @Test
  void readRaidV1_throwsResourceNoFoundException() {
    final String handle = "test-handle";
    final Long servicePointId = 999L;
    final RaidRecord raidRecord = new RaidRecord();
    final ServicePointRecord servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    raidRecord.setMetadata(JSONB.valueOf(metadataJson()));

    final Raid data = new Raid(
      raidRecord, new ServicePointRecord()
    );

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> raidService.readRaidV1(handle));
  }

  @Test
  void updateRaidSchemaV1() throws JsonProcessingException {
    final var handle = "10378.1/1696639";
    final var servicePointId = 999L;
    final var confidential = true;
    final var metaschema = Metaschema.raido_metadata_schema_v1;
    final var dateCreated = LocalDateTime.now();
    final var primaryTitle = "C. Japonicum Genome";
    final var startDate = LocalDate.of(2020,11,1);
    final var url = "https://demo.raido-infra.com/handle/10378.1/1696639";
    final var urlIndex = 123;
    final var metadata = objectMapper.readValue(metadataJson(), MetadataSchemaV1.class);
    final var mintRequest = new MintRequestSchemaV1().servicePointId(servicePointId);

    final var raidRecord = new RaidRecord();
    raidRecord.setHandle(handle);
    raidRecord.setServicePointId(servicePointId);
    raidRecord.setConfidential(confidential);
    raidRecord.setMetadataSchema(metaschema);
    raidRecord.setDateCreated(dateCreated);
    raidRecord.setPrimaryTitle(primaryTitle);
    raidRecord.setStartDate(startDate);
    raidRecord.setUrl(url);
    raidRecord.setUrlIndex(urlIndex);

    final var servicePointRecord = new ServicePointRecord();
    servicePointRecord.setId(servicePointId);

    final var existingRaid = new Raid(raidRecord, servicePointRecord);

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(existingRaid));

    final var result = raidService.updateRaidV1(mintRequest, metadata);

    verify(raidRepository).findByHandle(handle);
    verify(raidRepository).update(
      handle,
      servicePointId,
      url,
      urlIndex,
      primaryTitle,
      metadata,
      metaschema,
      startDate,
      confidential
    );

    assertThat(result.getMetadata(), Matchers.is(metadata));
    assertThat(result.getMintRequest(), Matchers.is(mintRequest));
  }

  @Test
  void updateRaidSchemaV1_throwsResourceNotFoundException() throws JsonProcessingException {
    final var handle = "10378.1/1696639";
    final var servicePointId = 999L;
    final var metadata = objectMapper.readValue(metadataJson(), MetadataSchemaV1.class);
    final var mintRequest = new MintRequestSchemaV1().servicePointId(servicePointId);

    when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> raidService.updateRaidV1(mintRequest, metadata));

    verify(raidRepository).findByHandle(handle);
    verifyNoMoreInteractions(raidRepository);
  }
  private String metadataJson() {
    return """
      {
        "metadataSchema": "RaidoMetadataSchemaV1",
        "id": {
          "identifier": "10378.1/1696639",
          "identifierTypeUri": "https://raid.org",
          "globalUrl": "https://hdl.handle.net/10378.1/1696639",
          "raidAgencyUrl": "https://demo.raido-infra.com/handle/10378.1/1696639",
          "raidAgencyIdentifier": "demo.raido-infra.com"
        },
        "titles": [
          {
            "title": "C. Japonicum Genome",
            "type": "Primary Title",
            "startDate": "2020-10-07"
          }
        ],
        "dates": {
          "startDate": "2020-11-01"
        },
        "descriptions": [
          {
            "description": "Genome sequencing and assembly project at WUR of the C. Japonicum. ",
            "type": "Primary Description"
          }
        ],
        "access": {
          "type": "Closed",
          "accessStatement": "This RAiD is closed"
        },
        "contributors": [
          {
            "id": "0000-0002-4368-8058",
            "identifierSchemeUri": "https://orcid.org/",
            "positions": [
              {
                "positionSchemaUri": "https://raid.org/",
                "position": "Leader",
                "startDate": "2020-10-07"
              }
            ],
            "roles": [
              {
                "roleSchemeUri": "https://credit.niso.org/",
                "role": "project-administration"
              }
            ]
          }
        ],
        "organisations": [
          {
            "id": "https://ror.org/04qw24q55",
            "identifierSchemeUri": "https://ror.org/",
            "roles": [
              {
                "roleSchemeUri": "https://raid.org/",
                "role": "Lead Research Organisation",
                "startDate": "2020-10-07"
              }
            ]
          }
        ]
      }
      """;
  }



  private MetadataSchemaV1 createMetadataJson() throws JsonProcessingException {
    final var metadata = objectMapper.readValue(metadataJson(), MetadataSchemaV1.class);
    metadata.id(null);

    return metadata;
  }
}