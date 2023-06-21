package raido.apisvc.exception;

public class InvalidVersionException extends RaidApiException {
  private final Integer version;

  public InvalidVersionException(final Integer version) {
    super();
    this.version = version;
  }

  @Override
  public String getTitle() {
    return "Invalid version";
  }

  @Override
  public int getStatus() {
    return 400;
  }

  @Override
  public String getDetail() {
    return String.format("Update failed with stale version: %d", version);
  }
}
