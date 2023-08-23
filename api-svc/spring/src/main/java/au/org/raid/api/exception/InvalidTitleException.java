package au.org.raid.api.exception;

public class InvalidTitleException extends RaidApiException {
    private static final String TITLE = "RAiD title is invalid";
    private static final int STATUS = 400;

    private final String detail;

    public InvalidTitleException(final String detail) {
        super();
        this.detail = detail;
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public int getStatus() {
        return STATUS;
    }

    @Override
    public String getDetail() {
        return this.detail;
    }
}
