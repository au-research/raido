package au.org.raid.api.exception;

public class ResourceNotFoundException extends RaidApiException {
    private static final String TITLE = "The resource was not found.";
    private static final int STATUS = 404;
    private final String handle;

    public ResourceNotFoundException(final String handle) {
        super();
        this.handle = handle;
    }

    @Override
    public String getDetail() {
        return String.format("No RAiD was found with handle %s.", handle);
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public int getStatus() {
        return STATUS;
    }
}
