package au.org.raid.api.exception;

public class OrganisationRoleSchemaNotFoundException extends RuntimeException {
    public OrganisationRoleSchemaNotFoundException(final String schemaUri) {
        super("Organisation role schema not found %s".formatted(schemaUri));
    }

    public OrganisationRoleSchemaNotFoundException(final Integer schemaId) {
        super("Organisation role schema not found with id %d".formatted(schemaId));
    }
}
