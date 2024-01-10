package au.org.raid.api.exception;

public class SubjectTypeSchemaNotFoundException extends RuntimeException {
    public SubjectTypeSchemaNotFoundException(final Integer id) {
        super("Subject type schema not found with id %d".formatted(id));
    }
}
