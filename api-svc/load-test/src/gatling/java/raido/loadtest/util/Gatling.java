package raido.loadtest.util;

import com.fasterxml.jackson.core.type.TypeReference;
import io.gatling.javaapi.core.CheckBuilder;
import io.gatling.javaapi.core.Session;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.gatling.javaapi.core.CoreDsl.bodyString;
import static raido.loadtest.util.Json.parseJson;

public class Gatling {

  public static Function<Session, Session> sessionDebug(Consumer<Session> f){
    return (sess)->{
      f.accept(sess);
      return sess;
    };
  }
  
  public static <T> BiFunction<T, Session, T> guard(Consumer<T> f){
    return (t, sess)->{
      f.accept(t);
      return t;
    };
  }

  /**
   Initially implemented for use on `foreach` session variables created during
   the iteration.
   AFAIK, the only state available with Gatling is the session,
   which is global state to the user, there's no concept of scoping
   session variables (to the "body" of the `foreach` iteration).
   That means I_SP_ID var will persist from one iteration to the next.
   If you don't re-initialise the var, the `doIfOrElse` will get the
   value from the previous iteration.
   If you map it to null `contains()` will fail 😠
   Guess I need a `SessionUtil.hasValue(Session, String)`.
   */
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  public static Session setOrRemove(Session sess, String name, Optional<?> value) {
    return value.
      map(i->sess.set(name, i)).
      orElseGet(()->sess.remove(name));
  }

  @SuppressWarnings("unchecked")
  public static <T> T sessionCast(
    Session sess, 
    String varName, 
    TypeReference<T> type
  ){
    return (T) sess.get(varName);
  }

  public static Function<Session, Session> convertSessionStringToType(
    String varName,
    TypeReference<?> type
  ){
    return (Session sess)-> sess.set(
      varName,
      parseJson(sess.getString(varName), type) );
  }

  /**
   Avoids having to have two separate constants for typed session variables.
   Groups the name and the type together to avoid copy/paste type errors.
   Adds convenience methods like `get()` and `saveBody()`. 
   */
  public static class Var<T> {
    public final String name;
    public final TypeReference<T> type;

    public Var(String name, TypeReference<T> type) {
      this.name = name;
      this.type = type;
    }
    
    public T get(Session sess){
      return sessionCast(sess, name, type);
    }

    public Session set(Session sess, T value){
      return sess.set(name, value);
    }
    
    public CheckBuilder.Final saveBody(){
      return bodyString().transform((body) ->
        parseJson(body, type)
      ).saveAs(name);
    }

    
  }
  
}
