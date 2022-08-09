package raido.inttest.service.auth;

import jakarta.annotation.PostConstruct;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import raido.apisvc.service.raidv1.RaidV1AuthService;
import raido.apisvc.spring.config.environment.RaidV1AuthProps;
import raido.apisvc.util.Log;
import raido.db.jooq.raid_v1_import.tables.records.TokenRecord;

import java.time.LocalDateTime;

import static raido.apisvc.util.Log.to;
import static raido.db.jooq.raid_v1_import.tables.Token.TOKEN;

@Component
public class TestAuthTokenService {
  private static final Log log = to(TestAuthTokenService.class);

  @Autowired protected DSLContext db;
  @Autowired protected RaidV1AuthProps authProps;

  private String testOwner = "raido.inttest";

  /**
   Doing this eagerly, so the execution time is not counted against
   whatever test happens to run first.
   */
  @PostConstruct
  public void setup() {
    log.info("setup()");
  }

  @Transactional
  public String initTestToken() {

    var authSvc = new RaidV1AuthService(db, authProps);

    TokenRecord record = db.newRecord(TOKEN);
    String testToken = authSvc.sign(testOwner);
    record.setName(testOwner).
      setEnvironment("test").
      setDateCreated(LocalDateTime.now()).
      setToken(testToken).
      setS3Export(JSONB.valueOf("{}")).
      merge();

    return record.getToken();
  }

  public String getTestOwner() {
    return testOwner;
  }
}
