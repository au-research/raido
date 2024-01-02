package au.org.raid.api.exception;

public class OrganisationNotFoundException extends RuntimeException {
    public OrganisationNotFoundException(final Integer id) {
        super("Organisation not found with id %d".formatted(id));
    }
}
