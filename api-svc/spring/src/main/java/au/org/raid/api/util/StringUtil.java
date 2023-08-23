package au.org.raid.api.util;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static au.org.raid.api.util.ObjectUtil.nullToDefault;
import static java.util.stream.Collectors.joining;

/**
 Centralise handling of strings here, so definitions are consistent and don't
 change unless we want them to (tools such as commons and guava have changed
 their definitions of things like "has value" and "is empty" before).
 <p>
 No method in this class should ever return null, use optional instead. If a
 param is annotated with @Nullable, then the method must handle that and
 should probably document what it does - otherwise all methods any method is
 allowed to fail if you pass null for a param that is not marked nullable.
 */
public final class StringUtil {
  public static final int DEFAULT_MAX_ELEMENTS_TO_STRING = 20;

  public static final String COMMA_SPACE = ", ";
  public static final String MASK_SEPARATOR = "...";
  public static final int DEFAULT_MASK_LENGTH = 10;

  public static final Collector<CharSequence, ?, String> BRACKET_JOINER =
    Collectors.joining(",", "[", "]");

  /**
   Null is not a value.  Empty string is not a value.  Whitespace is not a
   value.
   */
  public static boolean hasValue(String target) {
    return !isBlank(target);
  }

  public static boolean isNullOrEmpty(String string) {
    return string == null || string.isEmpty();
  }

  /**
   Null is blank. Empty string is blank.  All whitespace is blank.
   */
  public static boolean isBlank(String target) {
    if( isNullOrEmpty(target) ){
      return true;
    }
    return isWhitespace(target);
  }


  /**
   <p>Checks if the CharSequence contains only whitespace.</p>

   <p>{@code null} will return {@code false}.
   An empty CharSequence (length()=0) will return {@code true}.</p>

   <pre>
   StringUtils.isWhitespace(null)   = false
   StringUtils.isWhitespace("")     = true
   StringUtils.isWhitespace("  ")   = true
   StringUtils.isWhitespace("abc")  = false
   StringUtils.isWhitespace("ab2c") = false
   StringUtils.isWhitespace("ab-c") = false
   </pre>

   @param cs the CharSequence to check, may be null
   @return {@code true} if only contains whitespace, and is non-null
   @implNote This was taken from the commons-lang 3 source base, I didn't
   want to have to include all of cl-3 just for that one method.
   */
  public static boolean isWhitespace(@Nullable CharSequence cs) {
    if (cs == null) {
      return false;
    }
    final int sz = cs.length();
    for (int i = 0; i < sz; i++) {
      if ( !Character.isWhitespace(cs.charAt(i)) ) {
        return false;
      }
    }
    return true;
  }

  /**
   The behaviour with elements that are null or empty is unspecified (see unit
   tests) - bad idea to depend on current behaviour unless you document and
   write tests.

   @param args returns empty string if null or empty
   */
  public static String formatCommaSeparated(@Nullable String... args) {
    if( args == null || args.length == 0 ){
      return "";
    }
    return String.join(COMMA_SPACE, args);
  }

  /**
   Convert the args to a string with converter - be careful of nulls, you
   risk NPE's if you just use Object::toString.
   <p/>
   Not sure about the safevarargs thing, need to investigate.
   */
  private static <T> String convertToString(
    Function<T, String> converter,
    int maxElements,
    @Nullable List<T> args)
  {
    Guard.notNull("converter was null", converter);
    if( args == null ){
      return "";
    }
    return args.stream().
      limit(maxElements).
      map(converter).
      collect(joining(COMMA_SPACE));
  }

  public static <T> String convertToString(
    @Nullable T[] args)
  {
    if( args == null ){
      return "";
    }
    return convertMaxToString(
      DEFAULT_MAX_ELEMENTS_TO_STRING,
      Arrays.asList(args) );
  }

  public static <T> String convertMaxToString(
    int maxElements,
    @Nullable List<T> args)
  {
    return convertToString(
      i -> nullToDefault(i, "").toString(),
      maxElements,
      args);
  }


  /**
   @param defaultValue will be returned if args is null or empty, or if all
   objects result in blank strings.
   */
  public static <T> String listToString(
    int maxElements,
    @Nullable List<T> args,
    Function<T, String> converter,
    String defaultValue
  ){
    Guard.notNull("converter was null", converter);
    if( args == null || args.size() == 0 ){
      return defaultValue;
    }

    String resultValue = args.stream().
      limit(maxElements).
      map(converter).
      filter(StringUtil::hasValue).
      collect(joining(COMMA_SPACE));

    if( isBlank(resultValue) ){
      return defaultValue;
    }

    return resultValue;
  }

