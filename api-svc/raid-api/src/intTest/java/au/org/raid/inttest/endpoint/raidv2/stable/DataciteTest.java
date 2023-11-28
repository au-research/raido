package au.org.raid.inttest.endpoint.raidv2.stable;

import au.org.raid.api.service.datacite.DataciteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DataciteTest {
    DataciteService dataciteService = new DataciteService();

    @Test
    @DisplayName("Creating a suffix for a datacite raids")
    void checkDataciteSuffixLength() {
        String dataciteSuffix = dataciteService.getDataciteSuffix();
        assertThat(dataciteSuffix.length()).isEqualTo(8);
    }
}
