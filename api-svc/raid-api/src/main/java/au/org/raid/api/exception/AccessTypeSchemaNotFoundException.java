package au.org.raid.api.exception;

public class AccessTypeSchemaNotFoundException extends RuntimeException {
    public AccessTypeSchemaNotFoundException(final String schemaUri) {
        super("Access type schema not found %s".formatted(schemaUri));
    }
    public AccessTypeSchemaNotFoundException(final Integer schemaId) {
        super("Access type schema not found with id %d".formatted(schemaId));
    }
}
