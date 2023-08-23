package au.org.raid.api.exception;

public class CrossAccountAccessException extends RaidApiException {
  private static final String title = "You do not have permission to access this RAiD.";
  private static final int status = 403;

  final Long servicePointId;

  public CrossAccountAccessException(final Long servicePointId) {
    super();
    this.servicePointId = servicePointId;
  }

  public String getDetail() {
    return String.format("You don't have permission to access RAiDs with a service point of %d", this.servicePointId);
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public int getStatus() {
    return status;
  }
}
