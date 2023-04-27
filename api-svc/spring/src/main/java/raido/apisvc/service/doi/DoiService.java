package raido.apisvc.service.doi;

import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.service.orcid.OrcidService;
import raido.apisvc.util.Log;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_DOI_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class DoiService {
  private static final Log log = to(OrcidService.class);
  protected static final Log httpLog = to(OrcidService.class, "http");

  private RestTemplate rest;

  public DoiService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateDoiExists(String doi) {

    final var requestEntity = RequestEntity.head(doi).build();

    try {
      infoLogExecutionTime(log, VALIDATE_DOI_EXISTS, ()->
        rest.exchange(requestEntity, Void.class)
      );
    }
    catch( HttpClientErrorException e ){
      log.warnEx("Problem retrieving DOI", e);
      return of("The DOI does not exist.");
    }

    return emptyList();
  }
}
