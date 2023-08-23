package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.RelatedRaid;
import au.org.raid.idl.raidv2.model.RelatedRaidBlock;
import au.org.raid.idl.raidv2.model.RelatedRaidType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

class RelatedRaidFactoryTest {
    public static final String TYPE_SCHEME_URI = "https://github.com/au-research/raid-metadata/tree/main/scheme/related-raid/type/v1/";
    private final RelatedRaidFactory relatedRaidFactory = new RelatedRaidFactory();

    @Test
    @DisplayName("If RelatedRaidBlock is null returns null")
    void returnsNull() {
        assertThat(relatedRaidFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If RelatedRaidBlock has empty fields returns empty fields")
    void returnsEmptyFields() {
        final var expected = new RelatedRaid()
                .type(new RelatedRaidType()
                        .schemeUri(TYPE_SCHEME_URI));

        assertThat(relatedRaidFactory.create(new RelatedRaidBlock()), is(expected));
    }

    @Test
    @DisplayName("Sets all fields")
    void setsAllFields() {
        final var id = "_id";
        final var typeId = "type-id";

        final var relatedRaid = new RelatedRaidBlock()
                .relatedRaid(id)
                .relatedRaidType(typeId);

        final var expected = new RelatedRaid()
                .id(id)
                .type(new RelatedRaidType()
                        .id(typeId)
                        .schemeUri(TYPE_SCHEME_URI)
                );

        assertThat(relatedRaidFactory.create(relatedRaid), is(expected));
    }
}