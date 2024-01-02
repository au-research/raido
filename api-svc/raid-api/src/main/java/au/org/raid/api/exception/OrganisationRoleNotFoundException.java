package au.org.raid.api.exception;

public class OrganisationRoleNotFoundException extends RuntimeException {
    public OrganisationRoleNotFoundException(final String id, final String schemaUri) {
        super("Organisation role %s not found with schema %s".formatted(id, schemaUri));
    }

    public OrganisationRoleNotFoundException(final Integer id) {
        super("Organisation role not found with id %d".formatted(id));
    }
}
