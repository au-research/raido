package au.org.raid.api.exception;

public class AccessTypeNotFoundException extends RuntimeException {
    public AccessTypeNotFoundException(final String id, final String schemaUri) {
        super("Access type %s not found in schema %s".formatted(id, schemaUri));
    }
    public AccessTypeNotFoundException(final Integer id) {
        super("Access type not found with id %d".formatted(id));
    }
}
