package au.org.raid.api.exception;

public class TitleTypeNotFoundException extends RuntimeException {
    public TitleTypeNotFoundException(final Integer id) {
        super("Title type not found with id %d".formatted(id));
    }
    public TitleTypeNotFoundException(final String id, final String schemaUri) {
        super("Title type %s not found in schema %s".formatted(id, schemaUri));
    }
}
