package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.exception.ClosedRaidException;
import au.org.raid.api.exception.InvalidAccessException;
import au.org.raid.api.exception.ValidationException;
import au.org.raid.api.service.raid.RaidStableV1Service;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.util.SchemaValues;
import au.org.raid.api.validator.ValidationService;
import au.org.raid.idl.raidv2.api.RaidoStableV1Api;
import au.org.raid.idl.raidv2.model.RaidCreateRequest;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.RaidUpdateRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
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
public class RaidoStableV1 implements RaidoStableV1Api {
    private final ValidationService validationService;
    private final RaidStableV1Service raidService;

    public RaidoStableV1(final ValidationService validationService, final RaidStableV1Service raidService) {
        this.validationService = validationService;
        this.raidService = raidService;
    }

    @Override
    public ResponseEntity<RaidDto> readRaidV1(final String prefix, final String suffix) {
        var user = getApiToken();
        //return 403 if raid is confidential and doesn't have same service point as user

        final var handle = String.join("/", prefix, suffix);
        var raid = raidService.read(handle);

        if (!raid.getIdentifier().getOwner().getServicePoint().equals(user.getServicePointId())
                && !raid.getAccess().getType().getId().equals(SchemaValues.ACCESS_TYPE_OPEN.getUri())) {
            throw new ClosedRaidException(raid);
        }

        return ResponseEntity.ok(raid);
    }


    @Override
    public ResponseEntity<RaidDto> createRaidV1(final RaidCreateRequest request) {
        final var user = getApiToken();

        if (!raidService.isEditable(user, user.getServicePointId())) {
            throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
        }

        final var failures = new ArrayList<>(validationService.validateForCreate(request));

        if (!failures.isEmpty()) {
            throw new ValidationException(failures);
        }

        IdentifierUrl id = raidService.mintRaidSchemaV1(
                request, user.getServicePointId());

        return ResponseEntity.created(URI.create(id.formatUrl()))
                .body(raidService.read(id.handle().format()));
    }

    @Override
    public ResponseEntity<List<RaidDto>> listRaidsV1(final Long servicePoint) {
        var user = getApiToken();

        return ResponseEntity.ok(Optional.ofNullable(servicePoint)
                .map(raidService::list)
                .orElse(raidService.list(user)));
    }

    @Override
    public ResponseEntity<RaidDto> updateRaidV1(final String prefix, final String suffix, RaidUpdateRequest request) {
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

        return ResponseEntity.ok(raidService.update(request));
    }
}