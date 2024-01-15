package au.org.raid.inttest.endpoint.raidv2;

import au.org.raid.inttest.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatusCode;

import static org.assertj.core.api.Assertions.assertThat;

public class OrcidIntegrationTest extends AbstractIntegrationTest {

    @Test
    @DisplayName("Create a RAiD with X as the checksum in the ORCID")
    void xChecksumOrcid() {
        createRequest.getContributor().get(0).setId("https://orcid.org/0009-0001-8177-319X");

        var result = raidApi.mintRaid(createRequest);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatusCode.valueOf(201));
    }
}
