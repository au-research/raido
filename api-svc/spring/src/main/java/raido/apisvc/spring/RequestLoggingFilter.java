package raido.apisvc.spring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;
import raido.apisvc.util.Log;
import raido.apisvc.util.ObjectUtil;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.LOCATION;
import static raido.apisvc.endpoint.anonymous.PublicEndpoint.STATUS_PATH;
import static raido.apisvc.endpoint.auth.AppUserAuthnEndpoint.IDP_URL;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.sanitiseLocationUrl;
import static raido.apisvc.util.StringUtil.*;
import static raido.apisvc.util.StringUtil.areEqual;

/**
 IMPROVE: add the ability to enable by requestPath and/or principal (probably 
 want this to be at the "API" level at least, but maybe could even be useful
 to toggle for an individual user.
 IMPROVE: add header logging
 IMPROVE: add url param logging
 */
public class RequestLoggingFilter extends OncePerRequestFilter {

  
  /** if this is not enabled for info, then the filter will not be added to 
   the filter chain at all.
   @see #add(ServletContext, String) 
   */
  private static final Log log = to(RequestLoggingFilter.class);

  /** if this is set to debug, then the body of the request will be logged */
  private static final Log bodyLog = to(RequestLoggingFilter.class, "body");
  private static final Log bodyLog400 = to(RequestLoggingFilter.class, "body400");

  private static int getMaxPayloadLength = 1024;

  public static Optional<FilterRegistration.Dynamic> add(
    ServletContext ctx, 
    String dispatcherName
  ) {
    if( !log.isInfoEnabled() ){
      // do we want a warning or something here?
      return Optional.empty();
    }

    String filterName = RequestLoggingFilter.class.getSimpleName();
    FilterRegistration.Dynamic filterReg = ctx.addFilter(
      filterName, RequestLoggingFilter.class);
    filterReg.addMappingForServletNames(null, false, dispatcherName);
    log.with("filter", filterName).
      info("registered");
    return Optional.of(filterReg);
  }
  
  /** Adapted from AbstractRequestLoggingFilter. */
  @Override
  protected void doFilterInternal(
    HttpServletRequest request,
    HttpServletResponse response,
    FilterChain filterChain
  ) throws ServletException, IOException {

    boolean isFirstRequest = !isAsyncDispatch(request);
    HttpServletRequest requestToUse = request;

    if( isFirstRequest && !(request instanceof ContentCachingRequestWrapper) ){
      requestToUse = new ContentCachingRequestWrapper(
        request, getMaxPayloadLength);
    }

    long beforeReq = System.nanoTime();
    try {
      filterChain.doFilter(requestToUse, response);
    }
    finally {
      long time = (System.nanoTime() - beforeReq) / 1_000_000;
      
      boolean shouldLog = true;
      if( areEqual(request.getRequestURI(), STATUS_PATH) ){
        // because used by the AWS ASG health check, too log noise  
        shouldLog = false;
      }
      
      if( shouldLog ){
        logRequest(request, response, requestToUse, time);
      }
    }
  }

  private void logRequest(
    HttpServletRequest request,
    HttpServletResponse response,
    HttpServletRequest requestToUse,
    long time
  ) {
    /* No need for an info level check, if info is disabled the filter won't 
    have been added to the filter-chain in the first place. * */
    
    var logBuild = log.with("url", request.getRequestURI()).
      with("contentType", request.getContentType()).
      with("accept", request.getHeader(ACCEPT)).
      with("user", request.getRemoteUser()).
      with("timeMs", time).
      with("status", response.getStatus());

    String locationHeader = response.getHeader(LOCATION);
    if( locationHeader != null ){
      logBuild = logBuild.with("location", sanitiseLocationUrl(locationHeader));
    }

    // don't log params because they contain sensitive info
    if( !trimEqualsIgnoreCase(request.getRequestURI(), IDP_URL) ){
      logBuild = logBuild.with("params", request.getParameterMap());
    }
    
    logBuild.info("endpoint " + request.getMethod());

    
    if( bodyLog.isDebugEnabled() ){
      /* I wanted to put this before the filter invocation, but it has to 
      go after - the request underlying the CachingWrapper has to be read 
      before we can read the content here. */
      bodyLog.with("uri", request.getRequestURI()).
        with("requestPayload", getMessagePayload(requestToUse)).
        debug();
    }

    if( response.getStatus() == SC_BAD_REQUEST && bodyLog400.isInfoEnabled() ){
      bodyLog.with("uri", request.getRequestURI()).
        with("requestPayload", getMessagePayload(requestToUse)).
        info("400 bad request");
    }
    
  }

  record ServletRequestId(
    String method,
    String uri,
    String remoteAddr,
    String remoteUser
  ) {
    @Override
    public String toString() {
      return ObjectUtil.jsonToString(this);
    }
  }

  private ServletRequestId createRequestId(HttpServletRequest req) {
    return new ServletRequestId(
      req.getMethod(), req.getRequestURI(),
      req.getRemoteAddr(), req.getRemoteUser());
  }

  public static String getMessagePayload(HttpServletRequest request) {
    ContentCachingRequestWrapper wrapper =
      WebUtils.getNativeRequest(request, ContentCachingRequestWrapper.class);

    if( wrapper == null ){
      return "no wrapper";
    }

    byte[] buf = wrapper.getContentAsByteArray();
    if( buf.length <= 0 ){
      /* can happens if the request content doesn't actually get read by spring
       so the CachingWrapper hasn't got anything to return here. */
      return "0-length content";
    }

    int length = Math.min(buf.length, getMaxPayloadLength);
    try {
      return new String(buf, 0, length, wrapper.getCharacterEncoding());
    }
    catch( UnsupportedEncodingException ex ){
      return "encoding exception - " + ex.getMessage();
    }
  }

}

