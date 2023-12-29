package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.AccessStatement;
import au.org.raid.idl.raidv2.model.AccessType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class AccessFactoryTest {
    private final AccessFactory accessFactory = new AccessFactory();
    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var statement = new AccessStatement();
        final var type = new AccessType();
        final var embargoExpiry = LocalDate.now();

        final var result = accessFactory.create(statement, type, embargoExpiry);

        assertThat(result.getStatement(), is(statement));
        assertThat(result.getType(), is(type));
        assertThat(result.getEmbargoExpiry(), is(embargoExpiry));
    }
}