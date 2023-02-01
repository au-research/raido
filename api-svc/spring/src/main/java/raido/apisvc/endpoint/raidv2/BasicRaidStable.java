package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.validation.RaidSchemaV1ValidationService;
import raido.idl.raidv2.api.BasicRaidStableApi;
import raido.idl.raidv2.model.*;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.transaction.annotation.Propagation.NEVER;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static raido.apisvc.service.raid.RaidoSchemaV1Util.mintFailed;

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
    guardOperatorOrAssociated(user, data.getMintRequest().getServicePointId());
    return data;
  }

  @Override
  @Transactional(propagation = NEVER)
  public RaidSchemaV1 createRaidV1(CreateRaidV1Request request) {
    var mint = request.getMintRequest();
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, mint.getServicePointId());

    var failures = validationService.validateForCreate(request.getMetadata());

    if( !failures.isEmpty() ){
      throw new ValidationException(failures);
    }

    String handle = raidService.mintRaidSchemaV1(request);

    return raidService.readRaidV1(handle);
  }

  @Override
  public List<RaidSchemaV1> listRaidsV1(Integer servicePointId) {
    return null;
  }

  @Override
  public RaidSchemaV1 updateRaidV1(String handle, UpdateRaidV1Request request) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, request.getMintRequest().getServicePointId());

    var failures = validationService.validateForUpdate(handle, request.getMetadata());

    if( !failures.isEmpty() ){
      throw new ValidationException(failures);
    }

    return raidService.updateRaidV1(request.getMintRequest(), request.getMetadata());
  }
}
