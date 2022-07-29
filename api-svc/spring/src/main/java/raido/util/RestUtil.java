package raido.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

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
  
  public static HttpHeaders createBasicHeaders(
    String username, String password
  ) {
    Guard.allHaveValue(username);
    Guard.allHaveValue(password);

    String credentials = username + ":" + password;
    String encodedCredentials = Base64.getEncoder()
      .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    
    var headers = new HttpHeaders();

    headers.set("Authorization", "Basic "+ encodedCredentials);
    
    return headers;
  }
  
  
  
}
