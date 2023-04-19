package raido.apisvc.service.stub.util;

import raido.apisvc.util.ThreadUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 If client never exceeds an allocation rate of 1000 id's / second / node,
 then you need a column of 23 chars to hold unique id values.  21 if you use
 the compact form (without separators).
 <p/>
 Optimisations:
 <p/>
 This could be brought down to 11 chars by using Base64 alpha encoding
 (10 if we elide the "u" separator, but you'd have theoretical collisions
 between IDs generated in the same millisecond with later times, though
 you'd never collide with any time in the current millennium).
 <p/>
 You could bring it down to 7 chars if you limit the generator to one
 per millisecond.
 <p/>
 You could get it a bit lower if you expand the alphabet - depends on what
 domain of characters your target field supports.
 <p/>
 The sub-millisecond "u" suffixing could be optimised too, combining it into
 the encoding process instead of appending full characters would save a
 few characters.

 @see AlphaEncoder */
public class IdFactory {
  public static final String SEPARATOR = "u";

  private final Object generatorLock = new Object();
  private final String nodePrefix;

  private volatile String lastGeneratedTimeSuffix = "start";
  private volatile int lastUniqueSuffix = 1;

  public IdFactory(String nodePrefix) {
    this.nodePrefix = nodePrefix;
  }

  public String generateUniqueId(String prefix, boolean separator) {

    // questionable, might be better to use a proper modern-java lock structure
    synchronized( generatorLock ){
      StringBuilder buf = new StringBuilder();
      if( prefix != null ){
        buf.append(prefix);
        if( separator ){
          buf.append(SEPARATOR);
        }
      }

      String thisTimeSuffix = separator ?
        formatMillisecondFileSafe(now()) :
        formatCompactMillisecond(now());
      buf.append(thisTimeSuffix);

      if( lastGeneratedTimeSuffix.equals(thisTimeSuffix) ){
        buf.append(SEPARATOR).append(lastUniqueSuffix++);
      }
      else {
        lastUniqueSuffix=1;
      }

      lastGeneratedTimeSuffix = thisTimeSuffix;
      
      return buf.toString();
    }
  }

  public String generateUniqueId() {
    return generateUniqueId(true);
  }

  public String generateUniqueId(boolean separator) {
    return generateUniqueId(nodePrefix, separator);
  }

  private static Date now() {
    return new Date();
  }

  private static String COMPACT_MIILLISECOND_DATETIME_FORMAT =
    "yyyyMMddHHmmssSSS";

  private static String FILESAFE_MIILLISECOND_DATETIME_FORMAT =
    "yyyyMMdd-HHmmss-SSS";

  private static String formatMillisecondFileSafe(Date date) {

    SimpleDateFormat fmt = new SimpleDateFormat(
      FILESAFE_MIILLISECOND_DATETIME_FORMAT);
    fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

    return fmt.format(date);
  }

  private static String formatCompactMillisecond(Date date) {
    SimpleDateFormat fmt = new SimpleDateFormat(
      COMPACT_MIILLISECOND_DATETIME_FORMAT);
    fmt.setTimeZone(TimeZone.getTimeZone("UTC"));

    return fmt.format(date);
  }

  
}
