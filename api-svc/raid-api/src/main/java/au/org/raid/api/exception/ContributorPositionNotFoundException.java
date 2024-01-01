package au.org.raid.api.exception;

public class ContributorPositionNotFoundException extends RuntimeException {
    public ContributorPositionNotFoundException(final Integer contributorPositionId) {
        super("Contributor position not found with id %d".formatted(contributorPositionId));
    }

    public ContributorPositionNotFoundException(final String id, final String schemaUri) {
        super("Position not found %s with schema %s".formatted(id, schemaUri));
    }
}
