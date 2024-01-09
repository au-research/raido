package au.org.raid.api.exception;

public class RelatedRaidTypeNotFoundException extends RuntimeException {
    public RelatedRaidTypeNotFoundException(final Integer id) {
        super("Related raid type not found with id %d".formatted(id));
    }
    public RelatedRaidTypeNotFoundException(final String id, final String schemaUri) {
        super("Related raid type %s not found in schema %s".formatted(id, schemaUri));
    }
}
