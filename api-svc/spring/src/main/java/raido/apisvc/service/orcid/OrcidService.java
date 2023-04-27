package raido.apisvc.service.orcid;

import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.util.Log;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_ORCID_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class OrcidService {
  private static final Log log = to(OrcidService.class);
  protected static final Log httpLog = to(OrcidService.class, "http");

  private RestTemplate rest;

  public OrcidService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateOrcidExists(String orcid) {

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
