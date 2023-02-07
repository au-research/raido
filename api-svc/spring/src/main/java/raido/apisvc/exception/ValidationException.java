package raido.apisvc.exception;

import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collection;
import java.util.List;

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
    return String.format("Request had %d validation failure(s). See failures for more details...", failures.size());
  }
}
