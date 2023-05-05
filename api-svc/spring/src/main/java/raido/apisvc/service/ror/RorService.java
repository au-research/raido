package raido.apisvc.service.ror;

import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.util.Log;
import raido.apisvc.util.Security;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.regex.Pattern.compile;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_ROR_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class RorService {
  public static final Pattern ROR_REGEX =
    compile("^https://ror\\.org/[0-9a-z]{9}$");

  private static final Log log = to(RorService.class);
  protected static final Log httpLog = to(RorService.class, "http");
  public static final String NOT_FOUND_MESSAGE = "The ROR does not exist.";

  private RestTemplate rest;

  public RorService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateRorExists(String ror) {
    ror = guardSsrf(ror);
    
    final var requestEntity = RequestEntity.head(ror).build();

    try {
      infoLogExecutionTime(log, VALIDATE_ROR_EXISTS, ()->
        rest.exchange(requestEntity, Void.class)
      );
    }
    catch( HttpClientErrorException e ){
      log.with("message", e.getMessage()).
        with("status", e.getStatusCode()).
        warn("Problem retrieving ROR");
      return of(NOT_FOUND_MESSAGE);
    }

    return emptyList();
  }

  public static String guardSsrf(String ror){
    Security.guardSsrf("ROR", ROR_REGEX, ror);
    return ror;
  }
}
