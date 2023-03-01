package raido.apisvc.spring.bean;

import org.springframework.stereotype.Component;
import raido.apisvc.util.Nullable;

import java.util.Arrays;
import java.util.Optional;

/**
 Expects an url that has been passed from Spring MVC, so:
 - assume Spring already decoded the url if it was encoded
 - assume that deep paths that LOOK like prefixes would have already been
 directed to their mapping endpoint and not get passed, so we choose to assume
 that extra stuff on the end of the url is in the nature of a "description"
 (like StackOverflow question titles).
 
 IMPROVE: 
 - match specific configured prefixes only (102.100.100, 10378.1, etc.)
 - match only "proper" prefixes composed of digits and decimal separators
 - optimise for garbage allocation rate, CPU used
 */
@Component
public class HandleUrlParser {
  public static final char HANDLE_SEPARATOR_CHAR = '/';
  public static final String HANDLE_SEPARATOR =
    String.valueOf(HANDLE_SEPARATOR_CHAR);

  public Optional<Handle> parse(@Nullable String url) {
    if( url == null ){
      return Optional.empty();
    }

    String handle = url.trim();
    //? handle = handle.toLowerCase();

    if( handle.length() == 0 ){
      return Optional.empty();
    }

    if( handle.charAt(0) == HANDLE_SEPARATOR_CHAR ){
      handle = handle.substring(1);
    }

    String[] split = handle.split(HANDLE_SEPARATOR);
    if( split.length < 2 ){
      return Optional.empty();
    }

    if( split.length == 2 ){
      return Optional.of(new Handle(split[0], split[1], Optional.empty()));
    }

    return Optional.of(new Handle(
      split[0],
      split[1],
      Optional.of(String.join(
        HANDLE_SEPARATOR,
        Arrays.copyOfRange(split, 2, split.length)
      ))
    ));
  }

  public record Handle(
    String prefix,
    String suffix,
    /** we expect that deep paths that LOOK like prefixes but actually map
     to API stuff will have already been mapped to their endpoint method, so 
     we the parser chooses to assume the extra stuff is in the nature of a 
     "description" (like StackOverflow question titles). 
     */
    Optional<String> description
  ) {
    
    public String formatUrl(String urlPrefix){
      StringBuilder url = new StringBuilder(urlPrefix);
      url.
        append(HANDLE_SEPARATOR_CHAR).append(prefix).
        append(HANDLE_SEPARATOR_CHAR).append(suffix);

      description.ifPresent(s->
        url.append(HANDLE_SEPARATOR_CHAR).append(s)
      );
      
      return url.toString();
    }
  }

}
