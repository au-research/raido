package au.org.raid.api.util;


import au.org.raid.api.spring.bean.Shared;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
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
   convenience delegator: {@link Objects#equals(Object, Object)}
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

  public static void infoLogExecutionTime(
    Log log, String description, Runnable r
  ) {
    infoLogExecutionTime(log, description, ()->{
      r.run();
      // hack to adapt our "no return value" wrapper to the real method 
      return null;
    });
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

  /**
   Thread safe - but consider: https://stackoverflow.com/a/36162525/924597
   
   Mitigated by the fact that if we're calling toString() on an object, we're
   already likely on a slow path (exception handling or some debug loggging).
   Generally, we only use this for debugging and occasional logging (where 
   we want to avoid logging too much in general - and the "structured logging" 
   logic could also get fancy with this too, as long as we use `with()`). 
   This is just for usage in the context of `toString()` - wire serialisation 
   for these objects is handled by Spring, 
   see {@link Shared#restTemplate(ClientHttpRequestFactory)}.
   */
  private static ObjectMapper jsonToStringMapper = new ObjectMapper().
    // so it can do LocalDateTime, etc.
    findAndRegisterModules();

  public static String jsonToString(Object value) {
    try {
      return jsonToStringMapper.writeValueAsString(value);
    }
    catch( JsonProcessingException e ){
      throw ExceptionUtil.wrapException(e, "could not generate toString()");
    }
  }
  
  public static boolean isTrue(@Nullable Optional<Boolean> value){
    if( value == null ){
      return false;
    }
    if( value.isEmpty() ){
      return false;
    }
    
    return value.get();
  }
  
  public static boolean isTrue(@Nullable Boolean value){
    if( value == null ){
      return false;
    }
    
    return value;
  }
  
  public static boolean isEmpty(@Nullable Collection<?> c){
    if( c == null ){
      return true;
    }
    
    return c.isEmpty();
  }

  /**
   Return a new list containing only up to the `first` element count.
   I really thought this utility functionality existed somewhere already, but
   I couldn't find it.  Remove this method if you can find a simple built-in
   somewhere already including in the codebase.
   */
  public static <T> List<T> first(@Nullable List<T> list, int first){
    if( list == null || list.isEmpty() ){
      return list;
    }
    
    return list.subList(0, Integer.min(list.size(), first));
  }

  public static <T> List<T> last(@Nullable List<T> list, int last){
    if( list == null || list.isEmpty() ){
      return list;
    }
    
    return list.subList(list.size()-last, list.size()-1);
  }
  
  /**
   Use with Stream.collect() to iterate over a stream with an index, 
   so you can bind the index into lambdas.
   https://stackoverflow.com/a/71885044/924597
   Note the index is 0-based, unlike the SO answer.
   */
  public static <T> Collector<T, ?, Map<Integer, T>> indexed() {
    return Collector.of(
      LinkedHashMap::new,
      (Map<Integer, T> map, T element)-> map.put(map.size(), element),
      (Map<Integer, T> left, Map<Integer, T> right)-> {
        left.putAll(right);
        return left;
      });
  }
}
