package au.org.raid.api.exception;

public class UnknownServicePointException extends RaidApiException {
  private static final String TITLE = "Unknown service point";
  private static final int STATUS = 400;
  private final long servicePointId;
  public UnknownServicePointException(final long servicePointId) {
    super();
    this.servicePointId = servicePointId;
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
    return String.format("Service point with id %d was not found", this.servicePointId);
  }
}
