package au.org.raid.api.util;


import java.util.function.Supplier;


/**
 * Util functions to evaluate "guard clauses"; if the condition is true method
 * will return, if false method will throw a runtime exception.
 * Used to be called "check" but that word is now used for methods that return
 * boolean or a list of problems (consistent with Spring validations, etc.)
 * For complicated argument lists, "messages" come first, then other args go
 * further to the right - for consistency with methods that check multiple args
 * with a varargs param.
 */
public class Guard {
    public static void hasValue(@Nullable String v) {
        if (!StringUtil.hasValue(v)) {
            throw new IllegalArgumentException(
                    "check failed, no value present");
        }
    }


    public static void notNull(String message, @Nullable Object value) {
        if (value == null) {
            throw failedCheck("null value: %s", message);
        }
    }

    public static void notNull(Supplier<String> message, @Nullable Object value) {
        if (value == null) {
            throw failedCheck("null value: %s", message.get());
        }
    }

    public static void notNull(Object value) {
        if (value == null) {
            throw failedCheck("null value was given");
        }
    }


    /**
     * @param message {@link String#format(String, Object...)} format
     */
    public static IllegalArgumentException failedCheck(
            String message,
            Object... args
    ) {
        if (message == null) {
            if (args != null && args.length != 0) {
                return new IllegalArgumentException(StringUtil.convertToString(args));
            } else {
                return new IllegalArgumentException("");
            }
        }
        if (args != null && args.length == 0) {
            return new IllegalArgumentException(message);
        }
        return new IllegalArgumentException(String.format(message, args));
    }

    private static IllegalArgumentException failedCheck(String message) {
        return new IllegalArgumentException(message);
    }
}
