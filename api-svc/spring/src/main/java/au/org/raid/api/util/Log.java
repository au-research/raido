package au.org.raid.api.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Wraps slf4j Logger.
 * <p/>
 * <ul>Benefits: <li>
 * Shorter declarations, with static imports as well:
 * "private static Log log = to(Clazz.class)" or
 * "private Log log = to(this)".
 * </li> <li>
 * Java17 support: use lambdas "()->x.toString()" instead of
 * "if( log.isDebugEnabled(){log.debug(x.toString)}"
 * </li> <li>
 * Consistent string formatting: Log API uses "%s" instead of "{}", eliminates
 * formatting errors caused by misrememberance or copy pasting between different
 * string formatting contexts.
 * </li> <li>
 * Structured logging, %s format specifiers can still be messed up, so now
 * the Log API has `with()` methods to enable structured logging.
 * </li> <li>
 * Eases upgrade burden when upgrading libraries, especially as structured logging
 * is not something that is stable across the ecosystem.
 * </li> </ul>
 * <ul>Future: <li>
 * AVOID: don't add anonymous `with(Object)` or with `with(Object...)` methods
 * that just lets you add unnamed objects to be printed out.  Force the dev
 * to provide a name, better for the app in the long run.
 * </li> <li>
 * IMPROVE: String.format() is fairly safe (and looked after by the JDK people
 * instead of random open-source folks - see Log4j2 logpocalypse).  But it's also
 * quite slow.
 * Tt'd be good to add some abuse prevention (whitelist scanning for weird
 * characters for injection attacks etc.) and/or combine with something that can
 * do string generation more quickly (only needs to support %s specifier).
 * I think a fail-fast and warn approach would be good - limit to 7-bit ASCII and
 * add exceptions as they are proven to be necessary.  Or maybe just use something
 * like the OWASP encoder?
 * </li> </ul>
 */
public class Log {

    private Logger log;

    private Log(Logger log) {
        this.log = log;
    }

    public static Log to(Class<?> category) {
        return toClass(category);
    }

    public static Log to(Class<?> category, String subcategory) {
        Guard.notNull("null catebory is not valid ", category);
        Guard.notNull("null subcategory is not valid", subcategory);

        return toString(category.getName() + "." + subcategory);
    }

    public static Log to(String category) {
        return toString(category);
    }

    public static Log toString(String category) {
        return new Log(LoggerFactory.getLogger(category));
    }

    public static Log toClass(Class<?> category) {
        return new Log(LoggerFactory.getLogger(category));
    }

    /**
     * Creates a category for the class of the given instance.
     */
    public static Log toInstance(Object category) {
        return new Log(LoggerFactory.getLogger(category.getClass()));
    }

    /**
     * This method should only be called from static initialiser blocks.
     * <p>
     * Can be useful in other bootstrap contexts too (like logging from a main()
     * or test setup method before logging is even configured).
     */
    public static void staticLog(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    public static String formatArgValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            return formatArgValueMap(map);
        }

        if (value instanceof Collection<?> collection) {
            return formatArgValueCollection(collection);
        }

