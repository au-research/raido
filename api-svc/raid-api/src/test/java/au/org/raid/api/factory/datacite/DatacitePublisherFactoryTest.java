package au.org.raid.api.factory.datacite;

import au.org.raid.idl.raidv2.model.Owner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DatacitePublisherFactoryTest {
    private final DatacitePublisherFactory publisherFactory = new DatacitePublisherFactory();

    @Test
    @DisplayName("Create sets all fields")
    void create() {
        final var id = "_id";
        final var schemaUri = "schema-uri";

        final var owner = new Owner()
                .id(id)
                .schemaUri(schemaUri);

        final var result = publisherFactory.create(owner);

        assertThat(result.getSchemeUri(), is(schemaUri));
        assertThat(result.getPublisherIdentifier(), is(id));
        assertThat(result.getName(), is(id));
        assertThat(result.getPublisherIdentifierScheme(), is("ROR"));
    }
}