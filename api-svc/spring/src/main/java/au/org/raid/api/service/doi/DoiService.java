package au.org.raid.api.service.doi;

import au.org.raid.api.util.Log;
import au.org.raid.api.util.Security;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Pattern;

import static au.org.raid.api.spring.bean.LogMetric.VALIDATE_DOI_EXISTS;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.regex.Pattern.compile;

public class DoiService {
    public static final Pattern DOI_REGEX =
            compile("^http[s]?://doi\\.org/10\\..*");
    public static final String NOT_FOUND_MESSAGE = "The DOI does not exist.";
    protected static final Log httpLog = to(DoiService.class, "http");
    private static final Log log = to(DoiService.class);
    private RestTemplate rest;

    public DoiService(RestTemplate rest) {
        this.rest = rest;
    }

    public static void guardSsrf(String doi) {
        Security.guardSsrf("DOI", DOI_REGEX, doi);
    }

    public List<String> validateDoiExists(String doi) {
        guardSsrf(doi);

        final var requestEntity = RequestEntity.head(doi).build();

        try {
            infoLogExecutionTime(log, VALIDATE_DOI_EXISTS, () ->
                    rest.exchange(requestEntity, Void.class)
            );
        } catch (HttpClientErrorException e) {
            log.with("message", e.getMessage()).
                    with("status", e.getStatusCode()).
                    warn("Problem retrieving DOI");
            return of(NOT_FOUND_MESSAGE);
        }

        return emptyList();
    }

}
