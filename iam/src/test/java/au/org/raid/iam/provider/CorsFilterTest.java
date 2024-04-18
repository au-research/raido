package au.org.raid.iam.provider;

import au.org.raid.iam.provider.cors.CorsResponseFilter;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CorsFilterTest {
    private final CorsResponseFilter filter = new CorsResponseFilter();

    @Test
    @DisplayName("Adds CORS headers for localhost")
    void headersForLocalhost() throws IOException {
        final var origin = "http://localhost:7080";
        final var requestContext = mock(ContainerRequestContext.class);
        final var responseContext = mock(ContainerResponseContext.class);
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Origin")).thenReturn(origin);
        when(responseContext.getHeaders()).thenReturn(headers);

        filter.filter(requestContext, responseContext);

        assertThat(headers.get("Access-Control-Allow-Origin"), Matchers.contains(origin));
        assertThat(headers.get("Access-Control-Allow-Methods"), Matchers.contains("GET, POST, PUT, DELETE, OPTIONS, HEAD"));
        assertThat(headers.get("Access-Control-Allow-Headers"), Matchers.contains("Origin, Content-Type, Accept, Authorization"));
        assertThat(headers.get("Access-Control-Allow-Credentials"), Matchers.contains("true"));
        assertThat(headers.get("Access-Control-Max-Age"), Matchers.contains("3600"));
    }

    @Test
    @DisplayName("Adds CORS headers for test")
    void headersForTest() throws IOException {
        final var origin = "https://app.test.raid.org.au";
        final var requestContext = mock(ContainerRequestContext.class);
        final var responseContext = mock(ContainerResponseContext.class);
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Origin")).thenReturn(origin);
        when(responseContext.getHeaders()).thenReturn(headers);

        filter.filter(requestContext, responseContext);

        assertThat(headers.get("Access-Control-Allow-Origin"), Matchers.contains(origin));
        assertThat(headers.get("Access-Control-Allow-Methods"), Matchers.contains("GET, POST, PUT, DELETE, OPTIONS, HEAD"));
        assertThat(headers.get("Access-Control-Allow-Headers"), Matchers.contains("Origin, Content-Type, Accept, Authorization"));
        assertThat(headers.get("Access-Control-Allow-Credentials"), Matchers.contains("true"));
        assertThat(headers.get("Access-Control-Max-Age"), Matchers.contains("3600"));
    }

    @Test
    @DisplayName("Adds CORS headers for demo")
    void headersForDemo() throws IOException {
        final var origin = "https://app.demo.raid.org.au";
        final var requestContext = mock(ContainerRequestContext.class);
        final var responseContext = mock(ContainerResponseContext.class);
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Origin")).thenReturn(origin);
        when(responseContext.getHeaders()).thenReturn(headers);

        filter.filter(requestContext, responseContext);

        assertThat(headers.get("Access-Control-Allow-Origin"), Matchers.contains(origin));
        assertThat(headers.get("Access-Control-Allow-Methods"), Matchers.contains("GET, POST, PUT, DELETE, OPTIONS, HEAD"));
        assertThat(headers.get("Access-Control-Allow-Headers"), Matchers.contains("Origin, Content-Type, Accept, Authorization"));
        assertThat(headers.get("Access-Control-Allow-Credentials"), Matchers.contains("true"));
        assertThat(headers.get("Access-Control-Max-Age"), Matchers.contains("3600"));
    }

    @Test
    @DisplayName("Adds CORS headers for prod")
    void headersForProd() throws IOException {
        final var origin = "https://app.prod.raid.org.au";
        final var requestContext = mock(ContainerRequestContext.class);
        final var responseContext = mock(ContainerResponseContext.class);
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Origin")).thenReturn(origin);
        when(responseContext.getHeaders()).thenReturn(headers);

        filter.filter(requestContext, responseContext);

        assertThat(headers.get("Access-Control-Allow-Origin"), Matchers.contains(origin));
        assertThat(headers.get("Access-Control-Allow-Methods"), Matchers.contains("GET, POST, PUT, DELETE, OPTIONS, HEAD"));
        assertThat(headers.get("Access-Control-Allow-Headers"), Matchers.contains("Origin, Content-Type, Accept, Authorization"));
        assertThat(headers.get("Access-Control-Allow-Credentials"), Matchers.contains("true"));
        assertThat(headers.get("Access-Control-Max-Age"), Matchers.contains("3600"));

    }

    @Test
    @DisplayName("Adds no CORS headers if origin is invalid")
    void invalidOrigin() throws IOException {
        final var origin = "https://app.tes.raid.org.au";
        final var requestContext = mock(ContainerRequestContext.class);
        final var responseContext = mock(ContainerResponseContext.class);
        final MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();

        when(requestContext.getHeaderString("Origin")).thenReturn(origin);
        when(responseContext.getHeaders()).thenReturn(headers);

        filter.filter(requestContext, responseContext);

        assertThat(headers, not(hasKey("Access-Control-Allow-Origin")));
        assertThat(headers, not(hasKey("Access-Control-Allow-Origin")));
        assertThat(headers, not(hasKey("Access-Control-Allow-Methods")));
        assertThat(headers, not(hasKey("Access-Control-Allow-Headers")));
        assertThat(headers, not(hasKey("Access-Control-Allow-Credentials")));
        assertThat(headers, not(hasKey("Access-Control-Max-Age")));
    }
}