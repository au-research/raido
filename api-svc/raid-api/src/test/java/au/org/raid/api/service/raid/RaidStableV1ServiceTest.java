package au.org.raid.api.service.raid;

import au.org.raid.api.exception.ResourceNotFoundException;
import au.org.raid.api.factory.IdFactory;
import au.org.raid.api.factory.RaidDtoFactory;
import au.org.raid.api.factory.RaidRecordFactory;
import au.org.raid.api.repository.RaidRepository;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.apids.ApidsService;
import au.org.raid.api.service.apids.model.ApidsMintResponse;
import au.org.raid.api.service.raid.id.IdentifierHandle;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.spring.config.environment.MetadataProps;
import au.org.raid.api.util.FileUtil;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.*;
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
import org.springframework.transaction.support.TransactionTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static au.org.raid.api.service.raid.MetadataService.RAID_ID_TYPE_URI;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaidStableV1ServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
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
    private MetadataProps metadataProps;
    @Mock
    private TransactionTemplate transactionTemplate;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private IdFactory idFactory;
    @Mock
    private RaidChecksumService checksumService;
    @Mock
    private RaidDtoFactory raidDtoFactory;
    @Mock
    private RaidHistoryService raidHistoryService;
    @Mock
    private RaidIngestService raidIngestService;

    @InjectMocks
    private RaidStableV1Service raidStableV1Service;

    @Test
    @DisplayName("Mint a raid")
    void mintRaidV1() throws IOException {
        final var urlPrefix = "https://raid.org.au/";
        final long servicePointId = 123;
        final var handle = new IdentifierHandle("10378.1", "1696639");
        final var identifierUrl = new IdentifierUrl(urlPrefix, handle);
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

        final var raidDto = new RaidDto();
        final var raidRecord = new RaidRecord();


        final var id = new Id()
                .id(identifierUrl.formatUrl())
                .schemaUri(RAID_ID_TYPE_URI)
                .registrationAgency(new RegistrationAgency()
                        .id(registrationAgency)
                        .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri()))
                .owner(new Owner()
                        .id(identifierOwner)
                        .schemaUri(SchemaValues.ROR_SCHEMA_URI.getUri())
                        .servicePoint(servicePointId)
                );

        when(metadataProps.getHandleUrlPrefix()).thenReturn(urlPrefix);
        when(servicePointRepository.findById(servicePointId)).thenReturn(Optional.of(servicePointRecord));
        when(apidsService.mintApidsHandleContentPrefix(any(Function.class))).thenReturn(apidsResponse);
        when(idParser.parseHandle(handle.format())).thenReturn(handle);
        when(idFactory.create(identifierUrl, servicePointRecord)).thenReturn(id);
        when(metadataService.getMetaProps()).thenReturn(metadataProps);
        when(raidHistoryService.save(createRaidRequest)).thenReturn(raidDto);

        raidStableV1Service.mintRaidSchemaV1(createRaidRequest, servicePointId);
        verify(raidIngestService).create(raidDto);
    }

    @Test
    @DisplayName("Read a raid")
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

        when(raidDtoFactory.create(raidRecord)).thenReturn(expected);

        RaidDto result = raidStableV1Service.read(handle);
        assertThat(result, Matchers.is(expected));
    }

    @Test
    @DisplayName("List raids")
    void listRaidsV1() throws JsonProcessingException {
        final var raidJson = raidJson();
        final Long servicePointId = 999L;

        final var raidRecord = new RaidRecord().setMetadata(JSONB.valueOf(raidJson));

        when(raidRepository.findAllByServicePointId(servicePointId)).thenReturn(List.of(raidRecord));

        final var expected = objectMapper.readValue(raidJson, RaidDto.class);

        when(raidDtoFactory.create(raidRecord)).thenReturn(expected);

        List<RaidDto> results = raidStableV1Service.list(servicePointId);
        assertThat(results.get(0), Matchers.is(expected));
    }

    @Test
    @DisplayName("ResourceNotFoundException is thrown when raid not found on read")
    void readRaidV1_throwsResourceNoFoundException() {
        final String handle = "test-handle";
        final Long servicePointId = 999L;
        final ServicePointRecord servicePointRecord = new ServicePointRecord();
        servicePointRecord.setId(servicePointId);

        when(raidRepository.findByHandle(handle)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> raidStableV1Service.read(handle));
    }

    @Test
    @DisplayName("Updating a raid saves changes and returns updated raid")
    void update() throws JsonProcessingException, ValidationFailureException {
        final var servicePointId = 999L;
        final var raidJson = raidJson();

        final var updateRequest = objectMapper.readValue(raidJson, RaidUpdateRequest.class);
        final var expected = objectMapper.readValue(raidJson, RaidDto.class);

        final var existingRaid = new RaidRecord();
        final var updatedRaid = new RaidRecord()
                .setMetadata(JSONB.valueOf(raidJson));

        final var id = new IdentifierParser().parseUrlWithException(updateRequest.getIdentifier().getId());
        final var handle = id.handle().format();

        final var servicePointRecord = new ServicePointRecord();
        servicePointRecord.setId(servicePointId);

        when(checksumService.create(existingRaid)).thenReturn("1");
        when(checksumService.create(updateRequest)).thenReturn("2");

        when(raidRepository.findByHandleAndVersion(handle, 1)).thenReturn(Optional.of(existingRaid));

        when(raidHistoryService.save(updateRequest)).thenReturn(expected);
        when(raidRecordFactory.create(expected)).thenReturn(updatedRaid);
        when(idParser.parseUrlWithException(id.formatUrl())).thenReturn(id);
        when(mapper.readValue(raidJson, RaidDto.class)).thenReturn(expected);
//        when(transactionTemplate.execute(any(TransactionCallback.class))).thenReturn(1);
        when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(updatedRaid));

        final var result = raidStableV1Service.update(updateRequest);

        verify(raidRepository).findByHandleAndVersion(handle, 1);
