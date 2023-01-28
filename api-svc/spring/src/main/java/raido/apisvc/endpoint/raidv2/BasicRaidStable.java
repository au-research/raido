package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.validation.RaidoSchemaV1ValidationService;
import raido.idl.raidv2.api.BasicRaidStableApi;
import raido.idl.raidv2.model.CreateRaidSchemaV1;
import raido.idl.raidv2.model.RaidSchemaV1;
import raido.idl.raidv2.model.UpdateRaidV1Request;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class BasicRaidStable implements BasicRaidStableApi {
  private final RaidoSchemaV1ValidationService validSvc;
  private final RaidService raidSvc;

  public BasicRaidStable(RaidoSchemaV1ValidationService validSvc, RaidService raidSvc) {
    this.validSvc = validSvc;
    this.raidSvc = raidSvc;
  }

  @Override
  public RaidSchemaV1 readRaidV1(String handle) {
    var user = getAuthzPayload();
    var data = raidSvc.readRaidV1(handle);
    guardOperatorOrAssociated(user, data.getMintRequest().getServicePointId());
    return data;
  }

  @Override
  public RaidSchemaV1 createRaidV1(Integer xRaidoServicePointId, CreateRaidSchemaV1 createRaidSchemaV1) {
    return null;
  }

  @Override
  public List<RaidSchemaV1> listRaidsV1(Integer servicePointId) {
    return null;
  }

  @Override
  public RaidSchemaV1 updateRaidV1(Integer xRaidoServicePointId, String handle, UpdateRaidV1Request updateRaidV1Request) {
    return null;
  }
}
