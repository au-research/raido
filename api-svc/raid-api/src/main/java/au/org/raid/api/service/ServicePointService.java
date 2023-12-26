package au.org.raid.api.service;

import au.org.raid.api.dto.ServicePointDto;
import au.org.raid.api.factory.ServicePointDtoFactory;
import au.org.raid.api.factory.record.ServicePointRecordFactory;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.idl.raidv2.model.ServicePoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static au.org.raid.api.util.Log.to;
import static au.org.raid.db.jooq.tables.ServicePoint.SERVICE_POINT;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class ServicePointService {
    private final ServicePointRepository servicePointRepository;
    private final ServicePointRecordFactory servicePointRecordFactory;
    private final ServicePointDtoFactory servicePointDtoFactory;
    private final DSLContext db;

    public ServicePointDto create(final ServicePointDto servicePointDto) {
        final var record = servicePointRecordFactory.create(servicePointDto);
        return servicePointDtoFactory.create(servicePointRepository.create(record));
    }

    public ServicePointDto update(final ServicePointDto servicePointDto) {
        final var record = servicePointRecordFactory.create(servicePointDto);
        return servicePointDtoFactory.create(servicePointRepository.update(record));
    }

    public ServicePoint updateServicePoint(
            ServicePoint req
    ) {
        var record = req.getId() == null ?
                db.newRecord(SERVICE_POINT) :
                db.fetchSingle(SERVICE_POINT, SERVICE_POINT.ID.eq(req.getId()));

        record.setName(req.getName());
        record.setIdentifierOwner(req.getIdentifierOwner());
        record.setSearchContent(req.getSearchContent());
        record.setAdminEmail(req.getAdminEmail());
        record.setTechEmail(req.getTechEmail());
        record.setEnabled(req.getEnabled());
        record.setAppWritesEnabled(req.getAppWritesEnabled());

        if (req.getId() == null) {
            record.insert();
        } else {
            record.setId(req.getId());
            record.update();
        }

        return new ServicePoint().
                id(record.getId()).
                name((record.getName())).
                identifierOwner((record.getIdentifierOwner())).
                searchContent(record.getSearchContent()).
                adminEmail(record.getAdminEmail()).
                techEmail(record.getTechEmail()).
                enabled(record.getEnabled());
    }

    public Optional<ServicePointDto> findById(final Long id) {
        return servicePointRepository.findById(id)
                .map(servicePointDtoFactory::create)
                .or(Optional::empty);
    }

    public List<ServicePointDto> findAll() {
        return servicePointRepository.findAll().stream()
                .map(servicePointDtoFactory::create)
                .toList();
    }
}
