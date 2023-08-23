package au.org.raid.api.exception;

public class InvalidJsonException extends RaidApiException {
  private static final String TITLE = "Invalid JSON";
  private static final int STATUS = 400;

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
    return "There was a problem processing the RAiD";
  }
}
