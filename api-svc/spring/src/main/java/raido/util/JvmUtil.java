package raido.util;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.TimeZone;

import static raido.util.Log.to;
import static raido.util.StringUtil.areDifferent;

public class JvmUtil {
  private static Log log = to(JvmUtil.class);

  /**
   Use UTC, UTF-8 and Locale.ROOT as the defined defaults.
   This avoids issues with different platforms behaving differently depending
   on operating system, hardware, user defaults, etc.
   All TimeZone, formatting and localisation behaviours belong in client code,
   where sensible context-specific decisions can be made.
   */
  public static void normaliseJvmDefaults() {
    setDefaultUtcTimezone();
    setDefaultRootLocale();
    /* I think there's no way to force this other than via startup options (it 
    gets cached during JVM startup and that's all she wrote). */
    if( !Charset.defaultCharset().name().equals("UTF-8") ){
      throw new IllegalStateException("JVM must be configured to UTF-8 via" +
        " `-Dfile.encoding=UTF-8`, was: " + Charset.defaultCharset().name());
    }
  }

  public static void setDefaultUtcTimezone() {
    String currentTimeZoneId = TimeZone.getDefault().getID();
    if( areDifferent(currentTimeZoneId, "UTC") ){
      log.warn("TimeZone.default forced to UTC from `%s` - JVM should be" +
          " started with `-Duser.timezone=UTC`.",
        currentTimeZoneId);
      TimeZone.setDefault(DateUtil.utcTimezone());
    }
  }

  public static void setDefaultRootLocale() {
    Locale locale = Locale.getDefault();
    if( !locale.equals(Locale.ROOT) ){
      log.warn("Locale forced to ROOT from `%s` - JVM should be started with" +
          " `-Duser.language= -Duser.country= -Duser.variant=`",
        locale);
      Locale.setDefault(Locale.ROOT);
    }
  }

  private static void logStartupInfo() {
    infoLogSysProp("java.vendor", "java.vm.vendor", "java.vm.name",
      "java.version", "java.runtime.version", "java.runtime.name");
    infoLogSysProp("os.name", "user.timezone", "file.encoding",
      "user.language", "user.country", "user.variant");
    logBuildInfo();
  }

  private static void infoLogSysProp(String... names) {
    for( String iName : names ){
      log.info(formatSysProp(iName));
    }
  }

  private static String formatSysProp(String name) {
    return String.format("%s: %s", name, System.getProperty(name));
  }

  /**
   This method intended to be called *before* Spring is configured, so
   it can't use any of the bean instance fields.
   Not sure this is the best place for it.@param log
   */
  public static void logBuildInfo() {
    // build-info.properties is created by the build process 
    String buildInfoPath = "/META-INF/build-info.properties";
    InputStream buildInfo = JvmUtil.class.getResourceAsStream(buildInfoPath);

    if( buildInfo == null ){
      log.info("no resource stream returned for %s", buildInfoPath);
      return;
    }

    log.info("build-info:\n%s", IoUtil.linesAsString(buildInfo));
  }

}
