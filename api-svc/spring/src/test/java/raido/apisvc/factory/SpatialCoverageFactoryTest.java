package raido.apisvc.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.SpatialCoverage;
import raido.idl.raidv2.model.SpatialCoverageBlock;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class SpatialCoverageFactoryTest {
    private final SpatialCoverageFactory spatialCoverageFactory = new SpatialCoverageFactory();


    @Test
    @DisplayName("If SpatialCoverageBlock is null returns null")
    void returnsNull() {
        assertThat(spatialCoverageFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If SpatialCoverageBlock has empty fields returns empty fields")
    void returnsEmptyFields() {
        assertThat(spatialCoverageFactory.create(new SpatialCoverageBlock()), is(new SpatialCoverage()));
    }

    @Test
    void setAllFields() {
        final var id = "_id";
        final var schemeUri = "scheme-uri";
        final var place = "_place";

        final var spatialCoverage = new SpatialCoverageBlock()
            .spatialCoverage(id)
            .spatialCoverageSchemeUri(schemeUri)
            .spatialCoveragePlace(place);

        final var expected = new SpatialCoverage()
            .id(id)
            .schemeUri(schemeUri)
            .place(place);

        assertThat(spatialCoverageFactory.create(spatialCoverage), is(expected));
    }
}