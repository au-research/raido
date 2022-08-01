package raido.util;


import java.util.ArrayList;
import java.util.List;

import static raido.util.StringUtil.isBlank;


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
  public static RuntimeException createRuntimeException(
    String format,
    Object... args
  ) {
    return new RuntimeException(String.format(format, args));
  }

  /**
   Creates a new IllegalArgumentException.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static IllegalArgumentException createIllegalArgException(
    String format,
    Object... args
  ) {
    return new IllegalArgumentException(String.format(format, args));
  }

  /**
   Creates a new IllegalArgumentException.

   @param format {@link String#format(String, Object...)} - ("%s")
   */
  public static IllegalArgumentException createIae(
    String format,
    Object... args
  ) {
    return createIllegalArgException(format, args);
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
    if( isBlank(rootCause.getMessage()) ){
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
