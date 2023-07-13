package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.exception.InvalidAccessException;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.service.raid.RaidStableV1Service;
import raido.apisvc.service.raid.id.IdentifierUrl;
import raido.apisvc.service.raid.validation.RaidoStableV1ValidationService;
import raido.idl.raidv2.api.RaidoStableV1Api;
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.RaidDto;
import raido.idl.raidv2.model.UpdateRaidV1Request;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.transaction.annotation.Propagation.NEVER;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getApiToken;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;

@Scope(proxyMode = TARGET_CLASS)
@RestController
public class RaidoStableV1 implements RaidoStableV1Api {
  private final RaidoStableV1ValidationService validationService;
  private final RaidStableV1Service raidService;

  public RaidoStableV1(final RaidoStableV1ValidationService validationService, final RaidStableV1Service raidService) {
    this.validationService = validationService;
    this.raidService = raidService;
  }

  @Override
  public RaidDto readRaidV1(final String prefix, final String suffix) {
    final var handle = String.join("/", prefix, suffix);
    var user = getApiToken();
    var data = raidService.readRaidV1(handle);
    guardOperatorOrAssociated(user, data.getId().getIdentifierServicePoint());
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

    if( !failures.isEmpty() ){
      throw new ValidationException(failures);
    }

    IdentifierUrl id = raidService.mintRaidSchemaV1(
      request, user.getServicePointId() );

    return raidService.readRaidV1(id.handle().format());
  }

  @Override
  public List<RaidDto> listRaidsV1(Long servicePointId) {
    var user = getApiToken();
    guardOperatorOrAssociated(user, servicePointId);

    return raidService.listRaidsV1(servicePointId);
  }

  @Override
  public RaidDto updateRaidV1(final String prefix, final String suffix, UpdateRaidV1Request request) {
    final var handle = String.join("/", prefix, suffix);
    var user = getApiToken();
    guardOperatorOrAssociated(user, request.getId().getIdentifierServicePoint());

    if (!raidService.isEditable(user, request.getId().getIdentifierServicePoint())) {
      throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
    }

    final var failures = new ArrayList<>(validationService.validateForUpdate(handle, request));

    if( !failures.isEmpty() ){
      throw new ValidationException(failures);
    }

    return raidService.updateRaidV1(request);
  }
}
