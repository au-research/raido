package raido.apisvc.exception;

import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.List.copyOf;

public class ValidationException extends RaidApiException {
  private final List<ValidationFailure> failures;
  private static final String TITLE = "There were validation failures.";
  private static final int STATUS = 400;

  public ValidationException(Collection<ValidationFailure> failures) {
    super();
    this.failures = copyOf(failures);
  }

  public List<ValidationFailure> getFailures() {
    return failures;
  }

  public String getTitle() {
    return TITLE;
  }

  public int getStatus() {
    return STATUS;
  }

  public String getDetail() {
    return String.format(
      "Request had %d validation failure(s). See failures for more details...",
      failures.size());
  }

  /**
   This was added so I could use this kind of exception in gatling, it's not
   a understandable way to present failures to real users - you should use a 
   proper UI for that.
   */
  @Override
  public String getMessage() {
    return getFailures().stream().
      map(i->"%s - %s".formatted(i.getFieldId(), i.getMessage())).
      collect(Collectors.joining(","));
  }

}
