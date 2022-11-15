package raid.ddb_migration

import groovy.json.JsonBuilder

import java.time.*
import java.util.function.Supplier

import static java.lang.String.format

class Util {
  static ZoneId sydneyZone = ZoneId.of("Australia/Sydney")
  static ZoneId utcZone = ZoneId.of("UTC")

  static <T> T printExecTime(
    String description, Supplier<T> r
  ){
    long beforeReq = System.nanoTime();
    T result;
    try{
      result = r.get();
    }
    catch( Throwable e ){
      long time = (long) ((System.nanoTime() - beforeReq) / 1_000_000);
      println format("%s failed, took %s ms", description, time);
      throw e;
    }
    long time = (long) ((System.nanoTime() - beforeReq) / 1_000_000);
    println format("%s took %s ms", description, time);
    return result;
  }

  static String prettyJson(Object json){
    return new JsonBuilder(json).toPrettyString()
  }

  static String formalJson(Object json){
    return new JsonBuilder(json).toString()
  }

  static LocalDateTime parseDate(
    String format, 
    ZoneId zone, 
    String value
  ){
    if( !value ){
      return null
    }

    /* The timestamp in the export is local time to Sydney - must account
    for both timezone and daylight savings. */
    ZonedDateTime zdt = LocalDateTime.parse(value, format).atZone(zone)
    
    /* systemDefault() should be UTC on EC2 machines, in containers, or run
    from gradle; but if you just invoke the JVM directly on a dev machine,  
    it'll be your local TZ */
    return zdt.withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
  }

  static ZonedDateTime inSydneyZone(LocalDateTime date){
    return date.atZone(utcZone).withZoneSameInstant(sydneyZone)    
  }
  
  static ZonedDateTime inSystemZone(LocalDateTime date){
    return date.atZone(utcZone).withZoneSameInstant(ZoneId.systemDefault())    
  }

  /**
   @return will return null if null is passed
   */
  static LocalDateTime offset2Local(OffsetDateTime d){
    if( d == null ){
      return null;
    }

    return d.toLocalDateTime();
  }

  static OffsetDateTime local2Offset(LocalDateTime d){
    if( d == null ){
      return null;
    }

    return d.atOffset(ZoneOffset.UTC);
  }


}
