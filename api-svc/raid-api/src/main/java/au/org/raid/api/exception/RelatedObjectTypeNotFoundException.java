package au.org.raid.api.exception;

public class RelatedObjectTypeNotFoundException extends RuntimeException {
    public RelatedObjectTypeNotFoundException(final String id, final String schemaUri) {
        super("Related object type %s not found in schema %s".formatted(id, schemaUri));
    }
    public RelatedObjectTypeNotFoundException(final Integer id) {
        super("Related object schema not found with id %d".formatted(id));
    }
}
