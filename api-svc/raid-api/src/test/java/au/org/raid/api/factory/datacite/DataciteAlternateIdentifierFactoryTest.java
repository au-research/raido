package au.org.raid.api.factory.datacite;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataciteAlternateIdentifierFactoryTest {

    private DataciteAlternateIdentifierFactory dataciteAlternateIdentifierFactory = new DataciteAlternateIdentifierFactory();
    @Test
    @DisplayName("Create set all fields")
    public void create() {
        final var id = "_id";

        final var alternateIdentifier = new AlternateIdentifier()
                .id(id);

        final var result = dataciteAlternateIdentifierFactory.create(alternateIdentifier);

        assertThat(result.getAlternateIdentifier(), is(id));
        assertThat(result.getAlternateIdentifierType(), is("URL"));
    }
}
