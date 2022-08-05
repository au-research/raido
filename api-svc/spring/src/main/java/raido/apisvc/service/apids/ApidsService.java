package raido.apisvc.service.apids;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import raido.apisvc.service.apids.model.ApidsMintResponse;
import raido.apisvc.service.apids.model.RawXml;
import raido.apisvc.spring.config.environment.ApidsProps;
import raido.apisvc.util.Guard;
import raido.apisvc.util.Log;
import raido.apisvc.util.RestUtil;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static raido.apisvc.util.Log.to;

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

    guardApidsResponse(responseBody);
    
    return responseBody;
  }
  
  private void guardApidsResponse(ApidsMintResponse response){
    Guard.notNull(response);
    Guard.notNull(response.identifier);
    Guard.hasValue(response.identifier.handle);
    Guard.notNull(response.identifier.property);
    Guard.areEqual(response.identifier.property.index, 1);
    Guard.areEqual(response.identifier.property.type, "URL");
    Guard.hasValue(response.identifier.property.value);
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
