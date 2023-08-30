package au.org.raid.api.validator;

import org.geonames.WebService;
import org.junit.jupiter.api.Test;

class GeoNamesClientTest {
    @Test
    void name() throws Exception {
        final var id = 7839505;
        WebService.setUserName("test.raid.org.au");

        final var toponym = WebService.get(id, null, null);

    }
}