package au.org.raid.api.service.orcid;

import au.org.raid.api.util.Log;
import au.org.raid.api.util.Security;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Pattern;

import static au.org.raid.api.spring.bean.LogMetric.VALIDATE_ORCID_EXISTS;
import static au.org.raid.api.util.Log.to;
import static au.org.raid.api.util.ObjectUtil.infoLogExecutionTime;
import static java.util.Collections.emptyList;
import static java.util.regex.Pattern.compile;

public class OrcidService {
  public static final Pattern ORCID_REGEX =
    compile("^https://orcid\\.org/[\\d]{4}-[\\d]{4}-[\\d]{4}-[\\d]{3}[\\d|X]{1}$");
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
