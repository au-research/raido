package au.org.raid.api.controller;

import au.org.raid.api.exception.ClosedRaidException;
import au.org.raid.api.exception.InvalidAccessException;
import au.org.raid.api.exception.ValidationException;
import au.org.raid.api.service.RaidHistoryService;
import au.org.raid.api.service.RaidIngestService;
import au.org.raid.api.service.raid.RaidService;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.api.validator.ValidationService;
import au.org.raid.idl.raidv2.api.RaidApi;
import au.org.raid.idl.raidv2.model.RaidChange;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.getApiToken;
import static au.org.raid.api.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@CrossOrigin
@SecurityScheme(name = "bearerAuth", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER)
@RequiredArgsConstructor
public class RaidController implements RaidApi {
    private final ValidationService validationService;
    private final RaidService raidService;
    private final RaidIngestService raidIngestService;
    private final RaidHistoryService raidHistoryService;


    @Override
    public ResponseEntity<RaidDto> findRaidByName(final String prefix, final String suffix, final Integer version) {
        var user = getApiToken();
        //return 403 if raid is confidential and doesn't have same service point as user

        final var handle = String.join("/", prefix, suffix);
        var raidOptional = raidService.findByHandle(handle)
                .or(Optional::empty);

        if (raidOptional.isPresent()) {
            final var raid = raidOptional.get();

            if (!raid.getIdentifier().getOwner().getServicePoint().equals(user.getServicePointId())
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
        final var user = getApiToken();

        if (!raidService.isEditable(user, user.getServicePointId())) {
            throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
        }

        final var failures = new ArrayList<>(validationService.validateForCreate(request));

        if (!failures.isEmpty()) {
            throw new ValidationException(failures);
        }

        final var raidDto = raidService.mint(request, user.getServicePointId());

        return ResponseEntity.created(URI.create(raidDto.getIdentifier().getId())).body(raidDto);
    }

    @Override
    public ResponseEntity<List<RaidDto>> findAllRaids(final Long servicePoint, final List<String> includeFields) {
        var user = getApiToken();

        final var raids = Optional.ofNullable(servicePoint)
                .map(raidIngestService::findAllByServicePointId)
                .orElse(raidIngestService.findAllByServicePointIdOrNotConfidential(user));

        if (includeFields != null && !includeFields.isEmpty()) {
            return ResponseEntity.ok(filterFields(raids, includeFields));
        }

        return ResponseEntity.ok(raids);
    }

    @Override
    public ResponseEntity<RaidDto> updateRaid(final String prefix, final String suffix, RaidUpdateRequest request) {
        final var handle = String.join("/", prefix, suffix);
        var user = getApiToken();
        guardOperatorOrAssociated(user, request.getIdentifier().getOwner().getServicePoint());

        if (!raidService.isEditable(user, request.getIdentifier().getOwner().getServicePoint())) {
            throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
        }

        final var failures = new ArrayList<>(validationService.validateForUpdate(handle, request));

        if (!failures.isEmpty()) {
            throw new ValidationException(failures);
        }

        return ResponseEntity.ok(raidService.update(request, user.getServicePointId()));
    }

    @Override
    public ResponseEntity<List<RaidChange>> raidHistory(final String prefix, final String suffix) {
        final var handle = prefix + "/" + suffix;

        final var raidOptional = raidService.findByHandle(handle);

        if (raidOptional.isPresent()) {
            final var raid = raidOptional.get();

            if (!raid.getAccess().getType().getId().equals(SchemaValues.ACCESS_TYPE_OPEN.getUri())) {
                var user = getApiToken();
                guardOperatorOrAssociated(user, raid.getIdentifier().getOwner().getServicePoint());
            }
        }


        return ResponseEntity.ok(raidHistoryService.findAllChangesByHandle(handle));
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

