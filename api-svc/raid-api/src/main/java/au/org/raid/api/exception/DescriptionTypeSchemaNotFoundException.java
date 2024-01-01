package au.org.raid.api.exception;

public class DescriptionTypeSchemaNotFoundException extends RuntimeException {
    public DescriptionTypeSchemaNotFoundException(final String schemaUri) {
        super("Description type schema not found %s".formatted(schemaUri));
    }

    public DescriptionTypeSchemaNotFoundException(final Integer id) {
        super("Description type schema not found with id %d".formatted(id));
    }

}
