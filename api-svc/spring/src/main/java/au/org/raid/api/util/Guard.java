package au.org.raid.api.util;


import java.util.List;
import java.util.function.Supplier;


/**
 Util functions to evaluate "guard clauses"; if the condition is true method
 will return, if false method will throw a runtime exception.
 Used to be called "check" but that word is now used for methods that return
 boolean or a list of problems (consistent with Spring validations, etc.)
 For complicated argument lists, "messages" come first, then other args go
 further to the right - for consistency with methods that check multiple args
 with a varargs param.
 */
public class Guard {
  public static void hasValue(
    @Nullable String message,
    @Nullable String value
  ) {
    if( !StringUtil.hasValue(value) ){
      throw new IllegalArgumentException(
        "check failed, no value present: " + StringUtil.nullToEmpty(message));
    }
  }

  public static void areEqual(
    @Nullable String msg,
    @Nullable String left,
    @Nullable String right
  ) {
    //noinspection StringEquality
    if( left == right ){
      return;
    }

    if( !StringUtil.areEqual(left, right) ){
      throw ExceptionUtil.illegalArgException(
        "%s, values are not equal - left=%s right=%s",
        msg, left, right);
    }
  }
  
  public static void areEqual(
    @Nullable String left,
    @Nullable String right
  ) {
    areEqual("check failed", left, right);
  }

  public static void areEqual(
    @Nullable Integer left,
    @Nullable Integer right
  ) {
    //noinspection NumberEquality
    if( left == right ){
      return;
    }

    if( !ObjectUtil.areEqual(left, right) ){
      throw ExceptionUtil.illegalArgException(
        "check failed, values are not equal - left=%s right=%s",
        left, right);
    }
  }

  public static void areEqual(
    String msg,
    @Nullable Integer left,
    @Nullable Integer right
  ) {
    //noinspection NumberEquality
    if( left == right ){
      return;
    }

    if( !ObjectUtil.areEqual(left, right) ){
      throw ExceptionUtil.illegalArgException(msg, left, right);
    }
  }

  public static void isBlank(
    @Nullable String message,
    @Nullable String value
  ) {
    if( !StringUtil.isBlank(value) ){
      throw new IllegalArgumentException(
        "check failed, not blank: " + StringUtil.nullToEmpty(message));
    }
  }

  public static void hasValue(@Nullable String v) {
    if( !StringUtil.hasValue(v) ){
      throw new IllegalArgumentException(
        "check failed, no value present");
    }
  }

  public static void hasValue(String msg, List<String> v) {
    Guard.notNull(msg, v);
    Guard.isTrue(msg, !v.isEmpty());
    Guard.allHaveValue(msg, v.toArray(new String[0]));
  }
  
  public static void isPositive(String msg, Long value) {
    Guard.notNull(msg, value);
    Guard.isTrue(msg, value != 0);
  }
  
  public static void hasValue(String msg, String... v) {
    Guard.notNull(msg, v);
    Guard.isTrue(msg, v.length > 0);
    Guard.allHaveValue(msg, v);
  }

  /**
   Checks that there is at least one string with a value in the give args.

   @param args if this is null or empty, method will throw exception
   */
  public static void anyHasValue(
    @Nullable String message,
    @Nullable String... args
  ) {
    if( args == null ){
      throw new IllegalArgumentException(
        "null arg array passed: " + StringUtil.nullToEmpty(message));
    }

    for( String iValue : args ){
      if( StringUtil.hasValue(iValue) ){
        return;
      }
    }
    throw new IllegalArgumentException(
      "none of the passes arguments had a value: " + StringUtil.nullToEmpty(
        message));
  }

  public static void allHaveValue(
    @Nullable String message,
    @Nullable String... args
  ) {
    if( args == null ){
      throw failedCheck("null arg array passed: %s", message);
    }

    for( int i = 0; i < args.length; i++ ){
      String iValue = args[i];
      if( !StringUtil.hasValue(iValue) ){
        throw failedCheck(
          "element %d of the args did not have value: %s",
          i, StringUtil.nullToEmpty(message));
      }
    }
  }

  public static void notNull(String message, @Nullable Object value) {
    if( value == null ){
      throw failedCheck("null value: %s", message);
    }
  }

  public static void notNull(Supplier<String> message, @Nullable Object value) {
    if( value == null ){
      throw failedCheck("null value: %s", message.get());
    }
  }

  public static void notNull(Object value) {
    if( value == null ){
      throw failedCheck("null value was given");
    }
  }

  public static void allNotNull(String message, Object... values) {
    for( Object iValue : values ){
      if( iValue == null ){
        throw failedCheck("one of the passed args is null: %s", message);
      }
    }
  }

  public static void isTrue(
    boolean condition
  ) {
    isTrue((String) null, condition);
  }

  public static void isTrue(
    String message,
    boolean condition
  ) {
    if( !condition ){
      if( message != null ){
        throw failedCheck("condition returned false: %s", message);
      }
      else {
        throw failedCheck("condition returned false");
      }
    }
  }

  public static void isTrue(
    Supplier<String> message,
    boolean condition
  ) {
    if( !condition ){
      throw failedCheck("condition returned false: %s", message.get());
    }
  }

  public static void isTrue(
    Supplier<String> message,
    Supplier<Boolean> condition
  ) {
    notNull("can't pass a null condition check", condition);
    Boolean result = condition.get();
    notNull("condition predicate can't return null", result);

    if( !result ){
      if( message != null ){
        throw failedCheck("condition returned false: %s", message.get());
      }
      else {
        throw failedCheck("condition returned false");
      }
    }
  }

  /**
   @param message {@link String#format(String, Object...)} format
   */
  public static IllegalArgumentException failedCheck(
    String message,
    Object... args
  ) {
    if( message == null ){
      if( args != null && args.length != 0 ){
        return new IllegalArgumentException(StringUtil.convertToString(args));
      }
      else {
        return new IllegalArgumentException("");
      }
    }
    if( args != null && args.length == 0 ){
      return new IllegalArgumentException(message);
    }
    return new IllegalArgumentException(String.format(message, args));
  }

  private static IllegalArgumentException failedCheck(String message) {
    return new IllegalArgumentException(message);
  }

  public static <T> T isInstance(Class<T> clazz, Object instance){
    if( instance == null ){
      throw failedCheck("expectedinstance of %s, but was null",
        clazz.getSimpleName() );
    }
    if( !clazz.isInstance(instance) ){
      throw failedCheck("expectedinstance of %s, but was %s, - %s",
        clazz.getSimpleName(),
        instance.getClass().getSimpleName(), 
        instance.toString() );
    }
    
    return clazz.cast(instance);
  }
  
}
