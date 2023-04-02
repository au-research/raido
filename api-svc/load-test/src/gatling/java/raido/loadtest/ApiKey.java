package raido.loadtest;

import com.auth0.jwt.algorithms.Algorithm;
import raido.apisvc.service.auth.RaidV2ApiKeyAuthService;
import raido.db.jooq.api_svc.enums.UserRole;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static raido.apisvc.spring.security.raidv2.AuthzTokenPayload.AuthzTokenPayloadBuilder.anAuthzTokenPayload;
import static raido.db.jooq.api_svc.enums.IdProvider.RAIDO_API;

public class ApiKey {
  private Algorithm signingAlgo;

  // currently hardcoded to the key used in DEMO for legacy migration
  private long apiKeyId = 1000000001;
  private String issuer = "https://localhost:8080";

  public ApiKey(RaidoConfig config) {
    signingAlgo = Algorithm.HMAC256(config.apiKeyJwtSecret);
  }

  public String bootstrapToken(long svcPointId, String subject, UserRole role) {
    LocalDateTime expiry = LocalDateTime.now().plusDays(30);
//    var apiKeyId = insertApiKey(svcPointId, subject, role);

    var apiToken = RaidV2ApiKeyAuthService.sign(
      signingAlgo,
      anAuthzTokenPayload().
        withAppUserId(apiKeyId).
        withServicePointId(svcPointId).
        withSubject(subject).
        withClientId(RAIDO_API.getLiteral()).
        withEmail(subject).
        withRole(role.getLiteral()).
        build(),
      expiry.toInstant(ZoneOffset.UTC),
      issuer);

    return apiToken;
  }

}
