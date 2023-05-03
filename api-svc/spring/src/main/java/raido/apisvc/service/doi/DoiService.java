package raido.apisvc.service.doi;

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
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_DOI_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class DoiService {
  public static final Pattern DOI_REGEX = 
    compile("^http[s]?://doi\\.org/10\\..*");
  public static final String NOT_FOUND_MESSAGE = "The DOI does not exist.";

  private static final Log log = to(DoiService.class);
  protected static final Log httpLog = to(DoiService.class, "http");

  private RestTemplate rest;

  public DoiService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateDoiExists(String doi) {
    guardSsrf(doi);
    
    final var requestEntity = RequestEntity.head(doi).build();

    try {
      infoLogExecutionTime(log, VALIDATE_DOI_EXISTS, ()->
        rest.exchange(requestEntity, Void.class)
      );
    }
    catch( HttpClientErrorException e ){
      log.with("message", e.getMessage()).
        with("status", e.getStatusCode()).
        warn("Problem retrieving DOI");
      return of(NOT_FOUND_MESSAGE);
    }

    return emptyList();
  }

  public static void guardSsrf(String doi){
    Security.guardSsrf("DOI", DOI_REGEX, doi);
  }

}
