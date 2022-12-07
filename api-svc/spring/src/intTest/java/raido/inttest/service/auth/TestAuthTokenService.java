package raido.inttest.service.auth;

import jakarta.annotation.PostConstruct;
import org.jooq.DSLContext;
import org.jooq.JSONB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import raido.apisvc.service.auth.RaidV2ApiKeyAuthService;
import raido.apisvc.service.raidv1.RaidV1AuthService;
import raido.apisvc.spring.config.environment.RaidV1AuthProps;
import raido.apisvc.spring.config.environment.RaidV2ApiKeyAuthProps;
import raido.apisvc.util.Log;
import raido.db.jooq.api_svc.enums.UserRole;
import raido.db.jooq.raid_v1_import.tables.records.TokenRecord;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.jooq.impl.DSL.inline;
import static raido.apisvc.spring.security.raidv2.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.apisvc.util.Log.to;
import static raido.db.jooq.api_svc.enums.IdProvider.RAIDO_API;
import static raido.db.jooq.api_svc.tables.AppUser.APP_USER;
import static raido.db.jooq.raid_v1_import.tables.Token.TOKEN;

@Component
public class TestAuthTokenService {
  private static final Log log = to(TestAuthTokenService.class);

  @Autowired protected DSLContext db;
  @Autowired protected RaidV1AuthProps authProps;
  @Autowired protected RaidV2ApiKeyAuthProps authApiKeyProps;

  /* v1TestOwner is only used for minting via raidV1 endpoint, which is 
  only designed for use by RDM anyway.  The logic in the endpoint implementation
  requires it to match an existing service-point, might as well use the real 
  one. */
  private String v1TestOwner = "RDM@UQ";

  /**
   Doing this eagerly, so the execution time is not counted against
   whatever test happens to run first.
   */
  @PostConstruct
  public void setup() {
    log.info("setup()");
  }

  @Transactional
  public String initRaidV1TestToken() {

    var authSvc = new RaidV1AuthService(db, authProps);

    TokenRecord record = db.newRecord(TOKEN);
    String testToken = authSvc.sign(v1TestOwner);
    record.setName(v1TestOwner).
      setEnvironment("test").
      setDateCreated(LocalDateTime.now()).
      setToken(testToken).
      setS3Export(JSONB.valueOf("{}")).
      merge();

    return record.getToken();
  }
  
  @Transactional
  public String bootstrapToken(long svcPointId, String subject, UserRole role) {
    LocalDateTime expiry = LocalDateTime.now().plusDays(30);
    var apiKeyId = insertApiKey(svcPointId, subject, role); 

    var apiToken = RaidV2ApiKeyAuthService.sign(
       authApiKeyProps.signingAlgo,
        anAuthzTokenPayload().
          withAppUserId(apiKeyId).
          withServicePointId(svcPointId).
          withSubject(subject).
          withClientId(RAIDO_API.getLiteral()).
          withEmail(subject).
          withRole(role.getLiteral()).
          build(),
        expiry.toInstant(ZoneOffset.UTC),
      authApiKeyProps.issuer );
    
    return apiToken;
  }

  private Long insertApiKey(
    long servicePointId,
    String subject,
    UserRole role
  ) {
    return db.insertInto(APP_USER).
      set(APP_USER.SERVICE_POINT_ID, servicePointId).
      set(APP_USER.EMAIL, subject).
      set(APP_USER.CLIENT_ID, RAIDO_API.getLiteral()).
      set(APP_USER.ID_PROVIDER, RAIDO_API).
      set(APP_USER.SUBJECT, subject).
      set(APP_USER.ROLE, role).
      onConflict(
        APP_USER.EMAIL,
        APP_USER.CLIENT_ID,
        APP_USER.SUBJECT).
      // inline needed because: https://stackoverflow.com/a/73782610/924597
      where(APP_USER.ENABLED.eq(inline(true))).
      doUpdate().
      set(APP_USER.TOKEN_CUTOFF, (LocalDateTime) null).
      returningResult(APP_USER.ID).
      fetchOneInto(Long.class);
  }

}
