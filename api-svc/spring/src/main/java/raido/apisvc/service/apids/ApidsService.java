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
import static raido.apisvc.util.ExceptionUtil.re;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.StringUtil.*;

/**
 Confirmed by DevOps (Leo) on 2022-08-09:
 - correct repo: https://github.com/au-research/ANDS-PIDS-Service.
 - can set url at mint time, but can only do one property
   - if want to description at minting will need a second request
     to `addValue`, where you can just pass type and value params
   -https://github.com/au-research/ANDS-Registry-Core/blob/1140c19d798efa0b82704071484928639f8510ad/applications/apps/pids/models/_pids.php#L184
 - when want to update an url, need to use `modifyValueByIndex`, which 
   takes type, value and index.
 <p/>
 Consider replacing RestTemplate with feign client, to avoid silly string ops.
 And for consistency with other services, when we have them.
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

  public ApidsMintResponse mintApidsHandle(String contentUrl) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_XML);
    headers.setBasicAuth(props.appId, props.secret);

    var entity = new HttpEntity<>(
      buildBasicAuthorizedMintBody(), headers);

    String requestUrl = props.serviceUrl + "?" + formatMintParams(contentUrl);
    var responseBody = RestUtil.logExchange( httpLog, "APIDS mint",
      entity,
      e->rest.exchange(
        requestUrl, POST, entity, ApidsMintResponse.class ));

    guardApidsResponse(contentUrl, responseBody);

    log.with("handle", responseBody.identifier.handle).
      info("RAID minted with APIDS handle");
    
    return responseBody;
  }
  
  public static String formatMintParams(String url){
    /* lambda code was hardcoded to:
     `type=URL&value=https://www.raid.org.au/` */
    return "type=URL&value=%s".formatted(url);
  }
  
  private void guardApidsResponse(
    String contentUrl, 
    ApidsMintResponse response
  ){
    Guard.notNull("APIDS mint response was null", response);
  
    if( !equalsIgnoreCase("success", response.type) ){
      throw re("APIDS responded with minting %s: %s", 
        response.type, response.message );
    }
    
    Guard.notNull(response.identifier);
    Guard.hasValue(response.identifier.handle);
    Guard.notNull(response.identifier.property);
    Guard.areEqual(response.identifier.property.index, 1);
    Guard.areEqual(response.identifier.property.type, "URL");
    Guard.hasValue(response.identifier.property.value);

    if( !areEqual(contentUrl, response.identifier.property.value) ){
      throw re("APIDS mint returned different URL, sent=%s recieved=%s",
        contentUrl, response.identifier.property.value );
    }
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
