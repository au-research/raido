package au.org.raid.api.exception;

public class InvalidAccessException extends RaidApiException {
  private static final String TITLE = "Forbidden";
  private static final int STATUS = 403;

  private final String detail;

  public InvalidAccessException(final String detail) {
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
    return detail;
  }


}
