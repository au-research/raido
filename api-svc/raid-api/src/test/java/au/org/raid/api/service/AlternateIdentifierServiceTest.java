package au.org.raid.api.service;

import au.org.raid.api.factory.AlternateIdentifierFactory;
import au.org.raid.api.factory.record.RaidAlternateIdentifierRecordFactory;
import au.org.raid.api.repository.RaidAlternateIdentifierRepository;
import au.org.raid.db.jooq.tables.records.RaidAlternateIdentifierRecord;
import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlternateIdentifierServiceTest {
    @Mock
    private RaidAlternateIdentifierRepository raidAlternateIdentifierRepository;
    @Mock
    private RaidAlternateIdentifierRecordFactory raidAlternateIdentifierRecordFactory;
    @Mock
    private AlternateIdentifierFactory alternateIdentifierFactory;
    @InjectMocks
    private AlternateIdentifierService alternateIdentifierService;

    @Test
    @DisplayName("Create inserts all alternate identifiers")
    void create() {
        final var uuid = UUID.randomUUID().toString();
        final var uuidType = "UUID";
        final var sha256 = "fbc5b7232d727d2b36d6f4fbccf4af75f2bcbaa4a9580c9b10a74267432aac65";
        final var sha256Type = "SHA256";
        final var handle = "_handle";

        final var alternateIdentifier1 = new AlternateIdentifier()
                .id(uuid)
                .type(uuidType);
        final var alternateIdentifier2 = new AlternateIdentifier()
                .id(sha256)
                .type(sha256Type);

        final var alternateIdentifierRecord1 = new RaidAlternateIdentifierRecord()
                .setId(uuid);
        final var alternateIdentifierRecord2 = new RaidAlternateIdentifierRecord()
                .setId(sha256);

        when(raidAlternateIdentifierRecordFactory.create(alternateIdentifier1, handle))
                .thenReturn(alternateIdentifierRecord1);
        when(raidAlternateIdentifierRecordFactory.create(alternateIdentifier2, handle))
                .thenReturn(alternateIdentifierRecord2);

        alternateIdentifierService.create(List.of(alternateIdentifier1, alternateIdentifier2), handle);

        verify(raidAlternateIdentifierRepository).create(alternateIdentifierRecord1);
        verify(raidAlternateIdentifierRepository).create(alternateIdentifierRecord2);
    }

    @Test
    @DisplayName("create() does not save if alternateIdentifiers is null")
    void nullAlternateIdentifiers() {
        final var handle = "_handle";
        alternateIdentifierService.create(null, handle);
        verifyNoInteractions(raidAlternateIdentifierRecordFactory);
        verifyNoInteractions(raidAlternateIdentifierRepository);
    }

    @Test
    @DisplayName("findAllByHandle() returns list of alternate identifiers for given handle")
    void findAllByHandle() {
        final var handle = "_handle";
        final var id = "_id";
        final var type = "_type";
        final var record = new RaidAlternateIdentifierRecord()
                .setId(id)
                .setType(type);
        final var alternateIdentifier = new AlternateIdentifier();

        when(raidAlternateIdentifierRepository.findAllByHandle(handle)).thenReturn(List.of(record));

        when(alternateIdentifierFactory.create(id, type)).thenReturn(alternateIdentifier);

        assertThat(alternateIdentifierService.findAllByHandle(handle), is(List.of(alternateIdentifier)));
    }

    @Test
    @DisplayName("Update deletes all alternate identifiers for handle and recreates")
    void update() {
        final var uuid = UUID.randomUUID().toString();
        final var uuidType = "UUID";
        final var sha256 = "fbc5b7232d727d2b36d6f4fbccf4af75f2bcbaa4a9580c9b10a74267432aac65";
        final var sha256Type = "SHA256";
        final var handle = "_handle";

        final var alternateIdentifier1 = new AlternateIdentifier()
                .id(uuid)
                .type(uuidType);
        final var alternateIdentifier2 = new AlternateIdentifier()
                .id(sha256)
                .type(sha256Type);

        final var alternateIdentifierRecord1 = new RaidAlternateIdentifierRecord()
                .setId(uuid);
        final var alternateIdentifierRecord2 = new RaidAlternateIdentifierRecord()
                .setId(sha256);

        when(raidAlternateIdentifierRecordFactory.create(alternateIdentifier1, handle))
                .thenReturn(alternateIdentifierRecord1);
        when(raidAlternateIdentifierRecordFactory.create(alternateIdentifier2, handle))
                .thenReturn(alternateIdentifierRecord2);

        alternateIdentifierService.update(List.of(alternateIdentifier1, alternateIdentifier2), handle);

        verify(raidAlternateIdentifierRepository).deleteAllByHandle(handle);
        verify(raidAlternateIdentifierRepository).create(alternateIdentifierRecord1);
        verify(raidAlternateIdentifierRepository).create(alternateIdentifierRecord2);
    }
}