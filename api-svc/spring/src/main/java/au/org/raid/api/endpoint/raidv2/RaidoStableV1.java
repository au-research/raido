package au.org.raid.api.endpoint.raidv2;

import au.org.raid.api.exception.InvalidAccessException;
import au.org.raid.api.exception.ValidationException;
import au.org.raid.api.service.raid.RaidStableV1Service;
import au.org.raid.api.service.raid.id.IdentifierUrl;
import au.org.raid.api.validator.RaidoStableV1Validator;
import au.org.raid.idl.raidv2.api.RaidoStableV1Api;
import au.org.raid.idl.raidv2.model.CreateRaidV1Request;
import au.org.raid.idl.raidv2.model.RaidDto;
import au.org.raid.idl.raidv2.model.UpdateRaidV1Request;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.getApiToken;
import static au.org.raid.api.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.transaction.annotation.Propagation.NEVER;

@Scope(proxyMode = TARGET_CLASS)
@RestController
public class RaidoStableV1 implements RaidoStableV1Api {
    private final RaidoStableV1Validator validationService;
    private final RaidStableV1Service raidService;

    public RaidoStableV1(final RaidoStableV1Validator validationService, final RaidStableV1Service raidService) {
        this.validationService = validationService;
        this.raidService = raidService;
    }

    @Override
    public RaidDto readRaidV1(final String prefix, final String suffix) {
        final var handle = String.join("/", prefix, suffix);
        var user = getApiToken();
        var data = raidService.read(handle);
        guardOperatorOrAssociated(user, data.getIdentifier().getOwner().getServicePoint());
        return data;
    }

    @Override
    @Transactional(propagation = NEVER)
    public RaidDto createRaidV1(CreateRaidV1Request request) {
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

        return raidService.read(id.handle().format());
    }

    @Override
    public List<RaidDto> listRaidsV1(Long servicePointId) {
        var user = getApiToken();
        guardOperatorOrAssociated(user, servicePointId);

        return raidService.list(servicePointId);
    }

    @Override
    public RaidDto updateRaidV1(final String prefix, final String suffix, UpdateRaidV1Request request) {
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

        return raidService.update(request);
    }
}