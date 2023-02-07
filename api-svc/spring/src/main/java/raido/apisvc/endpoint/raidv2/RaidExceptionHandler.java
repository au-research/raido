package raido.apisvc.endpoint.raidv2;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import raido.apisvc.exception.CrossAccountAccessException;
import raido.apisvc.exception.RaidApiException;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.exception.ValidationException;
import raido.idl.raidv2.model.FailureResponse;
import raido.idl.raidv2.model.ValidationFailureResponse;

@ControllerAdvice
public class RaidExceptionHandler {

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ValidationFailureResponse> handleValidationException(HttpServletRequest request, Exception e) {
    final var exception = (ValidationException) e;

    final var body = new ValidationFailureResponse()
      .type(exception.getType())
      .title(exception.getTitle())
      .status(exception.getStatus())
      .detail(exception.getDetail())
      .instance(exception.getInstance())
      .failures(exception.getFailures());

    return ResponseEntity
      .badRequest()
      .contentType(MediaType.APPLICATION_JSON)
      .body(body);
  }

  @ExceptionHandler(CrossAccountAccessException.class)
  public ResponseEntity<FailureResponse> handleCrossAccountException(HttpServletRequest request, Exception e) {
    final var exception = (CrossAccountAccessException) e;

    final var body = new FailureResponse()
      .type(exception.getType())
      .title(exception.getTitle())
      .status(exception.getStatus())
      .detail(exception.getDetail())
      .instance(exception.getInstance());

    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .contentType(MediaType.APPLICATION_JSON)
      .body(body);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<FailureResponse> handleRaidApiException(HttpServletRequest request, Exception e) {
    final var exception = (ResourceNotFoundException) e;

    final var body = new FailureResponse()
      .type(exception.getType())
      .title(exception.getTitle())
      .status(exception.getStatus())
      .detail(exception.getDetail())
      .instance(exception.getInstance());

    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .contentType(MediaType.APPLICATION_JSON)
      .body(body);
  }

}
