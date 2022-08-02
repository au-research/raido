package raido.spring.security;

import org.eclipse.jetty.http.HttpStatus;

import java.util.List;

public class ApiClientException extends ApiSafeException {

  private final List<String> problems;

  public ApiClientException(String message, List<String> problems) {
    super(message, HttpStatus.BAD_REQUEST_400);
    this.problems = problems;
  }

  public List<String> getProblems() {
    return problems;
  }
}
