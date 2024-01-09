package au.org.raid.api.exception;

public class RelatedObjectTypeSchemaNotFoundException extends RuntimeException {
    public RelatedObjectTypeSchemaNotFoundException(final String schemaUri) {
        super("Related object schema not found %s".formatted(schemaUri));
    }
    public RelatedObjectTypeSchemaNotFoundException(final Integer id) {
        super("Related object type schema not found with id %d".formatted(id));
    }
}
