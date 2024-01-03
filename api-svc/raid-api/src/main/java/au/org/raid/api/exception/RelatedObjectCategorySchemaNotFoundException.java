package au.org.raid.api.exception;

public class RelatedObjectCategorySchemaNotFoundException extends RuntimeException {
    public RelatedObjectCategorySchemaNotFoundException(final String schemaUri) {
        super("Related object category schema not found %s".formatted(schemaUri));
    }
    public RelatedObjectCategorySchemaNotFoundException(final Integer id) {
        super("Related object category schema not found with id %d".formatted(id));
    }
}
