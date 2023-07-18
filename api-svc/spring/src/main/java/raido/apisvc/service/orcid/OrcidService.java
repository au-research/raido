package raido.apisvc.service.orcid;

import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.util.Log;
import raido.apisvc.util.Security;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.compile;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_ORCID_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class OrcidService {
  public static final Pattern ORCID_REGEX =
    compile("^https://orcid\\.org/[\\d]{4}-[\\d]{4}-[\\d]{4}-[\\d]{4}$");
  public static final String NOT_FOUND_MESSAGE = "The ORCID does not exist.";

  private static final Log log = to(OrcidService.class);
  protected static final Log httpLog = to(OrcidService.class, "http");

  private RestTemplate rest;

  public OrcidService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateOrcidExists(String orcid) {
    guardSsrf(orcid);

    final var requestEntity = RequestEntity.head(orcid).build();

    try {
      infoLogExecutionTime(log, VALIDATE_ORCID_EXISTS, ()->
        rest.exchange(requestEntity, Void.class)
      );
    }
    catch( HttpClientErrorException e ){
      log.with("message", e.getMessage()).
        with("status", e.getStatusCode()).
        warn("Problem retrieving ORCID");
      return List.of(NOT_FOUND_MESSAGE);
    }

    return emptyList();
  }

  public static void guardSsrf(String doi){
    Security.guardSsrf("ORCID", ORCID_REGEX, doi);
  }
  
}
