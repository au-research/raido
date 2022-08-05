package raido.apisvc.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 Indicates that a field or parameter may be null.
 Absence of this implies a field or param is not allowed to be null.
 <p/>
 NOTE: Generally speaking, you shouldn't annotate a method with this, change
 your method to return optional instead.
 I added ElementType.METHOD because Jooq generates it on methods.
 <p/>
 "Which @NotNull Java annotation should I use?"
 https://stackoverflow.com/q/4963300/924597
 All those options are awful, so we're using our own annotation.
 Needs to be configured explicitly in IDEs (Eclispse, IDEA, etc.) and tools
 (jOOQ, etc.)
 If tools aren't configured, then it still acts as documentation.
 */
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
// for frameworks using nullability annotations at runtime (Guice, etc.)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Nullable {
}
