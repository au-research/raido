package au.org.raid.api.util;


import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Creation methods use String format (%s).
 * <p/>
 * Could also consider adding lambda support for the formatting and doing our
 * own log level check.
 * <p/>
 * Don't create "load and warn" type methods, the app server will log the
 * message of any exception thrown, lets' not log them repeatedly.
 */
public class ExceptionUtil {
    /**
     * Creates a new IllegalArgumentException.
     *
     * @param format {@link String#format(String, Object...)} - ("%s")
     */
    public static IllegalArgumentException illegalArgException(
            String format,
            Object... args
    ) {
        return new IllegalArgumentException(String.format(format, args));
    }

    /**
     * Wraps the given Throwable in a new RuntimeException
     *
     * @param format {@link String#format(String, Object...)} - ("%s")
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
     * Logs an error with the given mesage and then a separate statement logs the
     * exception itself.
     *
     * @param format {@link String#format(String, Object...)} - ("%s")
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

    public static Throwable getRootCause(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.size() < 2 ? null : list.get(list.size() - 1);
    }

    public static List<Throwable> getThrowableList(Throwable throwable) {
        final List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = ExceptionUtil.getCause(throwable);
        }
        return list;
    }

    public static Throwable getCause(final Throwable throwable) {
        if (throwable == null) {
            return null;
        }

        final Throwable cause = throwable.getCause();
        return cause;
    }

}
