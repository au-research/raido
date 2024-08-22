package au.org.raid.api.service.raid;

import au.org.raid.api.exception.InvalidVersionException;
import au.org.raid.api.exception.ResourceNotFoundException;
import au.org.raid.api.exception.ServicePointNotFoundException;
import au.org.raid.api.exception.UnknownServicePointException;
import au.org.raid.api.factory.HandleFactory;
import au.org.raid.api.factory.IdFactory;
import au.org.raid.api.repository.ServicePointRepository;
import au.org.raid.api.service.Handle;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.RaidListenerService;
import au.org.raid.api.service.datacite.DataciteService;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.Contributor;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RaidService {
    private static final int MAX_MINT_RETRIES = 2;

    private final DataciteService dataciteSvc;
    private final ServicePointRepository servicePointRepository;
    private final IdFactory idFactory;
    private final RaidChecksumService checksumService;
    private final RaidHistoryService raidHistoryService;
    private final RaidIngestService raidIngestService;
    private final HandleFactory handleFactory;
    private final RaidListenerService raidListenerService;

    @Transactional
    public RaidDto mint(
            final RaidCreateRequest raid,
            final long servicePointId
    ) {
        final var servicePointRecord =
                servicePointRepository.findById(servicePointId).orElseThrow(() ->
                        new UnknownServicePointException(servicePointId));

        mintHandle(raid, servicePointRecord, 0);

        raidListenerService.create(raid.getIdentifier().getId(), raid.getContributor());

        raid.setContributor(Collections.emptyList());

        final var raidDto = raidHistoryService.save(raid);
        raidIngestService.create(raidDto);

        return raidDto;
    }

    private void mintHandle(final RaidCreateRequest request, final ServicePointRecord servicePointRecord, int mintRetries) {
        try {
            final var handle = handleFactory.createWithPrefix(servicePointRecord.getPrefix());
            request.setIdentifier(idFactory.create(handle.toString(), servicePointRecord));
            dataciteSvc.mint(request, handle.toString(), servicePointRecord.getRepositoryId(), servicePointRecord.getPassword());
        } catch (final HttpClientErrorException e) {
            if (mintRetries < MAX_MINT_RETRIES && e.getStatusCode().equals(HttpStatusCode.valueOf(422))) {
                mintRetries++;
                log.info("Re-attempting mint of raid in Datacite. Retry {} of {}", mintRetries, MAX_MINT_RETRIES, e);
                mintHandle(request, servicePointRecord, mintRetries);
            } else {
                throw e;
            }
        }
    }

    @SneakyThrows
    @Transactional
    public RaidDto update(final RaidUpdateRequest raid, final long servicePointId) {
        final var servicePointRecord =
                servicePointRepository.findById(servicePointId).orElseThrow(() ->
                        new UnknownServicePointException(servicePointId));

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

        raidListenerService.update(raid.getIdentifier().getId(), raid.getContributor(), existing.getContributor());
        raid.setContributor(Collections.emptyList());

        final var raidDto = raidHistoryService.save(raid);

        dataciteSvc.update(raid, handle, servicePointRecord.getRepositoryId(), servicePointRecord.getPassword());

        return raidIngestService.update(raidDto);
    }

    @Transactional
    public RaidDto patchContributors(final String prefix, final String suffix, List<Contributor> contributors) {
        final var handle = "%s/%s".formatted(prefix, suffix);
        final var raid = raidHistoryService.findByHandle(handle)
                .orElseThrow(() -> new ResourceNotFoundException(handle));

        final var servicePointId = raid.getIdentifier().getOwner().getServicePoint();

        final var servicePointRecord = servicePointRepository.findById(servicePointId)
                .orElseThrow(() -> new ServicePointNotFoundException(servicePointId));

        //TODO; validate contributors?

        raid.setContributor(contributors);

        raidHistoryService.save(raid);
        dataciteSvc.update(raid, handle, servicePointRecord.getRepositoryId(), servicePointRecord.getPassword());

        return raidIngestService.update(raid);

    }

    @Transactional(readOnly = true)
    public Optional<RaidDto> findByHandle(String handle) {
        return raidHistoryService.findByHandle(handle);
    }
}