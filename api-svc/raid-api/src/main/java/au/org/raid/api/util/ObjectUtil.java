package au.org.raid.api.util;


import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;


public final class ObjectUtil {

    /**
     * @return returns empty string if null is passed, otherwise returns value
     */
    public static <T> T nullToDefault(@Nullable T value, T defaultValue) {
        return (value == null ? defaultValue : value);
    }

    public static boolean containsIgnoreCase(Stream<String> list, String value) {
        return list.anyMatch(
                iElement -> StringUtil.equalsIgnoreCase(iElement, value)
        );
    }

    public static boolean containsIgnoreCase(String value, String... list) {
        return containsIgnoreCase(Arrays.stream(list), value);
    }

    /**
     * convenience delegator: {@link Objects#equals(Object, Object)}
     */
    public static boolean areEqual(Object l, Object r) {
        return Objects.equals(l, r);
    }

    /**
     * !{@link #areEqual(Object, Object)}
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
        } catch (Exception e) {
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
        infoLogExecutionTime(log, description, () -> {
            r.run();
            // hack to adapt our "no return value" wrapper to the real method
            return null;
        });
    }

    public static boolean isEmpty(@Nullable Collection<?> c) {
        if (c == null) {
            return true;
        }

        return c.isEmpty();
    }

    /**
     * Return a new list containing only up to the `first` element count.
     * I really thought this utility functionality existed somewhere already, but
     * I couldn't find it.  Remove this method if you can find a simple built-in
     * somewhere already including in the codebase.
     */
    public static <T> List<T> first(@Nullable List<T> list, int first) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        return list.subList(0, Integer.min(list.size(), first));
    }

    public static <T> List<T> last(@Nullable List<T> list, int last) {
        if (list == null || list.isEmpty()) {
            return list;
        }

        return list.subList(list.size() - last, list.size() - 1);
    }

    /**
     * Use with Stream.collect() to iterate over a stream with an index,
     * so you can bind the index into lambdas.
     * https://stackoverflow.com/a/71885044/924597
     * Note the index is 0-based, unlike the SO answer.
     */
    public static <T> Collector<T, ?, Map<Integer, T>> indexed() {
        return Collector.of(
                LinkedHashMap::new,
                (Map<Integer, T> map, T element) -> map.put(map.size(), element),
                (Map<Integer, T> left, Map<Integer, T> right) -> {
                    left.putAll(right);
                    return left;
                });
    }
}
