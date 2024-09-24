package au.org.raid.iam.provider.cors;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

@Slf4j
@Provider
public class CorsResponseFilter implements ContainerResponseFilter {
    protected static final CorsResponseFilter INSTANCE = new CorsResponseFilter();
    private static final String ORIGIN_HEADER = "Origin";
    private static final List<String> VALID_ORIGINS = List.of(
            "http://localhost:7080",
            "https://(app|app3).(test|demo|prod).raid.org.au"
    );
    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        final var origin = requestContext.getHeaderString(ORIGIN_HEADER);
        log.debug("Calling CorsResponseFilter");

        if (origin != null) {
            VALID_ORIGINS.forEach(validOrigin -> {
                if (origin.matches(validOrigin)) {
                    log.debug("Matched origin {}", origin);

                    responseContext.getHeaders().add("Access-Control-Allow-Origin", origin);
                    responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
                    responseContext.getHeaders().add("Access-Control-Allow-Headers", "Origin, Content-Type, Accept, Authorization");
                    responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
                    responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
                }
            });
        }
        else {
            log.debug("Origin is null");
        }
    }
}
