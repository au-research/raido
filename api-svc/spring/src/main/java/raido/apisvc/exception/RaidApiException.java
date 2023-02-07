package raido.apisvc.exception;

public abstract class RaidApiException extends RuntimeException {
  private static final String instanceUri = "https://raid.org.au";
  private static final String typeFormat = "https://raid.org.au/errors#%s";
  public RaidApiException() {
    super();
  }

  public String getType() {
    return String.format(typeFormat, this.getClass().getSimpleName());
  };

  public abstract String getTitle();

  public abstract int getStatus();

  public abstract String getDetail();

  public String getInstance() {
    return instanceUri;
  }
}
