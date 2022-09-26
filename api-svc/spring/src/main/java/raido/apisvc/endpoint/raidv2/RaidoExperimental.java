package raido.apisvc.endpoint.raidv2;

import org.jooq.DSLContext;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.RestController;
import raido.apisvc.service.auth.AuthzTokenPayload;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.idl.raidv2.api.RaidoExperimentalApi;
import raido.idl.raidv2.model.ServicePoint;

import java.util.List;

import static org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;
import static raido.apisvc.util.ExceptionUtil.notYetImplemented;
import static raido.apisvc.util.Log.to;

@Scope(proxyMode = TARGET_CLASS)
@RestController
public class RaidoExperimental implements RaidoExperimentalApi {
  private static final Log log = to(RaidoExperimental.class);
  // Hardcoded, this is the first ever SP inserted when DB bootstrapped
  public static final long RAIDO_SP_ID = 20_000_000;

  private DSLContext db;
  
  public RaidoExperimental(
    DSLContext db
  ) {
    this.db = db;
  }

  @Override
  public Void raidoKeepme() {
    return null;
  }
}
