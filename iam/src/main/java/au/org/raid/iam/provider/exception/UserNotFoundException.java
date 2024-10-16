package au.org.raid.iam.provider.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(final String userId) {
        super("User %s not found".formatted(userId));
    }
}
