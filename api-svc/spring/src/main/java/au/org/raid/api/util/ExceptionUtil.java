package au.org.raid.api.util;


import au.org.raid.api.spring.security.ApiSvcAuthenticationException;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;


/**
 Creation methods use String format (%s).
 <p/>
 Could also consider adding lambda support for the formatting and doing our
 own log level check.
 <p/>
 Don't create "load and warn" type methods, the app server will log the
 message of any exception thrown, lets' not log them repeatedly.
 */
public class ExceptionUtil {

  /**
   Creates a new RuntimeException.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static RuntimeException runtimeException(
    String format,
    Object... args
  ) {
    return new RuntimeException(String.format(format, args));
  }
  
  public static RuntimeException re(
    String format,
    Object... args
  ) {
    return runtimeException(format, args);
  }

  /**
   Creates a new IllegalArgumentException.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static IllegalArgumentException illegalArgException(
    String format,
    Object... args
  ) {
    return new IllegalArgumentException(String.format(format, args));
  }

  /**
   Creates a new IllegalStateException.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static IllegalStateException illegalStateException(
    String format,
    Object... args
  ) {
    return new IllegalStateException(String.format(format, args));
  }

  /**
   Creates a new IllegalArgumentException.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static IllegalArgumentException iae(
    String format,
    Object... args
  ) {
    return illegalArgException(format, args);
  }
  
  public static IllegalStateException ise(
    String format,
    Object... args
  ) {
    return illegalStateException(format, args);
  }
  
  /** Not yet implemented */
  public static RuntimeException notYetImplemented() {
    return runtimeException("not yet implemented");
  }
  
  public static ApiSvcAuthenticationException authFailed(){
    return new ApiSvcAuthenticationException();
  }
  

  /**
   Wraps the given Throwable in a new RuntimeException

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static RuntimeException wrapException(
    Throwable t,
    String format,
    Object... args
  ) {
    return new RuntimeException(String.format(format, args), t);
  }

  public static RuntimeException wrapIoException(
    IOException cause,
    String format,
    Object... args
  ) {
    return new UncheckedIOException(String.format(format, args), cause);
  }

  /**
   Logs an error with the given mesage and then a separate statement logs the
   exception itself.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static void logError(
    Log log,
    Throwable t,
    String format,
    Object... args
  ) {
    log.error(format, args);
    log.errorEx("exception", t);
  }

  /**
   Logs a warning with the given mesage and then a separate statement logs the
   exception itself.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static void logWarn(
    Log log,
    Throwable t,
    String format,
    Object... args) {
    log.warn(format, args);
    log.warnEx("exception", t);
  }

  public static Throwable getRootCause(final Throwable throwable) {
    final List<Throwable> list = getThrowableList(throwable);
    return list.size() < 2 ? null : list.get(list.size() - 1);
  }

  public static String getRootCauseMessage(@Nullable Throwable t) {
    if( t == null ){
      return "No problem";
    }
    Throwable rootCause = getRootCause(t);
    if( rootCause == null ){
      rootCause = t;
    }
    if( StringUtil.isBlank(rootCause.getMessage()) ){
      return rootCause.getClass().getSimpleName();
    }
    return rootCause.getMessage();
  }

  public static List<Throwable> getThrowableList(Throwable throwable) {
    final List<Throwable> list = new ArrayList<>();
    while( throwable != null && !list.contains(throwable) ){
      list.add(throwable);
      throwable = ExceptionUtil.getCause(throwable);
    }
    return list;
  }

  public static Throwable getCause(final Throwable throwable) {
    if( throwable == null ){
      return null;
    }

    final Throwable cause = throwable.getCause();
    if( cause != null ){
      return cause;
    }

    return null;
  }
  
}
