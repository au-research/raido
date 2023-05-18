package raido.apisvc.exception;

public class InvalidVersionException extends RaidApiException {
  public InvalidVersionException(final Integer version) {
    super();

  }

  @Override
  public String getTitle() {
    return null;
  }

  @Override
  public int getStatus() {
    return 400;
  }

  @Override
  public String getDetail() {
    return null;
  }
}
