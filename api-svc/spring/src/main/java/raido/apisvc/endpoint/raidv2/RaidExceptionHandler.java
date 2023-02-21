package raido.apisvc.endpoint.raidv2;

import org.jooq.exception.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import raido.apisvc.exception.CrossAccountAccessException;
import raido.apisvc.exception.InvalidJsonException;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.spring.RedactingExceptionResolver;
import raido.apisvc.spring.security.ApiSafeException;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.FailureResponse;
import raido.idl.raidv2.model.ValidationFailureResponse;

import static raido.apisvc.util.Log.to;

@ControllerAdvice
public class RaidExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Log log = to(RaidExceptionHandler.class);

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<ValidationFailureResponse> handleValidationException(final Exception e) {
    final var exception = (ValidationException) e;
    log.warnEx(exception.getTitle(), e);

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
  public ResponseEntity<FailureResponse> handleCrossAccountException(final Exception e) {
    final var exception = (CrossAccountAccessException) e;
    log.warnEx(exception.getTitle(), e);

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
  public ResponseEntity<FailureResponse> handleRaidApiException(final Exception e) {
    final var exception = (ResourceNotFoundException) e;
    log.warnEx(exception.getTitle(), e);

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

  @ExceptionHandler(InvalidJsonException.class)
  public ResponseEntity<FailureResponse> handleInvalidJsonException(final Exception e) {
    final var exception = (InvalidJsonException) e;
    log.warnEx(exception.getTitle(), e);

    final var body = new FailureResponse()
      .type(exception.getType())
      .title(exception.getTitle())
      .status(exception.getStatus())
      .detail(exception.getDetail())
      .instance(exception.getInstance());

    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .contentType(MediaType.APPLICATION_JSON)
      .body(body);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<Void> dataAccessExceptionHandler(final Exception e) {
    log.errorEx("Database exception", e);

    return ResponseEntity
      .internalServerError()
      .build();
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> defaultExceptionHandler(final Exception e, final WebRequest request) {
    try {
      final ResponseEntity<Object> response = super.handleException(e, request);
      if (response != null) {
        return response;
      }
    } catch (Exception ex) {
      log.errorEx("Exception thrown in exception handler", e);
    }
    log.errorEx("Unhandled exception", e);

    return ResponseEntity
      .internalServerError()
      .build();
  }

  /*
  Added this to fix LegacyRaidV1MintTest. Can probably be deleted once new error handling is supported by the app.
   */
  @ExceptionHandler(ApiSafeException.class)
  public ResponseEntity<RedactingExceptionResolver.ErrorJson> handleApiSafeException(Exception e) {

    final var errorJson = new RedactingExceptionResolver.ErrorJson();
    errorJson.detail = ((ApiSafeException) e).getDetail();
    errorJson.status = ((ApiSafeException) e).getHttpStatus();
    errorJson.message = e.getMessage();

    return ResponseEntity
      .badRequest()
      .body(errorJson);
  }
}
