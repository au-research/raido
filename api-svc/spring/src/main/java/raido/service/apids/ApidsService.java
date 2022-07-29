package raido.service.apids;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import raido.spring.config.environment.ApidsProps;
import raido.util.Log;
import raido.util.RestUtil;

import static raido.util.Log.to;

/**
 I think this is https://github.com/au-research/ANDS-PIDS-Service, not sure.
 "ANDS" was the old org name for what is now ARDC.
 */
@Service
public class ApidsService {
  private static final Log log = to(ApidsService.class);
  
  private ApidsProps props;
  private RestTemplate rest;
  

  public ApidsService(ApidsProps props, RestTemplate rest) {
    this.props = props;
    this.rest = rest;
  }

  static class Handle {
    
  }
  
  public Handle createAndsHandle() {
    HttpHeaders headers = RestUtil.createBasicHeaders(
      props.appId, props.secret);
    headers.set("Content-Type", "application/xml");
      
    HttpEntity<String> entity = new HttpEntity<String>(
      buildBasicAuthorizedMintBody(), headers);

    ResponseEntity<String> response = rest.exchange(
      props.serviceUrl,
      HttpMethod.POST, entity, String.class);

    log.info("APIDS response: " + response.getBody());
    
    return null;
  }

  private String buildBasicAuthorizedMintBody()  {
    return """
<request name="mint">
    <properties>
        <property name="identifier" value="raid"/>
        <property name="authDomain" value="raid.org.au"/>
    </properties>
</request>
""".trim();
  }

}
