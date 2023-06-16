package raido.apisvc.service.raid;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jooq.JSONB;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raido.apisvc.exception.InvalidJsonException;
import raido.apisvc.exception.InvalidVersionException;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.factory.RaidRecordFactory;
import raido.apisvc.repository.RaidRepository;
import raido.apisvc.service.raid.id.IdentifierHandle;
import raido.apisvc.service.raid.id.IdentifierParser;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.db.jooq.api_svc.tables.records.RaidRecord;
import raido.idl.raidv2.model.IdBlockV2;
import raido.idl.raidv2.model.RaidSchemaV2;
import raido.idl.raidv2.model.UpdateRaidStableV2Request;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaidoStableV2ServiceTest {
    @Mock
    private IdentifierParser identifierParser;
    @Mock
    private RaidRepository raidRepository;
    @Mock
    private RaidRecordFactory raidRecordFactory;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private RaidoStableV2Service raidService;

    @Test
    void findByHandle() throws JsonProcessingException {
        final var handle = "_handle";
        final var json = "{}";
        final var raidSchemaV2 = new RaidSchemaV2();
        final var raidRecord = new RaidRecord();
        raidRecord.setMetadata(JSONB.valueOf(json));

        when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(raidRecord));
        when(objectMapper.readValue(json, RaidSchemaV2.class)).thenReturn(raidSchemaV2);

        assertThat(raidService.findByHandle(handle), is(Optional.of(raidSchemaV2)));
    }

    @Test
    void findByHandle_throwsInvalidJsonException() throws JsonProcessingException {
        final var handle = "_handle";
        final var json = "{}";
        final var raidRecord = new RaidRecord();
        raidRecord.setMetadata(JSONB.valueOf(json));

        when(raidRepository.findByHandle(handle)).thenReturn(Optional.of(raidRecord));
        when(objectMapper.readValue(json, RaidSchemaV2.class)).thenThrow(JsonProcessingException.class);

        assertThrows(InvalidJsonException.class, () -> raidService.findByHandle(handle));
    }

    @Test
    void updateRaid() throws ValidationFailureException, JsonProcessingException {
        final var identifier = "_identifier";
        final var identifierUrl = mock(IdentifierUrl.class);
        final var raid = new UpdateRaidStableV2Request();
        final var prefix = "_prefix";
        final var suffix = "_suffix";
        final var identifierHandle = new IdentifierHandle(prefix, suffix);
        final var existing = new RaidRecord();
        final var merged = new RaidRecord();
        final var metadata = JSONB.valueOf("{}");
        final var raidSchemaV2 = new RaidSchemaV2();

        merged.setMetadata(metadata);
        raid.id(new IdBlockV2().identifier(identifier));

        when(identifierParser.parseUrlWithException(identifier)).thenReturn(identifierUrl);
        when(identifierUrl.handle()).thenReturn(identifierHandle);

        when(raidRepository.findByHandle(String.format("%s/%s", prefix, suffix))).thenReturn(Optional.of(existing));

        when(raidRecordFactory.merge(raid, existing)).thenReturn(merged);
        when(raidRepository.updateByHandleAndVersion(merged)).thenReturn(1);
        when(objectMapper.readValue("{}", RaidSchemaV2.class)).thenReturn(raidSchemaV2);

        assertThat(raidService.updateRaid(raid), is(raidSchemaV2));
    }

    @Test
    void updateRaid_throwsValidationException() throws ValidationFailureException, JsonProcessingException {
        final var identifier = "_identifier";
        final var raid = new UpdateRaidStableV2Request();
        final var merged = new RaidRecord();
        final var metadata = JSONB.valueOf("{}");

        merged.setMetadata(metadata);
        raid.id(new IdBlockV2().identifier(identifier));

        var validationFailure = new ValidationFailure();

        var validationFailureException = new ValidationFailureException(validationFailure);

        doThrow(validationFailureException).when(identifierParser).parseUrlWithException(identifier);

        var exception = assertThrows(ValidationException.class, () -> raidService.updateRaid(raid));

        assertThat(exception.getFailures().size(), is(1));
        assertThat(exception.getFailures(), equalTo(List.of(validationFailure)));
    }

    @Test
    void updateRaid_throwsResourceNotFoundException() throws ValidationFailureException, JsonProcessingException {
        final var identifier = "_identifier";
        final var identifierUrl = mock(IdentifierUrl.class);
        final var raid = new UpdateRaidStableV2Request();
        final var prefix = "_prefix";
        final var suffix = "_suffix";
        final var identifierHandle = new IdentifierHandle(prefix, suffix);
        final var merged = new RaidRecord();
        final var metadata = JSONB.valueOf("{}");

        merged.setMetadata(metadata);
        raid.id(new IdBlockV2().identifier(identifier));

        when(identifierParser.parseUrlWithException(identifier)).thenReturn(identifierUrl);
        when(identifierUrl.handle()).thenReturn(identifierHandle);

        when(raidRepository.findByHandle(String.format("%s/%s", prefix, suffix))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> raidService.updateRaid(raid));
    }

    @Test
    void updateRaid_throwsInvalidVersionException() throws ValidationFailureException, JsonProcessingException {
        final var identifier = "_identifier";
        final var identifierUrl = mock(IdentifierUrl.class);
        final var raid = new UpdateRaidStableV2Request();
        final var prefix = "_prefix";
        final var suffix = "_suffix";
        final var identifierHandle = new IdentifierHandle(prefix, suffix);
        final var existing = new RaidRecord();
        final var merged = new RaidRecord();
        final var metadata = JSONB.valueOf("{}");

        merged.setMetadata(metadata);
        raid.id(new IdBlockV2().identifier(identifier));

        when(identifierParser.parseUrlWithException(identifier)).thenReturn(identifierUrl);
        when(identifierUrl.handle()).thenReturn(identifierHandle);

        when(raidRepository.findByHandle(String.format("%s/%s", prefix, suffix))).thenReturn(Optional.of(existing));

        when(raidRecordFactory.merge(raid, existing)).thenReturn(merged);
        when(raidRepository.updateByHandleAndVersion(merged)).thenReturn(0);

        assertThrows(InvalidVersionException.class, () -> raidService.updateRaid(raid));
    }

    @Test
    void updateRaid_throwsInvalidJsonException() throws ValidationFailureException, JsonProcessingException {
        final var identifier = "_identifier";
        final var identifierUrl = mock(IdentifierUrl.class);
        final var raid = new UpdateRaidStableV2Request();
        final var prefix = "_prefix";
        final var suffix = "_suffix";
        final var identifierHandle = new IdentifierHandle(prefix, suffix);
        final var existing = new RaidRecord();
        final var merged = new RaidRecord();
        final var metadata = JSONB.valueOf("{}");

        merged.setMetadata(metadata);
        raid.id(new IdBlockV2().identifier(identifier));

        when(identifierParser.parseUrlWithException(identifier)).thenReturn(identifierUrl);
        when(identifierUrl.handle()).thenReturn(identifierHandle);

        when(raidRepository.findByHandle(String.format("%s/%s", prefix, suffix))).thenReturn(Optional.of(existing));

        when(raidRecordFactory.merge(raid, existing)).thenReturn(merged);
        when(raidRepository.updateByHandleAndVersion(merged)).thenReturn(1);

        doThrow(JsonProcessingException.class).when(objectMapper).readValue("{}", RaidSchemaV2.class);

        assertThrows(InvalidJsonException.class, () -> raidService.updateRaid(raid));
    }
}