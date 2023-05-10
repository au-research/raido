package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.exception.InvalidAccessException;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.RaidoStableV2Service;
import raido.apisvc.service.raid.validation.RaidoStableV2ValidationService;
import raido.idl.raidv2.api.RaidoStableV2Api;
import raido.idl.raidv2.model.RaidSchemaV2;
import raido.idl.raidv2.model.UpdateRaidV2Request;

import java.util.ArrayList;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class RaidoStableV2 implements RaidoStableV2Api {

  private final RaidoStableV2ValidationService validationService;
  private final RaidoStableV2Service raidoStableV2Service;

  private final RaidService raidService;

  public RaidoStableV2(final RaidoStableV2ValidationService validationService, final RaidoStableV2Service raidoStableV2Service, final RaidService raidService) {
    this.validationService = validationService;
    this.raidoStableV2Service = raidoStableV2Service;
    this.raidService = raidService;
  }

  @Override
  public RaidSchemaV2 readRaidV2(final String prefix, final String suffix) {
    return null;
  }

  @Override
  public RaidSchemaV2 updateRaidV2(final String prefix, final String suffix, final UpdateRaidV2Request request) {
    final var handle = String.join("/", prefix, suffix);
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, request.getId().getIdentifierServicePoint());

    if (!raidService.isEditable(user, request.getId().getIdentifierServicePoint())) {
      throw new InvalidAccessException("This service point does not allow Raids to be edited in the app.");
    }

    final var failures = new ArrayList<>(validationService.validateForUpdate(handle, request));

    if( !failures.isEmpty() ){
      throw new ValidationException(failures);
    }

    return raidoStableV2Service.updateRaid(request);  }
}