//        verify(transactionTemplate).execute(any(TransactionCallback.class));

        assertThat(result, Matchers.is(expected));
    }

    @Test
    @DisplayName("No update is performed if no diff is detected")
    void noUpdateWhenNoDiff() throws JsonProcessingException, ValidationFailureException {
        final var servicePointId = 999L;
        final var raidJson = raidJson();

        final var updateRequest = objectMapper.readValue(raidJson, RaidUpdateRequest.class);
        final var expected = objectMapper.readValue(raidJson, RaidDto.class);

        final var existingRaid = new RaidRecord().setMetadata(JSONB.valueOf(raidJson));
        final var updatedRaid = new RaidRecord()
                .setMetadata(JSONB.valueOf(raidJson));

        final var id = new IdentifierParser().parseUrlWithException(updateRequest.getIdentifier().getId());
        final var handle = id.handle().format();

        final var servicePointRecord = new ServicePointRecord();
        servicePointRecord.setId(servicePointId);

        when(checksumService.create(existingRaid)).thenReturn("1");
        when(checksumService.create(updateRequest)).thenReturn("1");

        when(raidRepository.findByHandleAndVersion(handle, 1)).thenReturn(Optional.of(existingRaid));
        when(idParser.parseUrlWithException(id.formatUrl())).thenReturn(id);
        when(mapper.readValue(raidJson, RaidDto.class)).thenReturn(expected);

        final var result = raidStableV1Service.update(updateRequest);

        verifyNoInteractions(raidRecordFactory);
        verify(raidRepository).findByHandleAndVersion(handle, 1);
        verifyNoInteractions(transactionTemplate);

        assertThat(result, Matchers.is(expected));
    }

    @Test
    @DisplayName("ResourceNotFoundException is thrown on update")
    void noResourceOnUpdate() throws JsonProcessingException, ValidationFailureException {
        final var servicePointId = 999L;
        final var raidJson = raidJson();

        final var updateRequest = objectMapper.readValue(raidJson, RaidUpdateRequest.class);

        final var id = new IdentifierParser().parseUrlWithException(updateRequest.getIdentifier().getId());
        final var handle = id.handle().format();

        final var servicePointRecord = new ServicePointRecord();
        servicePointRecord.setId(servicePointId);

        when(raidRepository.findByHandleAndVersion(handle, 1)).thenReturn(Optional.empty());
        when(idParser.parseUrlWithException(id.formatUrl())).thenReturn(id);

        assertThrows(ResourceNotFoundException.class, () -> raidStableV1Service.update(updateRequest));

        verify(raidRepository).findByHandleAndVersion(handle, 1);
        verifyNoInteractions(transactionTemplate);
    }

    private String raidJson() {
        return FileUtil.resourceContent("/fixtures/raid.json");
    }

    private RaidCreateRequest createRaidRequest() throws IOException {
        final String json = FileUtil.resourceContent("/fixtures/create-raid.json");
        return objectMapper.readValue(json, RaidCreateRequest.class);
    }
}