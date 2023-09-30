package au.org.raid.api.util;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.TimeZone;

@Slf4j
public class JvmUtil {
    /**
     * Use UTC, UTF-8 and Locale.ROOT as the defined defaults.
     * This avoids issues with different platforms behaving differently depending
     * on operating system, hardware, user defaults, etc.
     * All TimeZone, formatting and localisation behaviours belong in client code,
     * where sensible context-specific decisions can be made.
     * Note that this method is just a backup, you really want to be setting tese
     * values from the command line, because doing this in code means there may be
     * things in the JDK or libraries that are already with whatever the JVM
     * defaults were before this method was called (primary example being JDK JDBC
     * driver infrastructure).
     */
    public static void normaliseJvmDefaults() {
        setDefaultUtcTimezone();
        setDefaultRootLocale();
    /* I think there's no way to force this other than via startup options (it 
    gets cached during JVM startup and that's all she wrote). */
        if (!Charset.defaultCharset().name().equals("UTF-8")) {
            throw new IllegalStateException("JVM must be configured to UTF-8 via" +
                    " `-Dfile.encoding=UTF-8`, was: " + Charset.defaultCharset().name());
        }
    }

    public static void setDefaultUtcTimezone() {
        String currentTimeZoneId = TimeZone.getDefault().getID();
        if (StringUtil.areDifferent(currentTimeZoneId, "UTC")) {
            log.warn("TimeZone.default forced to UTC from `{}` - JVM should be" +
                            " started with `-Duser.timezone=UTC`.",
                    currentTimeZoneId);
            TimeZone.setDefault(DateUtil.utcTimezone());
        }
    }

    public static void setDefaultRootLocale() {
        Locale locale = Locale.getDefault();
        if (!locale.equals(Locale.ROOT)) {
            log.warn("Locale forced to ROOT from `{}` - JVM should be started with" +
                            " `-Duser.language= -Duser.country= -Duser.variant=`",
                    locale);
            Locale.setDefault(Locale.ROOT);
        }
    }

    public static void logSysProps() {
        infoLogSysProp("java.vendor", "java.vm.vendor", "java.vm.name",
                "java.version", "java.runtime.version", "java.runtime.name");
        infoLogSysProp("user.dir", "user.name", "os.name",
                "user.timezone", "file.encoding",
                "user.language", "user.country", "user.variant");
    }


    private static void infoLogSysProp(String... names) {
        for (String iName : names) {
            log.info(formatSysProp(iName));
        }
    }

    private static String formatSysProp(String name) {
        return String.format("%s: %s", name, System.getProperty(name));
    }

    /**
     * This method intended to be called *before* Spring is configured, so
     * it can't use any of the bean instance fields.
     * Not sure this is the best place for it.@param log
     */
    public static void logBuildInfo() {
        // build-info.properties is created by the build process
        String buildInfoPath = "/META-INF/build-info.properties";
        InputStream buildInfo = JvmUtil.class.getResourceAsStream(buildInfoPath);

        if (buildInfo == null) {
            log.info("no resource stream returned for {}", buildInfoPath);
            return;
        }

        log.info("build-info:\n{}", IoUtil.linesAsString(buildInfo));
    }

    public static boolean isWindowsPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }
}
