package au.org.raid.inttest.auth;

import au.org.raid.idl.raidv2.model.ApiKey;
import au.org.raid.idl.raidv2.model.GenerateApiTokenRequest;
import au.org.raid.idl.raidv2.model.RaidListRequestV2;
import au.org.raid.idl.raidv2.model.ServicePoint;
import au.org.raid.inttest.IntegrationTestCase;
import feign.FeignException.Forbidden;
import feign.FeignException.Unauthorized;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;
import static au.org.raid.api.test.util.BddUtil.*;
import static au.org.raid.db.jooq.api_svc.enums.IdProvider.RAIDO_API;
import static au.org.raid.db.jooq.api_svc.enums.UserRole.SP_USER;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApiKeyTest extends IntegrationTestCase {


    @Test
    void crossAccountCallShouldNotWork() {
        GIVEN("Service point exists");
        ServicePoint servicePoint = createServicePoint(null);

        WHEN("api-key is created on the service point");
        var spUser = createApiKeyUser(
                servicePoint.getId(), servicePoint.getName() + "-key", SP_USER);
        var basicApiAsSpUser = basicRaidExperimentalClient(spUser.getApiToken());

        THEN("should be able to list raids on that service point with the api-key");
        basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                servicePointId(servicePoint.getId()));

        AND("should not be able to list raids on a different service point");
        assertThatThrownBy(() ->
                basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                        servicePointId(RAIDO_SP_ID))
        ).
                isInstanceOf(Forbidden.class).
                hasMessageContaining("You don't have permission to access RAiDs with a" +
                        " service point of " + RAIDO_SP_ID);
    }

    @Test
    void disabledApiKeyShouldNotWork() {
        GIVEN("Service point exists");
        ServicePoint servicePoint = createServicePoint(null);

        WHEN("api-key is created on the service point");
        var adminApi = adminExperimentalClientAs(operatorToken);
        LocalDateTime expiry = LocalDateTime.now().plusDays(30);

        var spUser = adminApi.updateApiKey(new ApiKey().
                servicePointId(servicePoint.getId()).
                idProvider(RAIDO_API.getLiteral()).
                role(SP_USER.getLiteral()).
                subject(servicePoint.getName() + "-key").
                enabled(true).
                tokenCutoff(expiry.atOffset(UTC))
        );

        AND("api token is generated");
        var spUserToken = adminApi.generateApiToken(new GenerateApiTokenRequest().
                apiKeyId(spUser.getId()));
        var basicApiAsSpUser = basicRaidExperimentalClient(spUserToken.getApiToken());

        THEN("should be able to list raids on that service point with the api-key");
        basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                servicePointId(servicePoint.getId()));

        WHEN("api-key is disabled");
        spUser.setEnabled(false);
        adminExperimentalClientAs(operatorToken).updateApiKey(spUser);

        AND("should not be able to list raids using the generated token");
        assertThatThrownBy(() ->
                basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                        servicePointId(servicePoint.getId()))
        ).
                isInstanceOf(Unauthorized.class);
    }

}
