package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AlternateIdentifier;
import au.org.raid.idl.raidv2.model.AlternateIdentifierBlock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class AlternateIdentifierFactoryTest {
    private final AlternateIdentifierFactory alternateIdentifierFactory = new AlternateIdentifierFactory();

    @Test
    @DisplayName("If AlternateIdentifierBlock is null returns null")
    void returnsNull() {
        assertThat(alternateIdentifierFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If AlternateIdentifierBlock has empty fields returns object with empty fields ")
    void emptyFields() {
        assertThat(alternateIdentifierFactory.create(new AlternateIdentifierBlock()), is(new AlternateIdentifier()));
    }

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = "_id";
        final var type = "_type";

        final var alternateIdentifier = new AlternateIdentifierBlock()
            .alternateIdentifier(id)
            .alternateIdentifierType(type);

        final var expected = new AlternateIdentifier()
            .id(id)
            .type(type);

        assertThat(alternateIdentifierFactory.create(alternateIdentifier), is(expected));
    }
}