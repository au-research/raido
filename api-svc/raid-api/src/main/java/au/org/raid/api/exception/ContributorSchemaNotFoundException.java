package au.org.raid.api.exception;

public class ContributorSchemaNotFoundException extends RuntimeException {
    public ContributorSchemaNotFoundException(final String schemaUri) {
        super("Contributor schema not found with URI %s".formatted(schemaUri));
    }

    public ContributorSchemaNotFoundException(final Integer id) {
        super("Contributor schema not found with id %d".formatted(id));
    }
}
