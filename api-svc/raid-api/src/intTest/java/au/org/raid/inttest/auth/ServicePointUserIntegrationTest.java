package au.org.raid.inttest.auth;

import au.org.raid.inttest.AbstractIntegrationTest;
import au.org.raid.inttest.dto.GroupMemberDto;
import au.org.raid.inttest.dto.KeycloakGroup;
import au.org.raid.inttest.service.AuthClient;
import au.org.raid.inttest.service.TestClient;
import au.org.raid.inttest.service.TokenService;
import feign.FeignException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Disabled
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServicePointUserIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TestClient testClient;

    @Test
    @Order(1)
    @DisplayName("Revoke service-point-user role")
    void revokeRole() {

        final var authClient = new AuthClient();

        var group = authClient.getGroup(adminToken)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        var raidTestUser = getRaidTestUser(group);

        assertThat(raidTestUser.getRoles()).contains("service-point-user");

        authClient.revoke(group.getId(), raidTestUser.getId(), adminToken);

        group = authClient.getGroup(adminToken)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        raidTestUser = getRaidTestUser(group);

        assertThat(raidTestUser.getRoles()).doesNotContain("service-point-user");

        try {
            final var raidApi = testClient.raidApi(tokenService.getToken(raidAuUser, raidAuPassword));

            raidApi.mintRaid(createRequest);
            fail("Mint should fail");
        } catch (final FeignException e) {
            assertThat(e.status()).isEqualTo(403);
        } catch (final Exception e) {
            fail(e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Grant service-point-user role")
    void grantRole() {

        final var authClient = new AuthClient();

        var group = authClient.getGroup(adminToken)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        var raidTestUser = getRaidTestUser(group);

        assertThat(raidTestUser.getRoles()).doesNotContain("service-point-user");

        authClient.grant(group.getId(), raidTestUser.getId(), adminToken);

        group = authClient.getGroup(adminToken)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        raidTestUser = getRaidTestUser(group);

        assertThat(raidTestUser.getRoles()).contains("service-point-user");

        try {
            final var raidApi = testClient.raidApi(tokenService.getToken(raidAuUser, raidAuPassword));

            raidApi.mintRaid(createRequest);
        } catch (Exception e) {
            fail("Mint should be successful", e);
        }
    }

    private GroupMemberDto getRaidTestUser(final KeycloakGroup group) {
        return group.getMembers().stream()
                .filter(m -> m.getAttributes().get("username").contains("raid-test-user"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("raid-test-user not found"));

    }
}
