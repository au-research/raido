package raido.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class RestUtil {
  
  public static <T> HttpEntity<T> createEntityWithBearer(String authnToken) {
    Guard.notNull(authnToken);
    var headers = new HttpHeaders();
    headers.set("Authorization", "bearer "+ authnToken);
    return new HttpEntity<T>(headers);
  }
  
  public static <T> HttpEntity<T> createEntityWithBearer(
    String authnToken, T body
  ) {
    Guard.notNull(authnToken);
    var headers = new HttpHeaders();
    headers.set("Authorization", "bearer "+ authnToken);
    return new HttpEntity<T>(body, headers);
  }
  
}
