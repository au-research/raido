package raido.util;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.TimeZone;

public class DateUtil {
  public static ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
  private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
  
  public static TimeZone utcTimezone() {
    return TimeZone.getTimeZone("UTC");
  }



  public static String formatUtcDateTime(
    @Nullable LocalDateTime d,
    String defaultValue
  ) {
    if( d == null ){
      return defaultValue;
    }

    SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
    sdf.setTimeZone(utcTimezone());
    return sdf.format(d);
  }
  
  public static String formatUtcDateTime(
    LocalDateTime d
  ) {
    Guard.notNull(d);

    SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
    sdf.setTimeZone(utcTimezone());
    return sdf.format(d);
  }
  
}

