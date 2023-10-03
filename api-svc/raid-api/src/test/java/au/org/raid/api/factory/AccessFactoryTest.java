package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class AccessFactoryTest {
    private static final String CLOSED_ID =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/closed.json";
    private static final String OPEN_ID =
            "https://github.com/au-research/raid-metadata/blob/main/scheme/access/type/v1/open.json";
    private static final String ACCESS_TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/access/type/v1/";

    private final AccessFactory accessFactory = new AccessFactory();

    @Test
    @DisplayName("Sets closed access type")
    void setsPrimaryType() {
        final var accessStatement = "This is closed";

        final var access = new AccessBlock()
                .type(AccessType.CLOSED)
                .accessStatement(accessStatement);

        final var result = accessFactory.create(access);

        final var expected = new Access()
                .type(new AccessTypeWithSchemaUri()
                        .id(CLOSED_ID)
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI))
                .accessStatement(new AccessStatement().text(accessStatement));

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Sets alternative access type")
    void setsAlternativeType() {
        final var access = new AccessBlock()
                .type(AccessType.OPEN);

        final var result = accessFactory.create(access);

        final var expected = new Access()
                .type(new AccessTypeWithSchemaUri()
                        .id(OPEN_ID)
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI));

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Null AccessBlock returns null")
    void returnsNull() {
        assertThat(accessFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("Doesn't throw NullPointerException")
    void noNpe() {
        final var access = new AccessBlock();

        final var result = accessFactory.create(access);

        final var expected = new Access()
                .type(new AccessTypeWithSchemaUri()
                        .schemaUri(ACCESS_TYPE_SCHEMA_URI));

        assertThat(result, is(expected));
    }
}