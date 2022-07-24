package raido.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;


public final class ObjectUtil {

  /**
   @return returns empty string if null is passed, otherwise returns value
   */
  public static <T> T nullToDefault(@Nullable T value, T defaultValue) {
    return (value == null ? defaultValue : value);
  }

  public static boolean containsIgnoreCase(Stream<String> list, String value) {
    return list.anyMatch(
      iElement->StringUtil.equalsIgnoreCase(iElement, value)
    );
  }

  public static boolean containsIgnoreCase(String value, String... list) {
    return containsIgnoreCase(Arrays.stream(list), value);
  }

  /**
   convenience delgator: {@link Objects#equals(Object, Object)}
   */
  public static boolean areEqual(Object l, Object r) {
    return Objects.equals(l, r);
  }

  /**
   !{@link #areEqual(Object, Object)}
   */
  public static boolean areDifferent(Object l, Object r) {
    return !Objects.equals(l, r);
  }

  // put supplier last so that multi-line lambda's read better
  public static <T> T infoLogExecutionTime(
    Log log, String description, Supplier<T> r
  ) {
    long beforeReq = System.nanoTime();
    T result;
    try {
      result = r.get();
    }
    catch( Exception e ){
      long time = (System.nanoTime() - beforeReq) / 1_000_000;
      log.info("%s failed, took %s ms", description, time);
      throw e;
    }
    long time = (System.nanoTime() - beforeReq) / 1_000_000;
    log.info("%s took %s ms", description, time);
    return result;
  }

  public static <T> List<T> filterType(
    List<?> raw, Class<T> type
  ) {
    return raw.stream().
      filter(type::isInstance).
      map(type::cast).
      collect(toList());
  }

  /**
   Will not return null, always returns a new list leaving inputs
   unchanged.
   */
  public static <T> List<T> concat(
    @Nullable List<T> left,
    @Nullable List<T> right
  ) {
    if( left == null && right == null ){
      return Collections.emptyList();
    }
    else if( left == right ){
      return new ArrayList<>(left);
    }
    else if( left == null ){
      return new ArrayList<>(right);
    }
    else if( right == null ){
      return new ArrayList<>(left);
    }

    return Stream.concat(left.stream(), right.stream()).collect(toList());
  }

  public static <T> String mapJoin(
    Function<T, String> map,
    String separator,
    Collection<T> list
  ) {
    return list.stream().map(map).collect(Collectors.joining(separator));
  }

  public static <T, R> List<R> listMap(
    Collection<T> list,
    Function<T, R> map
  ) {
    return list.stream().map(map).collect(Collectors.toList());
  }

  public static <T> List<T> concat(List<T> list, T element) {
    List<T> result = new ArrayList<>(list);
    result.add(element);
    return result;
  }

  public static boolean hasValue(Collection<?> c) {
    if( c == null ){
      return false;
    }
    if( c.isEmpty() ){
      return false;
    }

    return true;
  }
}
