package au.org.raid.api.exception;

public class ContributorRoleSchemaNotFoundException extends RuntimeException {
    public ContributorRoleSchemaNotFoundException(final Integer schemaId) {
        super("Contributor role schema not found with id %d".formatted(schemaId));
    }

    public ContributorRoleSchemaNotFoundException(final String uri) {
        super("Contributor role schema not found %s".formatted(uri));
    }
}
