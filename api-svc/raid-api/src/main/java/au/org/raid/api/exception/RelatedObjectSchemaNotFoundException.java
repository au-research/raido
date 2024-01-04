package au.org.raid.api.exception;

public class RelatedObjectSchemaNotFoundException extends RuntimeException {
    public RelatedObjectSchemaNotFoundException(final String schemaUri) {
        super("Related object schema not found %s".formatted(schemaUri));
    }
    public RelatedObjectSchemaNotFoundException(final Integer schemaId) {
        super("Related object schema not found with id %d".formatted(schemaId));
    }
}
