package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.RaidoExperimentalApi;
import raido.idl.raidv2.model.MintRaidRequestV1;
import raido.idl.raidv2.model.RaidListItemV1;
import raido.idl.raidv2.model.RaidListRequest;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static raido.apisvc.endpoint.Constant.MAX_EXPERIMENTAL_RECORDS;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.getAuthzPayload;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.guardOperatorOrAssociated;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.tables.Raid.RAID;

@Scope(proxyMode = TARGET_CLASS)
@RestController
public class RaidoExperimental implements RaidoExperimentalApi {
  private static final Log log = to(RaidoExperimental.class);
  /* Hardcoded, we know this statically because we hardcoded the sequence to
   20M and raido is the first SP inserted */
  public static final long RAIDO_SP_ID = 20_000_000;

  private DSLContext db;
  
  public RaidoExperimental(
    DSLContext db
  ) {
    this.db = db;
  }

  @Override
  public List<RaidListItemV1> listRaid(RaidListRequest req) {
    var user = getAuthzPayload();
    guardOperatorOrAssociated(user, req.getServicePointId());
    
    return db.select(RAID.HANDLE).
      from(RAID).
      where(RAID.SERVICE_POINT_ID.eq(req.getServicePointId())).
      orderBy(RAID.DATE_CREATED.desc()).
      limit(MAX_EXPERIMENTAL_RECORDS).
      fetchInto(RaidListItemV1.class);
  }

  @Override
  public RaidListItemV1 mintRaidV1(MintRaidRequestV1 mintRaidRequestV1) {
    return null;
  }
}
