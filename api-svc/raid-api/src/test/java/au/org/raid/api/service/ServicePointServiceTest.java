package au.org.raid.api.service;

import au.org.raid.api.dto.ServicePointDto;
import au.org.raid.api.factory.ServicePointFactory;
import au.org.raid.api.factory.record.ServicePointRecordFactory;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.ServicePoint;
import au.org.raid.idl.raidv2.model.ServicePointCreateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServicePointServiceTest {
    @Mock
    private ServicePointRepository servicePointRepository;
    @Mock
    private ServicePointRecordFactory servicePointRecordFactory;
    @Mock
    private ServicePointFactory servicePointFactory;
    @InjectMocks
    private ServicePointService servicePointService;

    @Test
    @DisplayName("create() saves new service point")
    void createSavesNewServicePoint() {

        final var request = new ServicePointCreateRequest();
        final var record = new ServicePointRecord();
        final var saved = new ServicePointRecord()
                .setId(123L);
        final var servicePoint = new ServicePoint();

        when(servicePointRecordFactory.create(request)).thenReturn(record);
        when(servicePointRepository.create(record)).thenReturn(saved);
        when(servicePointFactory.create(saved)).thenReturn(servicePoint);

        assertThat(servicePointService.create(request), is(servicePoint));
    }

    @Test
    @DisplayName("update()")
    void update() {
        final var input = new ServicePoint();
        final var record = new ServicePointRecord();
        final var updated = new ServicePointRecord()
                .setId(123L);
        final var servicePoint = new ServicePoint();

        when(servicePointRecordFactory.create(input)).thenReturn(record);
        when(servicePointRepository.update(record)).thenReturn(updated);
        when(servicePointFactory.create(updated)).thenReturn(servicePoint);

        assertThat(servicePointService.update(input), is(servicePoint));
    }

    @Test
    @DisplayName("findById() returns optional service point")
    void findById() {
        final var id = 123L;

        final var record = new ServicePointRecord();
        final var servicePoint = new ServicePoint();

        when(servicePointRepository.findById(id)).thenReturn(Optional.of(record));
        when(servicePointFactory.create(record)).thenReturn(servicePoint);

        assertThat(servicePointService.findById(id), is(Optional.of(servicePoint)));
    }

    @Test
    @DisplayName("findById() returns empty optional")
    void findByIdReturnsEmptyOptional() {
        final var id = 123L;

        when(servicePointRepository.findById(id)).thenReturn(Optional.empty());

        assertThat(servicePointService.findById(id), is(Optional.empty()));
        verifyNoInteractions(servicePointFactory);
    }

    @Test
    @DisplayName("findAll() returns list of service points")
    void findAll() {
        final var record = new ServicePointRecord();
        final var servicePoint = new ServicePoint();

        when(servicePointRepository.findAll()).thenReturn(List.of(record));
        when(servicePointFactory.create(record)).thenReturn(servicePoint);

        assertThat(servicePointService.findAll(), is(List.of(servicePoint)));
    }

    @Test
    @DisplayName("findAll() returns empty list")
    void findAllReturnsEmptyList() {
        when(servicePointRepository.findAll()).thenReturn(Collections.emptyList());
        assertThat(servicePointService.findAll(), is(Collections.emptyList()));
        verifyNoInteractions(servicePointFactory);
    }
}