package au.org.raid.api.util;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import static java.lang.Runtime.getRuntime;
import static org.springframework.util.unit.DataSize.ofBytes;

public class JvmUtil {
    private static Log log = Log.to(JvmUtil.class);

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
            log.warn("TimeZone.default forced to UTC from `%s` - JVM should be" +
                            " started with `-Duser.timezone=UTC`.",
                    currentTimeZoneId);
            TimeZone.setDefault(DateUtil.utcTimezone());
        }
    }

    public static void setDefaultRootLocale() {
        Locale locale = Locale.getDefault();
        if (!locale.equals(Locale.ROOT)) {
            log.warn("Locale forced to ROOT from `%s` - JVM should be started with" +
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

    /**
     * Memory and GC logging was implemented before Micrometer metrics were
     * implemented.  I like seeing them logged on startup as I understand the
     * meaning of the basic built-in stats and how they relate to the JVM - as
     * opposed to he Micrometer metrics, which I find difficult to interpret.
     */
    public static void logMemoryInfo(String from) {
        if (!log.isInfoEnabled()) {
            return;
        }

        Runtime runtime = getRuntime();

        log.with("from", from).
                with("totalMemory", ofBytes(runtime.totalMemory()).toMegabytes()).
                with("maxMemory", ofBytes(runtime.maxMemory()).toMegabytes()).
                with("freeMemory", ofBytes(runtime.freeMemory()).toMegabytes()).
                info("memory stats (MB)");

        logGcInfo();
    }

    public static void logGcInfo() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        listGcMxBeans(mBeanServer).forEach(iName -> {
            GarbageCollectorMXBean iGcBean = getGcMxBean(mBeanServer, iName);
            log.with("name", iName).
                    with("collectionCount", iGcBean.getCollectionCount()).
                    with("collectionTime", iGcBean.getCollectionTime()).
                    info("GC info");
        });
    }

    private static Set<ObjectName> listGcMxBeans(MBeanServer mBeanServer) {
        try {
            return mBeanServer.queryNames(new ObjectName(
                            ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",*"),
                    null);
        } catch (MalformedObjectNameException e) {
            throw new RuntimeException(e);
        }
    }

    private static GarbageCollectorMXBean getGcMxBean(
            MBeanServer mBeanServer,
            ObjectName gcBeanName
    ) {
        GarbageCollectorMXBean gcBean;
        try {
            gcBean = ManagementFactory.newPlatformMXBeanProxy(
                    mBeanServer,
                    gcBeanName.getCanonicalName(),
                    GarbageCollectorMXBean.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return gcBean;
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
            log.info("no resource stream returned for %s", buildInfoPath);
            return;
        }

        log.info("build-info:\n%s", IoUtil.linesAsString(buildInfo));
    }

    public static boolean isWindowsPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }
}
