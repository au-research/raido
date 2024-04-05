package au.org.raid.api.exception;

public class ServicePointNotFoundException extends RuntimeException {
    public ServicePointNotFoundException(final String groupId) {
        super("No service point exists for group %s".formatted(groupId));
    }
}
