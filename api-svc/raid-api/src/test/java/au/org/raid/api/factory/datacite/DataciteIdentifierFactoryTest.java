package au.org.raid.api.factory.datacite;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class DataciteIdentifierFactoryTest {

    private final DataciteIdentifierFactory identifierFactory = new DataciteIdentifierFactory();
    @Test
    void setsAllFields() {
        final var handle = "_handle";
        final var identifierType = "identifier-type";

        final var result = identifierFactory.create(handle, identifierType);

        assertThat(result.getIdentifier(), is(handle));
        assertThat(result.getIdentifierType(), is(identifierType));
    }
}