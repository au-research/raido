package au.org.raid.api.service;

import au.org.raid.api.factory.RelatedRaidFactory;
import au.org.raid.api.factory.record.RelatedRaidRecordFactory;
import au.org.raid.api.repository.RelatedRaidRepository;
import au.org.raid.db.jooq.tables.records.RelatedRaidRecord;
import au.org.raid.idl.raidv2.model.RelatedRaid;
import au.org.raid.idl.raidv2.model.RelatedRaidType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatedRaidServiceTest {
    @Mock
    private RelatedRaidRepository relatedRaidRepository;
    @Mock
    private RelatedRaidRecordFactory relatedRaidRecordFactory;
    @Mock
    private RelatedRaidTypeService relatedRaidTypeService;
    @Mock
    private RelatedRaidFactory relatedRaidFactory;
    @InjectMocks
    private RelatedRaidService relatedRaidService;

    @Test
    @DisplayName("create() saves related raid")
    void createSavesRelatedRaid() {
        final var handle = "_handle";
        final var typeId = 123;
        final var uri = "_uri";

        final var type = new RelatedRaidType();

        final var relatedRaid = new RelatedRaid()
                .type(type)
                .id(uri);

        final var relatedRaidRecord = new RelatedRaidRecord();

        when(relatedRaidTypeService.findId(type)).thenReturn(typeId);
        when(relatedRaidRecordFactory.create(handle, uri, typeId)).thenReturn(relatedRaidRecord);

        relatedRaidService.create(List.of(relatedRaid), handle);

        verify(relatedRaidRepository).create(relatedRaidRecord);
    }

    @Test
    @DisplayName("create() with null related raids does nothing")
    void createWithNull() {
        final var handle = "_handle";

        relatedRaidService.create(null, handle);

        verifyNoInteractions(relatedRaidTypeService);
        verifyNoInteractions(relatedRaidRecordFactory);
        verifyNoInteractions(relatedRaidRepository);
    }

    @Test
    @DisplayName("findAllByHandle() returns list of related raids")
    void findAllByHandle() {
        final var handle = "_handle";
        final var relatedRaidHandle = "related-raid-handle";
        final var relatedRaidTypeId = 123;

        final var relatedRaidRecord = new RelatedRaidRecord()
                .setRelatedRaidTypeId(relatedRaidTypeId)
                .setRelatedRaidHandle(relatedRaidHandle);

        final var type = new RelatedRaidType();
        final var relatedRaid = new RelatedRaid();

        when(relatedRaidRepository.findAllByHandle(handle)).thenReturn(List.of(relatedRaidRecord));
        when(relatedRaidTypeService.findById(relatedRaidTypeId)).thenReturn(type);
        when(relatedRaidFactory.create(relatedRaidHandle, type)).thenReturn(relatedRaid);

        assertThat(relatedRaidService.findAllByHandle(handle), is(List.of(relatedRaid)));
    }

    @Test
    @DisplayName("update() deletes and re-inserts related raid")
    void update() {
        final var handle = "_handle";
        final var typeId = 123;
        final var uri = "_uri";

        final var type = new RelatedRaidType();

        final var relatedRaid = new RelatedRaid()
                .type(type)
                .id(uri);

        final var relatedRaidRecord = new RelatedRaidRecord();

        when(relatedRaidTypeService.findId(type)).thenReturn(typeId);
        when(relatedRaidRecordFactory.create(handle, uri, typeId)).thenReturn(relatedRaidRecord);

        relatedRaidService.update(List.of(relatedRaid), handle);

        verify(relatedRaidRepository).deleteAllByHandle(handle);
        verify(relatedRaidRepository).create(relatedRaidRecord);
    }
}