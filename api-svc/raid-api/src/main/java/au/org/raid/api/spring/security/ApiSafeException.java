package au.org.raid.api.spring.security;


import au.org.raid.api.util.ExceptionUtil;
import au.org.raid.api.util.Log;
import lombok.Getter;

import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import static au.org.raid.api.util.ExceptionUtil.logError;
import static au.org.raid.api.util.Log.to;
import static java.util.Collections.emptyList;


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
@Getter
public class ApiSafeException extends RuntimeException {
    private static final Log log = to(ApiSafeException.class);

    private final int httpStatus;

    private final boolean logStack;

    /**
     * Remember  this is explicitly intended to be returned to the client,
     * even exceptions are being redacted.
     * Do not put sensitivie info in here.
     * It's intended for validation errors, etc..
     */
    private final List<String> detail;

    public ApiSafeException(String message) {
        this(message, 500, emptyList(), false);
    }

    public ApiSafeException(String message, List<String> detail) {
        this(message, 500, detail, false);
    }

    public ApiSafeException(String message, int httpStatus) {
        this(message, httpStatus, emptyList(), false);
    }

    public ApiSafeException(
            String message, int httpStatus, List<String> detail
    ) {
        this(message, httpStatus, detail, false);
    }

    protected ApiSafeException(
            String message, int httpStatus, List<String> detail, boolean logStack
    ) {
        super(message);
        this.httpStatus = httpStatus;
        this.detail = detail;
        this.logStack = logStack;
    }

    public static ApiSafeException apiSafe(
            String message, int httpStatus, List<String> detail
    ) {
        return new ApiSafeException(message, httpStatus, detail);
    }

    public static ApiSafeException apiSafe(
            String message, List<String> detail
    ) {
        return new ApiSafeException(message, detail);
    }

    public static ApiSafeException apiSafe(
            String message, int httpStatus
    ) {
        return new ApiSafeException(message, httpStatus);
    }

    public static void mapLoggedRootCauseMessage(
            Exception e,
            Pattern pattern,
            Supplier<ApiSafeException> converter
    ) {
        mapRootCause(
                e,
                (root) -> {
                    boolean rootCauseMatches = pattern.matcher(root.getMessage()).matches();
                    if (rootCauseMatches) {
                        logError(log, e, "replaced with ApiSafeException");
                    }
                    return rootCauseMatches;
                },
                converter);
    }

    /**
     * The root cause of e will be passed to the predicate, if it matches, then
     * the exception generate by the supplier will be thrown.
     */
    public static void mapRootCause(
            Exception e,
            Predicate<Throwable> match,
            Supplier<ApiSafeException> converter
    ) {
        if (e == null) {
            return;
        }

        Throwable root = ExceptionUtil.getRootCause(e);
        if (root == null || root.getMessage() == null) {
            return;
        }

        if (match.test(root)) {
            throw converter.get();
        }
    }

    /**
     * Creates a new {@link ApiSafeException}.
     *
     * @param format {@link String#format(String, Object...)} - ("%s")
     */
    public static ApiSafeException format(String format, Object... args) {
        return new ApiSafeException(String.format(format, args));
    }

}
