package raido.inttest.endpoint.raidv2.stable;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.MintResponse;
import raido.idl.raidv2.model.RaidDto;
import raido.inttest.TestData;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.fail;
import static raido.apisvc.endpoint.raidv2.AuthzUtil.RAIDO_SP_ID;

public class BackwardsCompatabilityIntegrationTest extends AbstractStableIntegrationTest {

    @Test
    @Disabled("Unimplemented")
    @DisplayName("Read a raid created by V1 experimental api in stable api")
    void readExperimentalRaid() {
        final var today = LocalDate.now();
        try {
            final MintResponse mintResponse = experimentalApi.mintRaidoSchemaV1(TestData.mintRaidoSchemaV1Request(RAIDO_SP_ID));

            final String[] split = mintResponse.getRaid().getHandle().split("/");
            final var prefix = split[0];
            final var suffix = split[1];

            final RaidDto raidDto = raidApi.readRaidV1(prefix, suffix);





        } catch (Exception e) {
            fail(e.getMessage());
        }



    }
}