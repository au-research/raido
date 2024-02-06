package au.org.raid.api.factory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.Collections;

@Component
public class HttpHeadersFactory {
    public HttpHeaders createBasicAuthHeaders(final String username, final String password) {
        final var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(HttpHeaders.encodeBasicAuth(username, password, Charset.defaultCharset()));
        return headers;
    }

}
