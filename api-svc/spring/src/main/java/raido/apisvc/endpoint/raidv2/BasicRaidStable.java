package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.validation.RaidSchemaV1ValidationService;
import raido.idl.raidv2.api.BasicRaidStableApi;
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.RaidSchemaV1;
import raido.idl.raidv2.model.UpdateRaidV1Request;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.transaction.annotation.Propagation.NEVER;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class BasicRaidStable implements BasicRaidStableApi {
  private final RaidSchemaV1ValidationService validationService;
  private final RaidService raidService;

  public BasicRaidStable(final RaidSchemaV1ValidationService validationService, final RaidService raidService) {
    this.validationService = validationService;
    this.raidService = raidService;
  }

  @Override
  public RaidSchemaV1 readRaidV1(String handle) {
    var user = getAuthzPayload();
    var data = raidService.readRaidV1(handle);
    guardOperatorOrAssociated(user, data.getId().getIdentifierServicePoint());
    return data;
  }

  @Override
  @Transactional(propagation = NEVER)
  public RaidSchemaV1 createRaidV1(CreateRaidV1Request request) {
    final var user = getAuthzPayload();

    final var failures = new ArrayList<>(validationService.validateForCreate(request));

    if( !failures.isEmpty() ){
      throw new ValidationException(failures);
    }

    String handle = raidService.mintRaidSchemaV1(request, user.getServicePointId());

    return raidService.readRaidV1(handle);
  }

  @Override
  public List<RaidSchemaV1> listRaidsV1(Long servicePointId) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, servicePointId);

    return raidService.listRaidsV1(servicePointId);
  }

  @Override
  public RaidSchemaV1 updateRaidV1(String handle, UpdateRaidV1Request request) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, request.getId().getIdentifierServicePoint());

    final var failures = new ArrayList<>(validationService.validateForUpdate(handle, request));

    if( !failures.isEmpty() ){
      throw new ValidationException(failures);
    }

    return raidService.updateRaidV1(request);
  }
}
