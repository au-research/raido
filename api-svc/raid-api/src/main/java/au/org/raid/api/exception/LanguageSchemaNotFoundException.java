package au.org.raid.api.exception;

public class LanguageSchemaNotFoundException extends RuntimeException {
    public LanguageSchemaNotFoundException(final String schemaUri) {
        super("Language schema not found %s".formatted(schemaUri));
    }
    public LanguageSchemaNotFoundException(final Integer id) {
        super("Language schema not found with id %d".formatted(id));
    }
}
