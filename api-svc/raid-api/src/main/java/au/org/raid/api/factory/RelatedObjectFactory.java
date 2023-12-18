package au.org.raid.api.factory;

import au.org.raid.idl.raidv2.model.RelatedObject;
import au.org.raid.idl.raidv2.model.RelatedObjectBlock;
import au.org.raid.idl.raidv2.model.RelatedObjectCategory;
import au.org.raid.idl.raidv2.model.RelatedObjectType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RelatedObjectFactory {
    private static final String CATEGORY_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/category/v1/";
    private static final String TYPE_SCHEMA_URI =
            "https://github.com/au-research/raid-metadata/tree/main/scheme/related-object/type/v1/";

    private static final Map<String, String> CATEGORY_MAP = Map.of(
            "input", "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/input.json",
            "output", "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/output.json",
            "internal", "https://github.com/au-research/raid-metadata/blob/main/scheme/related-object/category/v1/internal.json"
    );

    public RelatedObject create(final RelatedObjectBlock relatedObjectBlock) {
        if (relatedObjectBlock == null) {
            return null;
        }

        return new RelatedObject()
                .id(relatedObjectBlock.getRelatedObject())
                .schemaUri(relatedObjectBlock.getRelatedObjectSchemeUri())
                .category(List.of(new RelatedObjectCategory()
                        .schemaUri(CATEGORY_SCHEMA_URI)
                        .id(relatedObjectBlock.getRelatedObjectCategory() != null ?
                                CATEGORY_MAP.get(relatedObjectBlock.getRelatedObjectCategory().toLowerCase()) : null))
                )
                .type(new RelatedObjectType()
                        .schemaUri(TYPE_SCHEMA_URI)
                        .id(relatedObjectBlock.getRelatedObjectType())
                );
    }

    public RelatedObject create(final String id, final String schemaUri, final RelatedObjectType type, final List<RelatedObjectCategory> categories) {
        return new RelatedObject()
                .id(id)
                .schemaUri(schemaUri)
                .type(type)
                .category(categories);
    }

}