        // will call toString()
        return StringUtil.nullToString(value);
    }

    public static String formatArgValueCollection(Collection<?> c) {
        return c.stream().
                map(Log::formatArgValue).
                // can the collector be shared across threads (i.e. use a static?)
                        collect(StringUtil.BRACKET_JOINER);
    }

    public static String formatArgValueMap(Map<?, ?> c) {
        return c.entrySet().stream().
                map(i -> formatArgValue(i.getKey()) + "=" + formatArgValue(i.getValue())).
                // can the collector be shared across threads (i.e. use a static?)
                        collect(StringUtil.BRACKET_JOINER);
    }

    public void debug(String msg) {
        log.debug(msg);
    }

    /**
     * isEnabled() methods are still desired, even though already builtin to
     * the log methods because I sometimes abuse them as a flag to tell me whether
     * to do something (blech - use a proper flag instead!)
     */
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    public void debug(Supplier<String> msg) {
        if (log.isDebugEnabled()) {
            log.debug(msg.get());
        }
    }

    /**
     * Uses {@link String#format} - %s
     */
    public void debug(String msg, Object... args) {
        if (!log.isDebugEnabled()) {
            return;
        }

        if (args.length == 1 && args[0] instanceof Throwable) {
            if (!msg.contains("%s")) {
                log.debug(msg, (Throwable) args[0]);
                return;
            }
        }

        log.debug(String.format(msg, args));
    }

    /**
     * Uses {@link String#format} - %s
     */
    public void debugEx(String msg, Throwable t, Object... args) {
        if (log.isDebugEnabled()) {
            log.debug(String.format(msg, args), t);
        }
    }

    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    public void info(String msg) {
    /* don't need "if is enabled" shortcut because  we're not doing any work
    here and the logging implementation already has a shortcut. */
        log.info(msg);
    }

    /**
     * Uses {@link String#format} - %s
     */
    public void info(String msg, Object... args) {
        if (!log.isInfoEnabled()) {
            return;
        }

        if (args.length == 1 && args[0] instanceof Throwable) {
            if (!msg.contains("%s")) {
                log.info(msg, (Throwable) args[0]);
                return;
            }
        }

        log.info(String.format(msg, args));
    }

    public void info(Supplier<String> msg) {
        if (log.isInfoEnabled()) {
            log.info(msg.get());
        }
    }

    public void warn(String msg) {
        log.warn(msg);
    }

    /**
     * Uses {@link String#format} - %s
     */
    public void warn(String msg, Object... args) {
        if (args.length == 1 && args[0] instanceof Throwable) {
            if (!msg.contains("%s")) {
                log.warn(msg, (Throwable) args[0]);
                return;
            }
        }

        log.warn(String.format(msg, args));
    }

    /**
     * Uses {@link String#format} - %s
     */
    public void warnEx(String msg, Throwable t, Object... args) {
        log.warn(String.format(msg, args), t);
    }

    public void error(String msg) {
        log.error(msg);
    }

    /**
     * Uses {@link String#format} - %s
     */
    public void error(String msg, Object... args) {
        if (args.length == 1 && args[0] instanceof Throwable) {
            if (!msg.contains("%s")) {
                log.error(msg, (Throwable) args[0]);
                return;
            }
        }

        log.error(String.format(msg, args));
    }

    /**
     * Uses {@link String#format} - %s
     */
    public void errorEx(String msg, Throwable t, Object... args) {
        log.error(String.format(msg, args), t);
    }

    /**
     * Uses {@link String#format} - %s
     */
    public RuntimeException runtimeException(String msg, Object... args) {
        RuntimeException re = new RuntimeException(String.format(msg, args));
        log.error(re.getMessage());
        return re;
    }

    public LogMessageBuilder msg(String msg, Object... args) {
        return new LogMessageBuilder(this, msg, args);
    }

    public LogMessageBuilder with(String name, Object value) {
        return new LogMessageBuilder(this, name, value);
    }

    /* world's dodgiest structured logging API - seriously, no thought went into
     this at all.
     At the moment, this is focused on printing the messages in a way that's
     easy for a human to read particularly in console messages during
     development.  Eventually want to swap to something more log parser friendly.
     */
    public static class LogMessageBuilder {

        /**
         * marker to indicate the end of the unstructured message and the beginning
         * of the structured parameters
         */
        public static final String MESSAGE_SEPARATOR = "-";
        /**
         * mark end of structured key and beginning of value
         */
        public static final String KEY_VALUE_SEPARATOR = "=";
        /**
         * mark end of structure param and beginning of next
         */
        public static final String PARAMETER_SEPARATOR = " ";

        private final Log log;
        private final Map<String, Object> structuredArgs;

        /* IMPROVE: standardise the `log.with().info("")` API, remove the old API
         and get rid of these fields. */
        private String msg;
        private Object[] messageArgs;

        /**
         * when using log.msg() to start, i.e.:
         * `log.msg("message %s", val).with("field", field).info();`
         */
        public LogMessageBuilder(Log log, String msg, Object... messageArgs) {
            this.log = log;
            this.msg = msg;
            this.messageArgs = messageArgs;
            // LinkedHashMap so that insert order defines print order
            this.structuredArgs = new LinkedHashMap<>();
        }

        /**
         * when using log.with() to start, i.e.:
         * `log.with("field", field).info("message %s", val);`
         */
        public LogMessageBuilder(Log log, String name, Object value) {
            this(log, "");
            with(name, value);
        }

        public LogMessageBuilder with(String name, Object value) {
            structuredArgs.put(name, value);
            return this;
        }

        /**
         * when using log.msg() to start, i.e.:
         * `log.msg("message %s", val).with("field", field).info();`
         */
        public void info() {
            if (!log.isInfoEnabled()) {
                return;
            }
            log.log.info(this.toString());
        }

        /**
         * when using log.with() to start, i.e.:
         * `log.with("field", field).info("message %s", val);`
         */
        public void info(String msg, Object... args) {
            this.msg = msg;
            this.messageArgs = args;
            if (!log.isInfoEnabled()) {
                return;
            }
            log.log.info(this.toString());
        }

        public void error(String msg, Object... args) {
            this.msg = msg;
            this.messageArgs = args;
            if (!log.isInfoEnabled()) {
                return;
            }
            log.log.error(this.toString());
        }

        public void warn(String msg, Object... args) {
            this.msg = msg;
            this.messageArgs = args;
            if (!log.isInfoEnabled()) {
                return;
            }
            log.log.warn(this.toString());
        }

        public void debug(String msg, Object... args) {
            this.msg = msg;
            this.messageArgs = args;
            if (!log.isInfoEnabled()) {
                return;
            }
            log.log.debug(this.toString());
        }

        public void debug() {
            if (!log.isDebugEnabled()) {
                return;
            }
            log.log.debug(this.toString());
        }

        public void warn() {
            if (!log.log.isWarnEnabled()) {
                return;
            }
            log.log.warn(this.toString());
        }

        /* This is useful at the moment as a flat string in the logs for viewing
        in the console output directly.  In a larger context, we'll probably want
        to output this as a json blob so it integrates with log aggregation
        infrastructure (Splunk, etc.) */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            if (msg != null && msg.length() != 0) {
                sb.append(String.format(msg, messageArgs)).append(" ");
            }
            sb.append(MESSAGE_SEPARATOR).append(" ");

            structuredArgs.forEach((key, value) ->
                    sb.append(key).
                            append(KEY_VALUE_SEPARATOR).
                            append(StringUtil.nullToString(value)).
                            append(PARAMETER_SEPARATOR));
            return sb.toString();
        }
    }
}
