package raido.apisvc.spring;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.HttpStatus;
import org.jooq.exception.NoDataFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import raido.apisvc.exception.ResourceNotFoundException;
import raido.apisvc.exception.ValidationException;
import raido.apisvc.service.raid.ValidationFailureException;
import raido.apisvc.spring.security.ApiSafeException;
import raido.apisvc.spring.security.IdProviderException;
import raido.apisvc.util.Log;
import raido.apisvc.util.Nullable;

import java.time.LocalDateTime;

import static raido.apisvc.util.DateUtil.formatIsoDateTime;
import static raido.apisvc.util.Log.to;

/** In prod, this should be configured that the resolver will make
 sure we don't send any exception details to callers of the API (to avoid
 accidentall leakage of information). 
 
 IMPROVE:  Exception hierarchy + resolver implementation is getting messy.
 There's no clear definition of how to use the various exceptions and what a 
 dev should expect to happen with regards to client-facing behaviour.
 When authn/authz implementation is finished, we need to re-visit the design
 of this and define a formal exception handling strategy in the coding standard. 
 Streamline the API and document so a dev can easily know what kind
 of exception to throw  and what will happen (what code returned, what 
 message/deatil returned if any, what message is logged at what level and if
 stack trace is logged).
 If we don't do this it will cause security issues, inconsistent logging and
 accidetnal info leakage.
 
 @see ValidationFailureException
 */
public class RedactingExceptionResolver implements HandlerExceptionResolver {
  private static final Log log = to(RedactingExceptionResolver.class);

  private final boolean redactErrorDetails;

  public RedactingExceptionResolver(
    boolean redactErrorDetails
  ) {
    this.redactErrorDetails = redactErrorDetails;
  }

  @Override
  public ModelAndView resolveException(
    HttpServletRequest request,
    HttpServletResponse response,
    Object handler,
    Exception ex
  ) {
    ErrorJson errorJson = new ErrorJson();
    errorJson.timeStamp = formatIsoDateTime(LocalDateTime.now());
    
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
      || ex instanceof IdProviderException
    ){
      // don't log stacks for these
      return;
    }
    
    if(ex instanceof NoHandlerFoundException noHandlerEx){
      // info() level is intentional, don't clutter the logs with warnings
      log.with("method", noHandlerEx.getHttpMethod()).
        with("url", noHandlerEx.getRequestURL()).info("no mapping found");
      return;
    }
    
    if( ex instanceof ApiSafeException apiSafeEx ){
      log.with("detail", apiSafeEx.getDetail()).
        error(apiSafeEx.getMessage());
      if( apiSafeEx.isLogStack() ){
        log.errorEx(apiSafeEx.getMessage(), ex);
      }
      
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

    if (ex instanceof ResourceNotFoundException) {
      return ex.getMessage();
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
      return ((ApiSafeException) ex).getHttpStatus();
    }


    if( ex instanceof ValidationException){
      return HttpStatus.BAD_REQUEST_400;
    }

    if( ex instanceof ResourceNotFoundException){
      return HttpStatus.NOT_FOUND_404;
    }

    if( ex instanceof BadCredentialsException ){
      return HttpStatus.UNAUTHORIZED_401;
    }
    
    if( ex instanceof IdProviderException ){
      return HttpStatus.BAD_REQUEST_400;
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
    if( ex instanceof ApiSafeException ){
      return ((ApiSafeException) ex).getDetail();
    }

    if( ex instanceof ValidationException) {
      return ((ValidationException) ex).getFailures();
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