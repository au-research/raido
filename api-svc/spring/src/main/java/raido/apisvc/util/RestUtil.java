package raido.apisvc.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static raido.apisvc.util.ObjectUtil.*;
import static raido.apisvc.util.StringUtil.equalsIgnoreCase;

public class RestUtil {

  public static <T> HttpEntity<T> createEntityWithBearer(String authnToken) {
    Guard.notNull(authnToken);
    var headers = new HttpHeaders();
    headers.setBearerAuth(authnToken);
    return new HttpEntity<T>(headers);
  }

  public static <T> HttpEntity<T> createEntityWithBearer(
    String authnToken, T body
  ) {
    Guard.notNull(authnToken);
    var headers = new HttpHeaders();
    headers.setBearerAuth(authnToken);
    return new HttpEntity<T>(body, headers);
  }

  /** Does log headers, but redacts the Authorization header. */
  public static void debugLogEntity(
    Log log,
    String description,
    HttpEntity<?> entity
  ) {
    if( !log.isDebugEnabled() ){
      return;
    }

    var logHeaders = new HttpHeaders();
    entity.getHeaders().entrySet().stream().
      filter(i->!equalsIgnoreCase(i.getKey(), AUTHORIZATION)).
      forEach(i->logHeaders.addAll(i.getKey(), i.getValue()));
    logHeaders.set(AUTHORIZATION, "redacted");

    log.with("body", entity.getBody()).
      with("headers", logHeaders).
      debug(description + " request");
  }

  /** Does log headers, does noT redact any values. */
  public static void debugLogResponse(
    Log log,
    String description,
    ResponseEntity<?> response
  ) {
    if( !log.isDebugEnabled() ){
      return;
    }

    log.with("status", response.getStatusCode()).
      with("headers", response.getHeaders()).
      with("body", response.getBody()).
      debug(description + " response");
  }

  /** Debug log data and info log exec time. */
  public static <
    TResult,
    TEntity extends HttpEntity<?>,
    TResultResponse extends ResponseEntity<TResult>
  > TResult logExchange(
    Log httpLog,
    String description,
    TEntity entity,
    Function<TEntity, TResultResponse> fun
  ) {
    RestUtil.debugLogEntity(httpLog, description, entity);

    ResponseEntity<TResult> response =
      infoLogExecutionTime(httpLog, description,
        ()-> fun.apply(entity)
      );

    RestUtil.debugLogResponse(httpLog, description, response);

    return response.getBody();
  }

}
