package au.org.raid.api.exception;

public class LanguageNotFoundException extends RuntimeException {
    public LanguageNotFoundException(final String code, final String schemaUri) {
        super("Language %s not found in schema %s".formatted(code, schemaUri));
    }

    public LanguageNotFoundException(final Integer id) {
        super("Language not found with id %d".formatted(id));
    }
}
