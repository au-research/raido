package raido.apisvc.service.ror;

import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.util.Log;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_ROR_EXISTS;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class RorService {
  private static final Log log = to(RorService.class);
  protected static final Log httpLog = to(RorService.class, "http");

  private RestTemplate rest;

  public RorService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateRorExists(String ror) {

    final var requestEntity = RequestEntity.head(ror).build();

    try {
      infoLogExecutionTime(log, VALIDATE_ROR_EXISTS, ()->
        rest.exchange(requestEntity, Void.class)
      );
    } catch (HttpClientErrorException e) {
      log.warnEx("Problem retrieving ROR", e);
      return of("The ROR does not exist.");
    }
    
    return emptyList();
  }
  
}
