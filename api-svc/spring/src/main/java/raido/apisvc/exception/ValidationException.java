package raido.apisvc.exception;

import raido.idl.raidv2.model.ValidationFailure;

import java.util.Collection;
import java.util.List;

import static java.util.List.copyOf;
public class ValidationException extends RuntimeException {
  private final List<ValidationFailure> failures;

  public ValidationException(Collection<ValidationFailure> failures) {
    this.failures = copyOf(failures);
  }

  public List<ValidationFailure> getFailures() {
    return failures;
  }
}
