package raido.apisvc.service.orcid;

import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.util.Log;
import raido.idl.raidv2.model.ValidationFailure;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.regex.Pattern.compile;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_ORCID_EXISTS;
import static raido.apisvc.util.ExceptionUtil.illegalArgException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class OrcidService {
  public static final Pattern ORCID_REGEX =
    compile("^https://orcid\\.org/[\\d]{4}-[\\d]{4}-[\\d]{4}-[\\d]{4}$");

  private static final Log log = to(OrcidService.class);
  protected static final Log httpLog = to(OrcidService.class, "http");

  private RestTemplate rest;

  public OrcidService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateOrcidExists(String orcid) {

    /* SSRF prevention - keep this "near" the actual HTTP call so 
    that static analysis tools understand it's protected */
    if( !ORCID_REGEX.matcher(orcid).matches() ){
      throw illegalArgException("ORCID failed SSRF prevention");
    }

    final var requestEntity = RequestEntity.head(orcid).build();

    try {
      infoLogExecutionTime(log, VALIDATE_ORCID_EXISTS, ()->
        rest.exchange(requestEntity, Void.class)
      );
    }
    catch( HttpClientErrorException e ){
      log.warnEx("Problem retrieving ORCID", e);
      return of("The ORCID does not exist.");
    }

    return emptyList();
  }

}
