package au.org.raid.api.exception;

public class RelatedObjectTypeSchemaNotFoundException extends RuntimeException {
    public RelatedObjectTypeSchemaNotFoundException(final String schemaUri) {
        super("Related object schema not found %s".formatted(schemaUri));
    }

}
