package au.org.raid.api.util;

import org.jooq.Field;

public class JooqUtil {
  
  public static boolean valueFits(Field<String> field, @Nullable String value){
    if( value == null ){
      return true;
    }
    // but is this actually true for multi-byte unicode chars?
    // see the example strings in the URF-8 section of the coding standards
    return field.getDataType().length() >= value.length() ;
  }
}
