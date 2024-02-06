package au.org.raid.api.service.raid;

import au.org.raid.api.exception.ValidationFailureException;
import au.org.raid.api.factory.HandleFactory;
import au.org.raid.api.factory.IdFactory;
import au.org.raid.api.factory.RaidRecordFactory;
import au.org.raid.api.repository.RaidRepository;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.api.service.Handle;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.datacite.DataciteService;
import au.org.raid.api.service.raid.id.IdentifierParser;
import au.org.raid.api.util.FileUtil;
import au.org.raid.db.jooq.tables.records.RaidRecord;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.Id;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaidServiceTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    @Mock
    private RaidRepository raidRepository;
    @Mock
    private ServicePointRepository servicePointRepository;
    @Mock
    private RaidRecordFactory raidRecordFactory;
    @Mock
    private ObjectMapper mapper;
    @Mock
    private IdFactory idFactory;
    @Mock
    private RaidChecksumService checksumService;
    @Mock
    private RaidHistoryService raidHistoryService;
    @Mock
    private RaidIngestService raidIngestService;
    @Mock
    private DataciteService dataciteService;
    @Mock
    private HandleFactory handleFactory;
    @InjectMocks
    private RaidService raidService;

    @Test
    @DisplayName("Mint a raid")
    void mintRaidV1() throws IOException {
        final long servicePointId = 123;
        final var prefix = "_prefix";
        final var suffix = "_suffix";
        final var handle = new Handle(prefix, suffix);
        final var createRaidRequest = createRaidRequest();
        final var repositoryId = "repository-id";
        final var password = "_password";

        final var servicePointRecord = new ServicePointRecord()
                .setPrefix(prefix)
                .setRepositoryId(repositoryId)
                .setPassword(password);

        final var raidDto = new RaidDto();

        final var id = new Id();

        when(servicePointRepository.findById(servicePointId)).thenReturn(Optional.of(servicePointRecord));
        when(handleFactory.createWithPrefix(prefix)).thenReturn(handle);

        when(idFactory.create(handle.toString(), servicePointRecord)).thenReturn(id);
        when(raidHistoryService.save(createRaidRequest)).thenReturn(raidDto);

        raidService.mint(createRaidRequest, servicePointId);
        verify(raidIngestService).create(raidDto);
        verify(dataciteService).mint(createRaidRequest, handle.toString(), repositoryId, password);
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

        final var expected = objectMapper.readValue(raidJson(), RaidDto.class);
        when(raidIngestService.findByHandle(handle)).thenReturn(Optional.of(expected));

        final var result = raidService.findByHandle(handle);
        assertThat(result.get(), Matchers.is(expected));

    }

    @Test
    @DisplayName("Updating a raid saves changes and returns updated raid")
    void update() throws JsonProcessingException {

        final var handle = "10378.1/1696639";
        final var raidJson = raidJson();
        final var servicePointId = 123L;
        final var repositoryId = "repository-id";
        final var password = "_password";

        final var servicePointRecord = new ServicePointRecord()
                .setRepositoryId(repositoryId)
                .setPassword(password);

        final var updateRequest = objectMapper.readValue(raidJson, RaidUpdateRequest.class);
        final var expected = objectMapper.readValue(raidJson, RaidDto.class);

        when(raidHistoryService.findByHandleAndVersion(handle, 1)).thenReturn(Optional.of(expected));

        when(checksumService.create(updateRequest)).thenReturn("a");
        when(checksumService.create(expected)).thenReturn("b");

        when(raidHistoryService.save(updateRequest)).thenReturn(expected);
        when(raidIngestService.update(expected)).thenReturn(expected);

        when(servicePointRepository.findById(servicePointId)).thenReturn(Optional.of(servicePointRecord));

        final var result = raidService.update(updateRequest, servicePointId);
        assertThat(result, Matchers.is(expected));

        verify(dataciteService).update(updateRequest, handle, repositoryId, password);
    }

    @Test
    @DisplayName("No update is performed if no diff is detected")
    void noUpdateWhenNoDiff() throws JsonProcessingException, ValidationFailureException {

        final var servicePointId = 999L;
        final var raidJson = raidJson();
        final var repositoryId = "repository-id";
        final var password = "_password";

        final var servicePointRecord = new ServicePointRecord()
                .setId(servicePointId)
                .setRepositoryId(repositoryId)
                .setPassword(password);

        final var updateRequest = objectMapper.readValue(raidJson, RaidUpdateRequest.class);
        final var expected = objectMapper.readValue(raidJson, RaidDto.class);


        final var id = new IdentifierParser().parseUrlWithException(updateRequest.getIdentifier().getId());
        final var handle = id.handle().format();

        when(servicePointRepository.findById(servicePointId)).thenReturn(Optional.of(servicePointRecord));

        when(checksumService.create(expected)).thenReturn("1");
        when(checksumService.create(updateRequest)).thenReturn("1");

        when(raidHistoryService.findByHandleAndVersion(handle, 1)).thenReturn(Optional.of(expected));

        final var result = raidService.update(updateRequest, servicePointId);

        assertThat(result, Matchers.is(expected));

        verifyNoInteractions(dataciteService);
        verifyNoInteractions(raidIngestService);
    }

    private String raidJson() {
        return FileUtil.resourceContent("/fixtures/raid.json");
    }

    private RaidCreateRequest createRaidRequest() throws IOException {
        final String json = FileUtil.resourceContent("/fixtures/create-raid.json");
        return objectMapper.readValue(json, RaidCreateRequest.class);
    }
}