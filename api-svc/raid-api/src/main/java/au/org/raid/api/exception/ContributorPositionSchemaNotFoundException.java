package au.org.raid.api.exception;

public class ContributorPositionSchemaNotFoundException extends RuntimeException {
    public ContributorPositionSchemaNotFoundException(final Integer schemaId) {
        super("Contributor position schema not found with id %d".formatted(schemaId));
    }

    public ContributorPositionSchemaNotFoundException(final String uri) {
        super("Contributor position schema not found %s".formatted(uri));
    }

}
