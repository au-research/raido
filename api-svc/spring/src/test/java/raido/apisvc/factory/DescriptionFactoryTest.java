package raido.apisvc.factory;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import raido.idl.raidv2.model.Description;
import raido.idl.raidv2.model.DescriptionBlock;
import raido.idl.raidv2.model.DescriptionType;
import raido.idl.raidv2.model.DescriptionTypeWithSchemeUri;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class DescriptionFactoryTest {
    private static final String PRIMARY_ID =
        "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/primary.json";
    private static final String ALTERNATIVE_ID =
        "https://github.com/au-research/raid-metadata/blob/main/scheme/description/type/v1/alternative.json";
    private static final String DESCRIPTION_TYPE_SCHEME_URI =
        "https://github.com/au-research/raid-metadata/tree/main/scheme/description/type/v1/";
    private static final String DESCRIPTION = "Test Description";

    private final DescriptionFactory descriptionFactory = new DescriptionFactory();

    @Test
    @DisplayName("Sets primary description type")
    void setsPrimaryType() {
        final var description = new DescriptionBlock()
            .description(DESCRIPTION)
            .type(DescriptionType.PRIMARY_DESCRIPTION);

        final var result = descriptionFactory.create(description);

        final var expected = new Description()
            .description(DESCRIPTION)
            .type(new DescriptionTypeWithSchemeUri()
                .id(PRIMARY_ID)
                .schemeUri(DESCRIPTION_TYPE_SCHEME_URI));

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Sets alternative description type")
    void setsAlternativeType() {
        final var description = new DescriptionBlock()
            .description(DESCRIPTION)
            .type(DescriptionType.ALTERNATIVE_DESCRIPTION);

        final var result = descriptionFactory.create(description);

        final var expected = new Description()
            .description(DESCRIPTION)
            .type(new DescriptionTypeWithSchemeUri()
                .id(ALTERNATIVE_ID)
                .schemeUri(DESCRIPTION_TYPE_SCHEME_URI));

        assertThat(result, is(expected));
    }

    @Test
    @DisplayName("Null DescriptionBlock returns null")
    void returnsNull() {
        assertThat(descriptionFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("Doesn't throw NullPointerException")
    void noNpe() {
        final var description = new DescriptionBlock();

        final var result = descriptionFactory.create(description);

        final var expected = new Description()
            .type(new DescriptionTypeWithSchemeUri()
                .schemeUri(DESCRIPTION_TYPE_SCHEME_URI));

        assertThat(result, is(expected));
    }
}