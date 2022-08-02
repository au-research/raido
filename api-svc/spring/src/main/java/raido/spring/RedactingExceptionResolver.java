package raido.spring;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import raido.spring.security.ApiClientException;
import raido.spring.security.ApiSafeException;
import raido.util.DateUtil;
import raido.util.Log;
import raido.util.Nullable;

import java.time.LocalDateTime;

import static raido.util.Log.to;

/** In prod, this should be configured that the resolver will make
 sure we don't send any exception details to callers of the API (to avoid
 accidentall leakage of information). */
public class RedactingExceptionResolver extends AbstractHandlerExceptionResolver {
  private static final Log log = to(RedactingExceptionResolver.class);

  private final boolean redactErrorDetails;

  public RedactingExceptionResolver(
    boolean redactErrorDetails
  ) {
    this.redactErrorDetails = redactErrorDetails;
  }

  @SuppressWarnings("NullableProblems")
  @Override
  protected ModelAndView doResolveException(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler,
    Exception ex
  ) {
    ErrorJson errorJson = new ErrorJson();
    errorJson.timeStamp = DateUtil.formatIsoDateTime(LocalDateTime.now());
    
    try {

      errorJson.status = mapStatus(ex);
      
      errorJson.message = mapMessage(ex, redactErrorDetails);
      
      errorJson.detail = mapDetail(ex);

      logError(ex);
      
    } catch (Exception handlerException) {
      log.warn("Handling of [" + ex.getClass().getName() + 
          "] resulted in Exception", handlerException);
    }

    ModelAndView mav = new ModelAndView(
      new MappingJackson2JsonView(), "error", errorJson);
    mav.setStatus(HttpStatusCode.valueOf(errorJson.status));
    return mav;
  }

  private void logError(Exception ex) {
    if( ex == null ){
      // ends up generating a lot of spam in prod because of the internet
      // bad people scanning and probing and what not.
      log.debug("error resolve with no error");
      return;
    }
    
    if( ex instanceof BadCredentialsException 
//      || ex instanceof NotAuthorizedExcepton
    ){
      // don't log stacks for thse
      return;
    }
    
    if(ex instanceof NoHandlerFoundException noHandlerEx){
      // info() level is intentional, don't clutter the logs with warnings
      log.with("method", noHandlerEx.getHttpMethod()).
        with("url", noHandlerEx.getRequestURL()).info("no mapping found");
      return;
    }
    
    log.error("unhandled error", ex);
  }

  private String mapMessage(Exception ex, boolean redactErrorDetails) {
    if(ex instanceof ApiSafeException safe){
      // The entire point of an ApiSafeException is that it's message should 
      // be safe to send to the client.
      return safe.getMessage();
    }

    if(
      ex instanceof ServletException nested &&
        ((ServletException) ex).getRootCause() instanceof ApiSafeException
    ){
      return nested.getRootCause().getMessage();
    }

    if( redactErrorDetails ){
      return "internal error, see server logs for details";
    }
    
    // Logged as warn to avoid being accidentally filtered. 
    log.warn("DEVELOPMENT MODE - error details are exposed to clients");
    return ex.getCause() == null ? ex.getMessage() :
      ex.getCause().getMessage();
  }

  private int mapStatus(Exception ex) {
    if( ex instanceof ApiSafeException ){
      return ((ApiSafeException) ex).getStatus();
    }
    
    if( ex instanceof BadCredentialsException ){
      return HttpStatus.UNAUTHORIZED_401;
    }
    
//    if( ex instanceof NotAuthorizedExcepton ){
//      return HttpStatus.UNAUTHORIZED;
//    }

    if( ex instanceof NoHandlerFoundException ){
      return HttpStatus.NOT_FOUND_404;
    }
    
    return HttpStatus.INTERNAL_SERVER_ERROR_500;
  }

  @Nullable
  private Object mapDetail(Exception ex){
    if( ex instanceof ApiClientException ){
      return ((ApiClientException) ex).getProblems();
    }
    
    return null;
  }
  
  public static class ErrorJson {
    public Integer status;
    public String message;
    public String timeStamp;
    public Object detail;
  }

}