  public static <T> String listSomeToString(
    @Nullable List<T> args,
    Function<T, String> converter
  ){
    return listToString(
      DEFAULT_MAX_ELEMENTS_TO_STRING, args, converter, "<none>");
  }

  public static <T> String listAllToString(
    @Nullable List<T> args,
    Function<T, String> converter
  ){
    if( args == null ){
      return "<none>";
    }
    return listToString(args.size(), args, converter, "<none>");
  }

  public static String listAllToString(
    @Nullable List<String> args
  ){
    if( args == null ){
      return "<none>";
    }
    return listToString(args.size(), args, Function.identity(), "<none>");
  }

  public static String listSomeToString(
    int max,
    @Nullable List<String> args
  ){
    if( args == null ){
      return "<none>";
    }
    return listToString(max, args, Function.identity(), "<none>");
  }

  /**
   Removes the suffix, if present, from the target string.

   @param target returns empty string if null
   @param suffix returns target string if null
   */
  public static String removeSuffix(
    @Nullable String target,
    @Nullable String suffix)
  {
    if( target == null ){
      return "";
    }
    if( suffix == null ){
      return target;
    }
    if( target.endsWith(suffix) ){
      return target.substring(0, target.length() - suffix.length());
    }
    else {
      return target;
    }
  }

  /**
   Removes the prefix, if present, from the target string.

   @param target returns empty string if null
   @param prefix returns target string if null
   */
  public static String removePrefix(
    @Nullable String target,
    @Nullable String prefix)
  {
    if( target == null ){
      return "";
    }
    if( prefix == null ){
      return target;
    }
    if( target.startsWith(prefix) ){
      return target.substring(prefix.length());
    }
    else {
      return target;
    }
  }

  /**
   returns true if every element passed in has a value.

   @param values if null or empty will return false
   @see #hasValue(String)
   */
  public static boolean allElementsHaveValues(@Nullable String... values) {
    if( values == null || values.length == 0 ){
      return false;
    }

    for( String iValue : values ){
      if( !hasValue(iValue) ){
        return false;
      }
    }

    return true;
  }

  /**
   returns true if any element passed in has a value.

   @param values if null or empty will return false
   @see #hasValue(String)
   */
  public static boolean anyElementHasValue(@Nullable String... values) {
    if( values == null || values.length == 0 ){
      return false;
    }

    for( String iValue : values ){
      if( hasValue(iValue) ){
        return true;
      }
    }

    return false;
  }

  /** @return returns empty string if null is passed, otherwise returns value */
  public static String nullToEmpty(@Nullable String value) {
    return (value == null ? "" : value);
  }

  /** @return returns empty string if null is passed, otherwise returns 
  toString() */
  public static String nullToString(@Nullable Object value) {
    return (value == null ? "" : value.toString());
  }

  /**
   Wraps a ternary around {@link #isBlank(String)}

   @return will not return null, if null is passed defaultValue and a default
   value is needed, will use empty string instead).
   */
  public static String blankToDefault(
    @Nullable String value,
    String defaultValue)
  {
    return (isBlank(value) ? nullToEmpty(defaultValue) : value);
  }

  /** @return true if both values are null */
  @SuppressWarnings("StringEquality")
  public static boolean equalsIgnoreCase(
    @Nullable String l,
    @Nullable String r)
  {
    // shortcut deal with instance equality and two null values
    if( l == r ){
      return true;
    }
    if( l == null || r == null ){
      return false;
    }

    return l.equalsIgnoreCase(r);
  }

  public static boolean trimEqualsIgnoreCase(
    @Nullable String l,
    @Nullable String r)
  {
    // shortcut deal with instance equality and two null values
    if( l == r ){
      return true;
    }
    if( l == null || r == null ){
      return false;
    }

    return l.trim().equalsIgnoreCase(r.trim());
  }

  /** @return true if both values are null */
  @SuppressWarnings("StringEquality")
  public static boolean areEqual(
    @Nullable String l,
    @Nullable String r)
  {
    // shortcut deal with instance equality and two null values
    if( l == r ){
      return true;
    }
    if( l == null || r == null ){
      return false;
    }

    return l.equals(r);
  }
  
  /** return true if l equals any of the given values */
  public static boolean 
  equalsAny(
    @Nullable String l,
    String... rValues)
  {
    for( String r : rValues ){
      if( areEqual(l, r) ){
        return true;
      }
    }
    return false;
  }
  
