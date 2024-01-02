package au.org.raid.api.exception;

public class OrganisationSchemaNotFoundException extends RuntimeException {
    public OrganisationSchemaNotFoundException(final String schemaUri) {
        super("Contributor role schema not found %s".formatted(schemaUri));
    }

    public OrganisationSchemaNotFoundException(final Integer schemaId) {
        super("Organisation schema not found with id %d".formatted(schemaId));
    }
}
