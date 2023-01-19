package raido.apisvc.endpoint.raidv2;

import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.raid.RaidService;
import raido.apisvc.service.raid.validation.RaidoSchemaV1ValidationService;
import raido.idl.raidv2.api.BasicRaidStableApi;
import raido.idl.raidv2.model.CreateRaidV1Request;
import raido.idl.raidv2.model.RaidSchemaV1;
import raido.idl.raidv2.model.UpdateRaidV1Request;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;

@Scope(proxyMode = TARGET_CLASS)
@RestController
@Transactional
public class BasicRaidStable implements BasicRaidStableApi {
  private final RaidoSchemaV1ValidationService validSvc;
  private RaidService raidSvc;

  public BasicRaidStable(RaidoSchemaV1ValidationService validSvc) {
    this.validSvc = validSvc;
  }

  @Override
  public RaidSchemaV1 createRaidV1(CreateRaidV1Request request) {
    return null;
  }

  @Override
  public List<RaidSchemaV1> listRaidsV1(Integer servicePointId) {
    return null;
  }

  @Override
  public RaidSchemaV1 readRaidV1(String handle) {
    return null;
  }

  @Override
  public RaidSchemaV1 updateRaidV1(String handle, UpdateRaidV1Request updateRaidV1Request) {
    return null;
  }
}
