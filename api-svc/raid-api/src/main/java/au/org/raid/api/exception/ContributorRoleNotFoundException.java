package au.org.raid.api.exception;

public class ContributorRoleNotFoundException extends RuntimeException {
    public ContributorRoleNotFoundException(final Integer contributorRoleId) {
        super("Contributor role not found with id %d".formatted(contributorRoleId));
    }

    public ContributorRoleNotFoundException(final String id, final String schemaUri) {
        super("Role not found %s with schema %s".formatted(id, schemaUri));
    }
}
