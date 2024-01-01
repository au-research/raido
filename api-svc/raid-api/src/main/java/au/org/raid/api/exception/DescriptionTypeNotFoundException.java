package au.org.raid.api.exception;

public class DescriptionTypeNotFoundException extends RuntimeException {
    public DescriptionTypeNotFoundException(final String id, final String schemaUri) {
        super("Description type %s not found in schema %s".formatted(id, schemaUri));
    }

    public DescriptionTypeNotFoundException(final Integer id) {
        super("Description type id not found: %d".formatted(id));
    }

}
