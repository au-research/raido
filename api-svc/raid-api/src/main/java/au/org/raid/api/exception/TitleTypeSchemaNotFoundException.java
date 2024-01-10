package au.org.raid.api.exception;

public class TitleTypeSchemaNotFoundException extends RuntimeException {
    public TitleTypeSchemaNotFoundException(final Integer id) {
        super("Title type schema not found with id %d".formatted(id));
    }
    public TitleTypeSchemaNotFoundException(final String uri) {
        super("Title type schema not found %s".formatted(uri));
    }
}
