package au.org.raid.api.exception;

public class ContributorNotFoundException extends RuntimeException {
    public ContributorNotFoundException(final Integer contributorId) {
        super("Contributor not found with id %d".formatted(contributorId));
    }
}
