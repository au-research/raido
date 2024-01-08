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

import static au.org.raid.api.endpoint.raidv2.AuthzUtil.RAID_AU_SP_ID;
import static au.org.raid.db.jooq.enums.IdProvider.RAIDO_API;
import static au.org.raid.db.jooq.enums.UserRole.SP_USER;
import static java.time.ZoneOffset.UTC;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ApiKeyTest extends IntegrationTestCase {


    @Test
    void crossAccountCallShouldNotWork() {
        ServicePoint servicePoint = createServicePoint(null);

        var spUser = createApiKeyUser(
                servicePoint.getId(), servicePoint.getName() + "-key", SP_USER);
        var basicApiAsSpUser = basicRaidExperimentalClient(spUser.getApiToken());

        basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                servicePointId(servicePoint.getId()));

        assertThatThrownBy(() ->
                basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                        servicePointId(RAID_AU_SP_ID))
        ).
                isInstanceOf(Forbidden.class).
                hasMessageContaining("You don't have permission to access RAiDs with a" +
                        " service point of " + RAID_AU_SP_ID);
    }

    @Test
    void disabledApiKeyShouldNotWork() {
        ServicePoint servicePoint = createServicePoint(null);

        var adminApi = adminExperimentalClientAs(operatorToken);
        LocalDateTime expiry = LocalDateTime.now().plusDays(30);

        var spUser = adminApi.updateApiKey(new ApiKey().
                servicePointId(servicePoint.getId()).
                idProvider(RAIDO_API.getLiteral()).
                role(SP_USER.getLiteral()).
                subject(servicePoint.getName() + "-key").
                enabled(true).
                tokenCutoff(expiry.atOffset(UTC))
        ).getBody();

        var spUserToken = adminApi.generateApiToken(new GenerateApiTokenRequest().
                apiKeyId(spUser.getId())).getBody();
        var basicApiAsSpUser = basicRaidExperimentalClient(spUserToken.getApiToken());

        basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                servicePointId(servicePoint.getId()));

        spUser.setEnabled(false);
        adminExperimentalClientAs(operatorToken).updateApiKey(spUser);

        assertThatThrownBy(() ->
                basicApiAsSpUser.listRaidV2(new RaidListRequestV2().
                        servicePointId(servicePoint.getId()))
        ).
                isInstanceOf(Unauthorized.class);
    }

}
