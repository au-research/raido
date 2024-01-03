package au.org.raid.api.exception;

public class RelatedObjectCategoryNotFoundException extends RuntimeException {
    public RelatedObjectCategoryNotFoundException(final String id, final String schemaUri) {
        super("Related object category %s not found in schema %s".formatted(id, schemaUri));
    }
    public RelatedObjectCategoryNotFoundException(final Integer id) {
        super("Related object category not found with id %d".formatted(id));
    }
}
