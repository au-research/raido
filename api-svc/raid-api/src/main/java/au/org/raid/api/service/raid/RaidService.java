package au.org.raid.api.service.raid;

import au.org.raid.api.exception.InvalidVersionException;
import au.org.raid.api.exception.ResourceNotFoundException;
import au.org.raid.api.exception.UnknownServicePointException;
import au.org.raid.api.factory.HandleFactory;
import au.org.raid.api.factory.IdFactory;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.api.service.Handle;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.datacite.DataciteService;
import au.org.raid.api.spring.security.raidv2.ApiToken;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RaidService {
    private final DataciteService dataciteSvc;
    private final ServicePointRepository servicePointRepository;
    private final IdFactory idFactory;
    private final RaidChecksumService checksumService;
    private final RaidHistoryService raidHistoryService;
    private final RaidIngestService raidIngestService;
    private final HandleFactory handleFactory;

    public RaidDto mint(
            final RaidCreateRequest request,
            final long servicePoint
    ) {
        final var servicePointRecord =
                servicePointRepository.findById(servicePoint).orElseThrow(() ->
                        new UnknownServicePointException(servicePoint));

        final var handle = handleFactory.createWithPrefix(dataciteSvc.getDatacitePrefix());

        request.setIdentifier(idFactory.create(handle.toString(), servicePointRecord));

        final var raidDto = raidHistoryService.save(request);
        raidIngestService.create(raidDto);

        dataciteSvc.createDataciteRaid(request, handle.toString());

        return raidDto;
    }

    @SneakyThrows
    public RaidDto update(final RaidUpdateRequest raid) {
        final Integer version = raid.getIdentifier().getVersion();

        if (version == null) {
            throw new InvalidVersionException(version);
        }

        final var handle = new Handle(raid.getIdentifier().getId()).toString();

        final var existing = raidHistoryService.findByHandleAndVersion(handle, version)
                .orElseThrow(() -> new ResourceNotFoundException(handle));

        final var existingChecksum = checksumService.create(existing);
        final var updateChecksum = checksumService.create(raid);

        if (updateChecksum.equals(existingChecksum)) {
            return existing;
        }

        final var raidDto = raidHistoryService.save(raid);

        dataciteSvc.updateDataciteRaid(raid, handle);

        return raidIngestService.update(raidDto);
    }

    public Optional<RaidDto> findByHandle(String handle) {
        return raidIngestService.findByHandle(handle);
    }

    public boolean isEditable(final ApiToken user, final long servicePointId) {
        final var servicePoint = servicePointRepository.findById(servicePointId)
                .orElseThrow(() -> new UnknownServicePointException(servicePointId));

        return user.getClientId().equals("RAIDO_API") || servicePoint.getAppWritesEnabled();
    }
}