package au.org.raid.api.service;

import au.org.raid.api.factory.ServicePointFactory;
import au.org.raid.api.factory.record.ServicePointRecordFactory;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.idl.raidv2.model.ServicePoint;
import au.org.raid.idl.raidv2.model.ServicePointCreateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ServicePointService {
    private final ServicePointRepository servicePointRepository;
    private final ServicePointRecordFactory servicePointRecordFactory;
    private final ServicePointFactory servicePointFactory;

    public ServicePoint create(final ServicePointCreateRequest servicePoint) {
        final var record = servicePointRecordFactory.create(servicePoint);
        return servicePointFactory.create(servicePointRepository.create(record));
    }

    public ServicePoint update(final ServicePoint servicePointDto) {
        final var record = servicePointRecordFactory.create(servicePointDto);
        return servicePointFactory.create(servicePointRepository.update(record));
    }

    public Optional<ServicePoint> findById(final Long id) {
        return servicePointRepository.findById(id)
                .map(servicePointFactory::create)
                .or(Optional::empty);
    }

    public List<ServicePoint> findAll() {
        return servicePointRepository.findAll().stream()
                .map(servicePointFactory::create)
                .toList();
    }
}
