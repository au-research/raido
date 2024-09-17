package au.org.raid.api.service.raid;

import au.org.raid.api.dto.RaidPermissionsDto;
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
import au.org.raid.api.service.datacite.DataciteService;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.db.jooq.tables.records.ServicePointRecord;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RaidService {
    private static final int MAX_MINT_RETRIES = 2;
    private static final String SERVICE_POINT_USER_ROLE = "ROLE_service-point-user";
    private static final String RAID_USER_ROLE = "ROLE_raid-user";
    private static final String RAID_ADMIN_ROLE = "ROLE_raid-admin";
    public static final String SERVICE_POINT_GROUP_ID_CLAIM = "service_point_group_id";

    private final DataciteService dataciteSvc;
    private final ServicePointRepository servicePointRepository;
    private final IdFactory idFactory;
    private final RaidChecksumService checksumService;
    private final RaidHistoryService raidHistoryService;
    private final RaidIngestService raidIngestService;
    private final HandleFactory handleFactory;

    @Transactional
    public RaidDto mint(
            final RaidCreateRequest request,
            final long servicePointId
    ) {
        final var servicePointRecord =
                servicePointRepository.findById(servicePointId).orElseThrow(() ->
                        new UnknownServicePointException(servicePointId));

        mintHandle(request, servicePointRecord, 0);

        final var raidDto = raidHistoryService.save(request);
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

        final var raidDto = raidHistoryService.save(raid);

        dataciteSvc.update(raid, handle, servicePointRecord.getRepositoryId(), servicePointRecord.getPassword());

        return raidIngestService.update(raidDto);
    }

    @Transactional(readOnly = true)
    public Optional<RaidDto> findByHandle(String handle) {
        return raidHistoryService.findByHandle(handle);
    }

    public Optional<RaidPermissionsDto> getPermissions(final String prefix, final String suffix) {
        final var handle = "%s/%s".formatted(prefix, suffix);

        final var raidOptional = raidHistoryService.findByHandle(handle);

        if (raidOptional.isEmpty()) {
            return Optional.empty();
        }

        var servicePointMatch = false;
        var canWrite = false;
        var canRead = raidOptional.get().getAccess().getType().getId().equals(SchemaValues.ACCESS_TYPE_OPEN.getUri());

        final var token = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();

        final var groupId = (String) token.getClaims().get(SERVICE_POINT_GROUP_ID_CLAIM);

        final var servicePoint = servicePointRepository.findByGroupId(groupId)
                .orElseThrow(() -> new ServicePointNotFoundException(groupId));

        final var authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        if (raidOptional.get().getIdentifier().getOwner().getServicePoint().equals(servicePoint.getId())) {
            servicePointMatch = true;
        }

        if (authorities.contains(SERVICE_POINT_USER_ROLE)) {
            if (servicePointMatch) {
                return Optional.of(RaidPermissionsDto.builder()
                        .servicePointMatch(servicePointMatch)
                        .read(true)
                        .write(true)
                        .build());
            }
        }

        if (authorities.contains(RAID_USER_ROLE)) {
            final var userRaids = token.getClaims().get("user_raids");

            if (userRaids instanceof List) {
                canRead = (canRead) ? canRead : ((List<?>) userRaids).contains(handle);
                canWrite = ((List<?>) userRaids).contains(handle);
            }
        }

        if (authorities.contains(RAID_ADMIN_ROLE)) {
            final var adminRaids = token.getClaims().get("admin_raids");
            if (adminRaids instanceof List) {
                canRead = (canRead) ? canRead : ((List<?>) adminRaids).contains(handle);
                canWrite = (canWrite) ? canWrite : ((List<?>) adminRaids).contains(handle);
            }
        }

        return Optional.of(RaidPermissionsDto.builder()
                .servicePointMatch(servicePointMatch)
                .read(canRead)
                .write(canWrite)
                .build());
    }
}