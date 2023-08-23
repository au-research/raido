package au.org.raid.api.service.ror;

import au.org.raid.api.util.Log;
import au.org.raid.api.util.Security;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Pattern;

import static au.org.raid.api.spring.bean.LogMetric.VALIDATE_ROR_EXISTS;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.regex.Pattern.compile;

public class RorService {
    public static final Pattern ROR_REGEX =
            compile("^https://ror\\.org/[0-9a-z]{9}$");
    public static final String NOT_FOUND_MESSAGE = "The ROR does not exist.";
    protected static final Log httpLog = to(RorService.class, "http");
    private static final Log log = to(RorService.class);
    private RestTemplate rest;

    public RorService(RestTemplate rest) {
        this.rest = rest;
    }

    public static void guardSsrf(String ror) {
        Security.guardSsrf("ROR", ROR_REGEX, ror);
    }

    public List<String> validateRorExists(String ror) {
        guardSsrf(ror);

        final var requestEntity = RequestEntity.head(ror).build();

        try {
            infoLogExecutionTime(log, VALIDATE_ROR_EXISTS, () ->
                    rest.exchange(requestEntity, Void.class)
            );
        } catch (HttpClientErrorException e) {
            log.with("message", e.getMessage()).
                    with("status", e.getStatusCode()).
                    warn("Problem retrieving ROR");
            return of(NOT_FOUND_MESSAGE);
        }

        return emptyList();
    }
}
