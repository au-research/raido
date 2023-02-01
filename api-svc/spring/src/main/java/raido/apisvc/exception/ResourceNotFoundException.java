package raido.apisvc.exception;

public class ResourceNotFoundException extends RuntimeException {
  public ResourceNotFoundException(final String handle) {
    super(String.format("No RAiD found with handle %s", handle));
  }
}
