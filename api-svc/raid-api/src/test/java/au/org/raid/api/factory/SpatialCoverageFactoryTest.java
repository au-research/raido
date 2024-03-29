package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.SpatialCoveragePlace;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class SpatialCoverageFactoryTest {
    private final SpatialCoverageFactory factory = new SpatialCoverageFactory();

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = "_id";
        final var schemaUri = "schema-uri";
        final var places = List.of(new SpatialCoveragePlace());

        final var result = factory.create(id, schemaUri, places);

        assertThat(result.getId(), is(id));
        assertThat(result.getSchemaUri(), is(schemaUri));
        assertThat(result.getPlace(), is(places));
    }
}