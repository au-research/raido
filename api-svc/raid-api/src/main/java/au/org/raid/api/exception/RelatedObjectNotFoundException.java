package au.org.raid.api.exception;

public class RelatedObjectNotFoundException extends RuntimeException {
    public RelatedObjectNotFoundException(final Integer id) {
        super("Related object not found with id %d".formatted(id));
    }
}
