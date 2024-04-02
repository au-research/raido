package au.org.raid.api.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HttpEntityFactory {
    private final HttpHeadersFactory httpHeadersFactory;

    public <T> HttpEntity<T> create(final T body, final String repositoryId, final String password) {
        final HttpHeaders headers = httpHeadersFactory.createBasicAuthHeaders(repositoryId, password);
        return new HttpEntity<>(body, headers);
    }
}
