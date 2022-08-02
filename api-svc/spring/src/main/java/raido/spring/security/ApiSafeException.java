package raido.spring.security;


import org.eclipse.jetty.http.HttpStatus;
import raido.util.ExceptionUtil;
import raido.util.Log;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static raido.util.ExceptionUtil.logError;
import static raido.util.Log.to;


/**
 * Signals an error that is specifically intended to be returned to 
 * the client that communicates useful (but safe) information for the user.
 * <p/>
 * This exception intentionally does not allow nested exceptions because they
 * might leak unsafe stuff.  That's what the "safe" part is meant to imply. In 
 * production, generic error details are filtered from being returned to the 
 * client (because you never know what unsafe info they might contain).
 * <p/>
 * Care must be taken when constructing an ApiSafeException that it is suitable
 * for consumption by a user and does not leak unsafe information.
 * "Unsafe" here means all sorts of things: sensitive personal information,
 * authentication information, code structure, infrastructure information, etc.
 */
public class ApiSafeException extends RuntimeException {
  private static Log log = to(ApiSafeException.class);

  protected int status = HttpStatus.INTERNAL_SERVER_ERROR_500;
  
  public ApiSafeException(String message){
    super(message);
  }

  protected ApiSafeException(String message, int status){
    super(message);
    this.status = status;
  }

  public static void mapLoggedRootCauseMessage(
    Exception e, 
    Pattern pattern, 
    Supplier<ApiSafeException> converter
  ){
    mapRootCause(
      e,
      (root)-> {
        boolean rootCauseMatches = pattern.matcher(root.getMessage()).matches();
        if( rootCauseMatches ){
           logError(log, e, "replaced with ApiSafeException");
        }
        return rootCauseMatches;
      }, 
      converter);
  }

  /**
   * The root cause of e will be passed to the predicate, if it matches, then 
   * the exception generaate by the supplier will be thrown.
   */
  public static void mapRootCause(
    Exception e, 
    Predicate<Throwable> match, 
    Supplier<ApiSafeException> converter
  ){
    if( e == null ){
      return;
    }
    
    Throwable root = ExceptionUtil.getRootCause(e);
    if( root == null || root.getMessage() == null ){
      return;
    }
    
    if( match.test(root) ){
      throw converter.get();
    }
  }

  /**
   Creates a new {@link ApiSafeException}.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static ApiSafeException format(String format, Object... args) {
    return new ApiSafeException(String.format(format, args));
  }

  public int getStatus() {
    return status;
  }
}
