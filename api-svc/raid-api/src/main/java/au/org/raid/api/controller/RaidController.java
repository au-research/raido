package au.org.raid.api.controller;

import au.org.raid.api.exception.ClosedRaidException;
import au.org.raid.api.exception.CrossAccountAccessException;
import au.org.raid.api.exception.ServicePointNotFoundException;
import au.org.raid.api.exception.ValidationException;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.ServicePointService;
import au.org.raid.api.service.raid.RaidService;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.api.validator.ValidationService;
import au.org.raid.idl.raidv2.api.RaidApi;
import au.org.raid.idl.raidv2.model.*;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@CrossOrigin
@RestController
@RequiredArgsConstructor
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
public class RaidController implements RaidApi {
    public static final String SERVICE_POINT_GROUP_ID_CLAIM = "service_point_group_id";
    private final ValidationService validationService;
    private final RaidService raidService;
    private final RaidIngestService raidIngestService;
    private final RaidHistoryService raidHistoryService;
    private final ServicePointService servicePointService;

    @Override
    public ResponseEntity<RaidDto> findRaidByName(final String prefix, final String suffix, final Integer version) {
        final var servicePointId = getServicePointId();

        final var handle = String.join("/", prefix, suffix);
        var raidOptional = raidService.findByHandle(handle)
                .or(Optional::empty);

        if (raidOptional.isPresent()) {
            final var raid = raidOptional.get();

            if (!raid.getIdentifier().getOwner().getServicePoint().equals(servicePointId)
                    && !raid.getAccess().getType().getId().equals(SchemaValues.ACCESS_TYPE_OPEN.getUri())) {
                throw new ClosedRaidException(raid);
            }
        }

        if (version != null) {
            raidOptional = raidHistoryService.findByHandleAndVersion(handle, version);
        }

        return ResponseEntity.of(raidOptional);
    }


    @Override
    public ResponseEntity<RaidDto> mintRaid(final RaidCreateRequest request) {
        final var servicePointId = getServicePointId();

        final var failures = new ArrayList<>(validationService.validateForCreate(request));

        if (!failures.isEmpty()) {
            throw new ValidationException(failures);
        }

        final var raidDto = raidService.mint(request, servicePointId);

        return ResponseEntity.created(URI.create(raidDto.getIdentifier().getId())).body(raidDto);
    }

    @Override
    public ResponseEntity<List<RaidDto>> findAllRaids(final Long servicePoint, final List<String> includeFields) {
        final var servicePointId = getServicePointId();

        final var raids = Optional.ofNullable(servicePoint)
                .map(raidIngestService::findAllByServicePointId)
                .orElse(raidIngestService.findAllByServicePointIdOrNotConfidential(servicePointId));

        if (includeFields != null && !includeFields.isEmpty()) {
            return ResponseEntity.ok(filterFields(raids, includeFields));
        }

        return ResponseEntity.ok(raids);
    }

    @Override
    public ResponseEntity<RaidDto> updateRaid(final String prefix, final String suffix, RaidUpdateRequest request) {
        final var servicePointId = getServicePointId();

        if (!request.getIdentifier().getOwner().getServicePoint().equals(servicePointId)) {
            throw new CrossAccountAccessException(servicePointId);
        }

        final var handle = String.join("/", prefix, suffix);

        final var failures = new ArrayList<>(validationService.validateForUpdate(handle, request));

        if (!failures.isEmpty()) {
            throw new ValidationException(failures);
        }

        return ResponseEntity.ok(raidService.update(request, servicePointId));
    }

    @Override
    public ResponseEntity<List<RaidChange>> raidHistory(final String prefix, final String suffix) {

        final var handle = prefix + "/" + suffix;

        final var raidOptional = raidService.findByHandle(handle);

        if (raidOptional.isPresent()) {
            final var raid = raidOptional.get();

            if (!raid.getAccess().getType().getId().equals(SchemaValues.ACCESS_TYPE_OPEN.getUri())) {
                final var servicePointId = getServicePointId();

                if (!raid.getIdentifier().getOwner().getServicePoint().equals(servicePointId)) {
                    throw new CrossAccountAccessException(servicePointId);
                }
            }
        }

        return ResponseEntity.ok(raidHistoryService.findAllChangesByHandle(handle));
    }

    @Override
    @PreAuthorize("hasRole('contributor-writer')")
    public ResponseEntity<RaidDto> patchContributors(final String prefix, final String suffix, final ContributorPatchRequest contributorPatchRequest) {
        log.debug("Authorized as contributor-writer");
        return null;
    }

    private long getServicePointId() {
        final var token = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
        final var groupId = (String) token.getClaims().get(SERVICE_POINT_GROUP_ID_CLAIM);

        final var servicePoint = servicePointService.findByGroupId(groupId)
                .orElseThrow(() -> new ServicePointNotFoundException(groupId));

        return servicePoint.getId();
    }

    private List<RaidDto> filterFields(final List<RaidDto> raids, final List<String> includeFields) {
        final var filteredList = new ArrayList<RaidDto>();

        for (final var raid : raids) {
            filteredList.add(filterRaid(raid, includeFields));
        }

        return filteredList;
    }

    @SneakyThrows
    private RaidDto filterRaid(final RaidDto raidDto, final List<String> includeFields) {
        final var filtered = new RaidDto();

        for (final var fieldName : includeFields) {
            final var getter = "get%s".formatted(StringUtils.capitalize(fieldName));
            final var setter = "set%s".formatted(StringUtils.capitalize(fieldName));

            final var method = raidDto.getClass().getMethod(getter);

            final var value = method.invoke(raidDto);

            filtered.getClass().getMethod(setter, method.getReturnType()).invoke(filtered, method.getReturnType().cast(value));
        }

        return filtered;
    }
}

