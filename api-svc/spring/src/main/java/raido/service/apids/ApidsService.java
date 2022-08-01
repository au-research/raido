package raido.service.apids;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import raido.service.apids.model.ApidsMintResponse;
import raido.service.apids.model.RawXml;
import raido.spring.config.environment.ApidsProps;
import raido.util.Log;
import raido.util.RestUtil;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static raido.util.Log.to;

/**
 I think this is https://github.com/au-research/ANDS-PIDS-Service, not sure.
 "ANDS" was the old org name for what is now ARDC.
 */
@Service
public class ApidsService {
  private static final Log log = to(ApidsService.class);
  private static final Log httpLog = to(ApidsService.class, "http");

  private ApidsProps props;
  private RestTemplate rest;

  public ApidsService(ApidsProps props, RestTemplate rest) {
    this.props = props;
    this.rest = rest;
  }

  public ApidsMintResponse mintApidsHandle() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_XML);
    headers.setBasicAuth(props.appId, props.secret);

    var entity = new HttpEntity<>(
      buildBasicAuthorizedMintBody(), headers);

    var responseBody = RestUtil.logExchange(
      httpLog, "APIDS mint", 
      entity, 
      e ->rest.exchange(
        props.serviceUrl, POST, entity, ApidsMintResponse.class) );

    return responseBody;
  }

  private RawXml buildBasicAuthorizedMintBody() {
    return new RawXml("""
      <request name="mint">
          <properties>
              <property name="identifier" value="raid"/>
              <property name="authDomain" value="raid.org.au"/>
          </properties>
      </request>""".trim());
  }

}
