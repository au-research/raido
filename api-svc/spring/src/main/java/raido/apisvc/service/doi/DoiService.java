package raido.apisvc.service.doi;

import org.springframework.http.RequestEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.util.Log;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.Collections.emptyList;
import static java.util.List.of;
import static java.util.regex.Pattern.compile;
import static raido.apisvc.spring.bean.LogMetric.VALIDATE_DOI_EXISTS;
import static raido.apisvc.util.ExceptionUtil.illegalArgException;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.ObjectUtil.infoLogExecutionTime;

public class DoiService {
  public static final Pattern DOI_REGEX = 
    compile("^http[s]?://doi.org/10\\..*");
  
  private static final Log log = to(DoiService.class);
  protected static final Log httpLog = to(DoiService.class, "http");

  private RestTemplate rest;

  public DoiService(RestTemplate rest) {
    this.rest = rest;
  }

  public List<String> validateDoiExists(String doi) {

    /* SSRF prevention - keep this "near" the actual HTTP call so 
    that static analysis tools understand it's protected */
    if( !DOI_REGEX.matcher(doi).matches() ){
      throw illegalArgException("DOI failed SSRF prevention");
    }
    
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