  public static boolean equalsAnyIgnoreCase(
    @Nullable String l,
    String... rValues
  ) {
    for( String r : rValues ){
      if( equalsIgnoreCase(l, r) ){
        return true;
      }
    }
    return false;
  }
  
  public static String format(@Nullable Collection<?> c){
    if( c == null ){
      return "";
    }
    return format(c, ", ");
  }

  public static String format(@Nullable Collection<?> c, String separator){
    if( c == null ){
      return "";
    }
    return c.stream().
      map(i -> i == null ? "null" : i.toString()).
      collect(Collectors.joining(separator));
  }

  /**
   @return does not return null; generally speaking, if it's a weird case
   you'll get back empty string; even if passed two null values it will
   give you empty string.
   */
  public static String subtract(
    @Nullable String value, 
    @Nullable String prefix)
  {
    if( value == null && prefix == null ){
      return "";
    }
    else if( value == prefix ){
      return "";
    }
    else if( value == null ){
      // value is null, but prefix is non-null
      return "";
    }
    else if( prefix == null ){
      // prefix is null, but value is non-null
      return value;
    }
    else {
      if( value.startsWith(prefix) ){
        return value.substring(prefix.length());
      }
      else {
        return value;
      }
    }
    
  }

  public static boolean areDifferent(
    @Nullable String left, 
    @Nullable String right)
  {
    return !StringUtil.areEqual(left, right);
  }
  
  public static String truncate(@Nullable String value, int endIndex){
    if( value == null ){
      return "";
    }
    
    if( value.length() == 0 ){
      return value;
    }
    
    if( endIndex < 1 ){
      return "";
    }
    
    if( value.length() <= endIndex ){
      return value;
    }
    
    return value.substring(0, endIndex);
  }
  
  public static boolean endsWithIgnoreCase(
    @Nullable String value, 
    String suffix
  ){
    if( value == null ){
      return false;
    }
    
    return value.trim().toLowerCase().endsWith(suffix.trim().toLowerCase());
  }
  
  /**
   null->"", ""->"", 
   "a"->"A", "A"->"A", "A."->"A.", 
   "Aa."->"Aa.", "AA"->"Aa.", "aa"->"Aa.",
   ".aa" -> ".aa", " aa"->"aa"
   @return will not return null, gives empty string for null.
   */
  public static String capitalize(@Nullable String s){
    if( s == null ){
      return "";
    }
    if( s.length() == 0 ){
      return s;
    }
    if( s.length() == 1 ){
      return s.toUpperCase();
    }
    
    return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
  }

  /**
   Returns a string, of length at least {@code minLength}, consisting of
   {@code string} prepended
   with as many copies of {@code padChar} as are necessary to reach that 
   length. For example,

   <ul>
   <li>{@code padStart("7", 3, '0')} returns {@code "007"}
   <li>{@code padStart("2010", 3, '0')} returns {@code "2010"}
   </ul>

   <p>See {@link java.util.Formatter} for a richer set of formatting 
   capabilities.

   @param string    the string which should appear at the end of the result, may
   be null (treated as empty string)
   @param minLength the minimum length the resulting string must have. Can be
   zero or negative, in
   which case the input string is always returned.
   @param padChar   the character to insert at the beginning of the result 
   until the minimum length
   is reached
   @return the padded string
   @implNote taken from Google Guava
   */
  public static String padStart(
    @Nullable String string,
    int minLength,
    char padChar
  ) {
    if( string == null ){
      string = "";
    }
    if( string.length() >= minLength ){
      return string;
    }
    StringBuilder sb = new StringBuilder(minLength);
    for( int i = string.length(); i < minLength; i++ ){
      sb.append(padChar);
    }
    sb.append(string);
    return sb.toString();
  }  
  
  public static String mask(String value){
    return mask(value, DEFAULT_MASK_LENGTH);
  }

  /** makes a value that is safe to put in log messages, show to user, etc.
  @param maskLength the number of characters to show 
   */
  public static String mask(String value, int maskLength) {
    if( value == null || value.length() == 0 ){
      return "";
    }

    if( maskLength < MASK_SEPARATOR.length() ){
      return MASK_SEPARATOR + value.length();
    }

    if( value.length() < maskLength ){
      return MASK_SEPARATOR + value.length();
    }
    
    return truncate(value, maskLength - MASK_SEPARATOR.length()) +
      MASK_SEPARATOR + value.length();
  }

}
