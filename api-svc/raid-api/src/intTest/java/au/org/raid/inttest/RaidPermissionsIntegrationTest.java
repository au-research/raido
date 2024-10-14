package au.org.raid.inttest;

import au.org.raid.inttest.config.AuthConfig;
import au.org.raid.inttest.dto.keycloak.RaidUserPermissionsRequest;
import au.org.raid.inttest.service.Handle;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Slf4j
public class RaidPermissionsIntegrationTest extends AbstractIntegrationTest {
    @Autowired
    private AuthConfig authConfig;

    // TODO: Raid admin has access to write to a raid in admin_raids
    // TODO: Raid admin has access to embargoed raid in admin_raids
    // TODO: Raid admin does not have access to write to a raid not in admin_raids
    // TODO: Raid admin has access to embargoed raid in admin_raids
    // TODO: service point user has read/write access to all raids for service point
    // TODO: service point user has no access to any raids outside service point


    @Test
    @DisplayName("Raid user cannot read raid without permissions")
    void raidUserReadWithoutPermissions() {
        final var mintedRaid = testClient.raidApi(authConfig.getRaidAdmin())
                .mintRaid(createRequest).getBody();

        assert mintedRaid != null;

        final var handle = new Handle(mintedRaid.getIdentifier().getId());

        try {
            testClient.raidApi(authConfig.getRaidUser())
                    .findRaidByName(handle.getPrefix(), handle.getSuffix(), null);
        } catch (final FeignException e) {
            assertThat(e.status()).isEqualTo(403);
            // pass
        } catch (final Exception e) {
            log.error("Failed", e);
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Raid user cannot update raid without permissions")
    void raidUserUpdateWithoutPermissions() {
        final var mintedRaid = testClient.raidApi(authConfig.getRaidAdmin()).mintRaid(createRequest).getBody();

        assert mintedRaid != null;

        final var handle = new Handle(mintedRaid.getIdentifier().getId());

        try {
            testClient.raidApi(authConfig.getRaidUser())
                    .updateRaid(handle.getPrefix(), handle.getSuffix(), raidUpdateRequestFactory.create(mintedRaid));
        } catch (final FeignException e) {
            assertThat(e.status()).isEqualTo(403);
            // pass
        } catch (final Exception e) {
            log.error("Failed", e);
            fail(e.getMessage());
        }
    }

    @Test
    @DisplayName("Raid user can read raid with permissions")
    void raidUserReadWithPermissions() {
        final var mintedRaid = testClient.raidApi(authConfig.getRaidAdmin()).mintRaid(createRequest).getBody();

        assert mintedRaid != null;

        final var handle = new Handle(mintedRaid.getIdentifier().getId());

        final var raidUserPermissionsRequest = RaidUserPermissionsRequest.builder()
                .userId("da3f40a0-7e61-4c4c-b4f3-fdcaee7efa09")
                .handle(handle.toString())
                .build();

        keycloakClient.keycloakApi(authConfig.getRaidPermissionsAdmin())
                .addRaidUser(raidUserPermissionsRequest);

        try {
            testClient.raidApi(authConfig.getRaidUser())
                    .findRaidByName(handle.getPrefix(), handle.getSuffix(), null);
        } catch (final Exception e) {
            fail("Raid user should be able to read raid");
        } finally {
            keycloakClient.keycloakApi(authConfig.getRaidPermissionsAdmin()).removeRaidUser(raidUserPermissionsRequest);
        }
    }

    @Test
    @DisplayName("Raid user can update raid with permissions")
    void raidUserUpdateWithPermissions() {
        final var mintedRaid = testClient.raidApi(authConfig.getRaidAdmin()).mintRaid(createRequest).getBody();

        assert mintedRaid != null;

        final var handle = new Handle(mintedRaid.getIdentifier().getId());

        final var raidUserPermissionsRequest = RaidUserPermissionsRequest.builder()
                .userId("da3f40a0-7e61-4c4c-b4f3-fdcaee7efa09")
                .handle(handle.toString())
                .build();

        keycloakClient.keycloakApi(authConfig.getRaidPermissionsAdmin())
                .addRaidUser(raidUserPermissionsRequest);

        try {
            testClient.raidApi(authConfig.getRaidUser())
                    .updateRaid(handle.getPrefix(), handle.getSuffix(), raidUpdateRequestFactory.create(mintedRaid));
        } catch (final Exception e) {
            fail("Raid user should be able to update raid");
        } finally {
            keycloakClient.keycloakApi(authConfig.getRaidPermissionsAdmin()).removeRaidUser(raidUserPermissionsRequest);
        }
    }

    @Test
    @DisplayName("Raid admin can't read embargoed raids from same service point if no permissions")
    void raidAdminUnableToReadEmbargoedRaids() {
        final var raid1 = testClient.raidApi(authConfig.getRaidAu()).mintRaid(createRequest).getBody();
        assert raid1 != null;
        final var raid2 = testClient.raidApi(authConfig.getRaidAdmin()).mintRaid(createRequest).getBody();
        assert raid2 != null;

        try {
            final var response = testClient.raidApi(authConfig.getRaidAdmin()).findAllRaids(null);

            assert response.getBody() != null;

            final var raids = response.getBody().stream().map(raidDto -> raidDto.getIdentifier().getId()).toList();
            assertThat(raids).contains(raid2.getIdentifier().getId());
            assertThat(raids).doesNotContain(raid1.getIdentifier().getId());
        } catch (final Exception e) {
            log.error("Failed", e);
            fail(e.getMessage());
        }
    }
}