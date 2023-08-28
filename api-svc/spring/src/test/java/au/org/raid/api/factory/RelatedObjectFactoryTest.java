package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.RelatedObject;
import au.org.raid.idl.raidv2.model.RelatedObjectBlock;
import au.org.raid.idl.raidv2.model.RelatedObjectCategory;
import au.org.raid.idl.raidv2.model.RelatedObjectType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

@Component
class RelatedObjectFactoryTest {

    public static final String TYPE_SCHEME_URI = "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/";
    public static final String CATEGORY_SCHEME_URI = "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/category/v1/";
    private RelatedObjectFactory relatedObjectFactory = new RelatedObjectFactory();

    @Test
    @DisplayName("If RelatedObjectBlock is null returns null")
    void returnsNull() {
        assertThat(relatedObjectFactory.create(null), nullValue());
    }

    @Test
    @DisplayName("If RelatedObjectBlock as empty fields returns empty fields")
    void emptyFields() {
        final var expected = new RelatedObject()
                .type(new RelatedObjectType()
                        .schemaUri(TYPE_SCHEME_URI)
                )
                .category(new RelatedObjectCategory()
                        .schemaUri(CATEGORY_SCHEME_URI));

        assertThat(relatedObjectFactory.create(new RelatedObjectBlock()), is(expected));
    }

    @Test
    @DisplayName("Sets input category")
    void setsAllFields() {
        final var id = "_id";
        final var schemaUri = "scheme-uri";
        final var category = "input";
        final var type = "_type";
        final var typeSchemeUri = "type-scheme-uri";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject(id)
                .relatedObjectSchemeUri(schemaUri)
                .relatedObjectCategory(category)
                .relatedObjectType(type)
                .relatedObjectTypeSchemeUri(typeSchemeUri);

        final var expected = new RelatedObject()
                .id(id)
                .schemaUri(schemaUri)
                .type(new RelatedObjectType()
                        .id(type)
                        .schemaUri(TYPE_SCHEME_URI))
                .category(new RelatedObjectCategory()
                        .schemaUri(CATEGORY_SCHEME_URI)
                        .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/input.json")
                );

        assertThat(relatedObjectFactory.create(relatedObject), is(expected));
    }

    @Test
    @DisplayName("Ignores case when setting input category")
    void ignoresCase() {
        final var id = "_id";
        final var schemaUri = "scheme-uri";
        final var category = "Input";
        final var type = "_type";
        final var typeSchemeUri = "type-scheme-uri";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject(id)
                .relatedObjectSchemeUri(schemaUri)
                .relatedObjectCategory(category)
                .relatedObjectType(type)
                .relatedObjectTypeSchemeUri(typeSchemeUri);

        final var expected = new RelatedObject()
                .id(id)
                .schemaUri(schemaUri)
                .type(new RelatedObjectType()
                        .id(type)
                        .schemaUri(TYPE_SCHEME_URI))
                .category(new RelatedObjectCategory()
                        .schemaUri(CATEGORY_SCHEME_URI)
                        .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/input.json")
                );

        assertThat(relatedObjectFactory.create(relatedObject), is(expected));
    }

    @Test
    @DisplayName("Sets output category")
    void setsOutputCategory() {
        final var id = "_id";
        final var schemaUri = "scheme-uri";
        final var category = "output";
        final var type = "_type";
        final var typeSchemeUri = "type-scheme-uri";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject(id)
                .relatedObjectSchemeUri(schemaUri)
                .relatedObjectCategory(category)
                .relatedObjectType(type)
                .relatedObjectTypeSchemeUri(typeSchemeUri);

        final var expected = new RelatedObject()
                .id(id)
                .schemaUri(schemaUri)
                .type(new RelatedObjectType()
                        .id(type)
                        .schemaUri(TYPE_SCHEME_URI))
                .category(new RelatedObjectCategory()
                        .schemaUri(CATEGORY_SCHEME_URI)
                        .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/output.json")
                );

        assertThat(relatedObjectFactory.create(relatedObject), is(expected));
    }

    @Test
    @DisplayName("Sets internal category")
    void setsInternalCategory() {
        final var id = "_id";
        final var schemaUri = "scheme-uri";
        final var category = "internal";
        final var type = "_type";
        final var typeSchemeUri = "type-scheme-uri";

        final var relatedObject = new RelatedObjectBlock()
                .relatedObject(id)
                .relatedObjectSchemeUri(schemaUri)
                .relatedObjectCategory(category)
                .relatedObjectType(type)
                .relatedObjectTypeSchemeUri(typeSchemeUri);

        final var expected = new RelatedObject()
                .id(id)
                .schemaUri(schemaUri)
                .type(new RelatedObjectType()
                        .id(type)
                        .schemaUri(TYPE_SCHEME_URI))
                .category(new RelatedObjectCategory()
                        .schemaUri(CATEGORY_SCHEME_URI)
                        .id("https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/internal.json")
                );

        assertThat(relatedObjectFactory.create(relatedObject), is(expected));
    }
}