package au.org.raid.api.exception;

public class RelatedRaidTypeSchemaNotFoundException extends RuntimeException {
    public RelatedRaidTypeSchemaNotFoundException(final Integer id) {
        super("Related raid type schema not found with id %d".formatted(id));
    }
    public RelatedRaidTypeSchemaNotFoundException(final String uri) {
        super("Related raid type schema not found %s".formatted(uri));
    }
}
