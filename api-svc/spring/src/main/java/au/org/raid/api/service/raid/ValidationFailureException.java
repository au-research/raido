package au.org.raid.api.service.raid;

import au.org.raid.api.spring.RedactingExceptionResolver;
import au.org.raid.idl.raidv2.model.ValidationFailure;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.List.copyOf;

/**
 Intentionally a checked exception, when one of these can be throw the 
 containing code is supposed to catch it and deal with it (fitting it into
 whatever the return shape of the endpoint is supposed to be).
 
 Alternate design:
 - change this to runtime exception
 - define a global http endpoint contract (status code, result structure) 
   for validation failures 
 - alter exception resolver to transform runtime ex to the structure
 @see RedactingExceptionResolver
 */
public class ValidationFailureException extends Exception {
  private List<ValidationFailure> failures;

  public ValidationFailureException(Collection<ValidationFailure> failures) {
    this.failures = copyOf(failures);
  }

  public ValidationFailureException(ValidationFailure... failures) {
    this.failures = copyOf(Arrays.stream(failures).toList());
  }

  public List<ValidationFailure> getFailures() {
    return failures;
  }
}
