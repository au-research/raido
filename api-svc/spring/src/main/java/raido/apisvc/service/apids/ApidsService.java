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

import java.util.function.Function;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static raido.apisvc.util.ExceptionUtil.re;
import static raido.apisvc.util.Log.to;
import static raido.apisvc.util.RestUtil.urlDecode;
import static raido.apisvc.util.RestUtil.urlEncode;
import static raido.apisvc.util.StringUtil.*;

/**
 Confirmed by DevOps (Leo) on 2022-08-09:
 - correct repo: https://github.com/au-research/ANDS-PIDS-Service.
 - can set url at mint time, but can only do one property
   - if want to add description at minting will need a second request
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
  public static final String RAID_HANDLE_DESC = "RAID+handle";

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

    var mintEntity = new HttpEntity<>(
      buildBasicAuthorizedMintBody(), headers);

    String requestUrl = props.serviceUrl + "/mint?" + 
      formatMintParams(contentUrl);
    var mintResponse = RestUtil.logExchange( httpLog, "APIDS mint with url",
      mintEntity,
      e->rest.exchange(
        requestUrl, POST, mintEntity, ApidsMintResponse.class ));

    guardApidsResponse(mintResponse);
    Guard.notNull(mintResponse.identifier.property);
    Guard.areEqual(mintResponse.identifier.property.index, 1);
    Guard.areEqual(mintResponse.identifier.property.type, "URL");
    Guard.hasValue(mintResponse.identifier.property.value);
    if( !areEqual(contentUrl, mintResponse.identifier.property.value) ){
      throw re("APIDS mint returned different URL, sent=%s recieved=%s",
        contentUrl, mintResponse.identifier.property.value );
    }

    log.with("handle", mintResponse.identifier.handle).
      info("RAID minted with APIDS handle");
    
    return mintResponse;
  }
  
  public ApidsMintResponse mintApidsHandleContentPrefix(
    Function<String, String> raidLandingPageUrl
  ) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(APPLICATION_XML);
    headers.setBasicAuth(props.appId, props.secret);

    var mintEntity = new HttpEntity<>(
      buildBasicAuthorizedMintBody(), headers);

    ///////////////////// create handle 
    
    String mintRequest = props.serviceUrl + "/mint" + 
      "?type=DESC" +
      "&value=" + RAID_HANDLE_DESC;
    var mintResponse = RestUtil.logExchange( httpLog, "APIDS mint with desc",
      mintEntity,
      e->rest.exchange(
        mintRequest, POST, mintEntity, ApidsMintResponse.class ));

    guardApidsResponse(mintResponse);
    Guard.notNull(mintResponse.identifier.property);
    Guard.areEqual(mintResponse.identifier.property.index, 1);
    Guard.areEqual(mintResponse.identifier.property.type, "DESC");
    Guard.hasValue(mintResponse.identifier.property.value);
    if( !areEqual(urlDecode(RAID_HANDLE_DESC), mintResponse.identifier.property.value) ){
      throw re("APIDS mint returned different DESC, sent=`%s` recieved=`%s`",
        urlDecode(RAID_HANDLE_DESC), mintResponse.identifier.property.value );
    }
    log.with("handle", mintResponse.identifier.handle).
      info("RAID minted with APIDS handle");

    
    ///////////////////// update url to point to landing page 

    var addValueEntity = new HttpEntity<>(
      buildBasicAuthorizedAddValueBody(), headers);
    String url = raidLandingPageUrl.apply(mintResponse.identifier.handle);
    String addValueRequest = props.serviceUrl + "/addValue?" + 
      "type=URL&value=%s&handle=%s".formatted( 
        url, mintResponse.identifier.handle );

    var addResponse = RestUtil.logExchange( httpLog, "APIDS addValue",
      addValueEntity,
      e->rest.exchange(
        addValueRequest, POST, addValueEntity, ApidsMintResponse.class ));
    
    guardApidsResponse(addResponse);
    Guard.notNull(addResponse.identifier.property);
    Guard.areEqual(addResponse.identifier.property.index, 2);
    Guard.areEqual(addResponse.identifier.property.type, "URL");
    Guard.hasValue(addResponse.identifier.property.value);
    if( !areEqual(url, addResponse.identifier.property.value) ){
      throw re("APIDS mint returned different URL, sent=%s recieved=%s",
        url, addResponse.identifier.property.value );
    }    
    return addResponse;
  }
  
  public static String formatMintParams(String url){
    /* lambda code was hardcoded to:
     `type=URL&value=https://www.raid.org.au/` */
    return "type=URL&value=%s".formatted(url);
  }
  
  private void guardApidsResponse(
    ApidsMintResponse response
  ){
    Guard.notNull("APIDS mint response was null", response);
  
    if( !equalsIgnoreCase("success", response.type) ){
      throw re("APIDS responded with minting %s: %s", 
        response.type, response.message );
    }
    
    Guard.notNull(response.identifier);
    Guard.hasValue(response.identifier.handle);

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

  private RawXml buildBasicAuthorizedAddValueBody() {
    return new RawXml("""
      <request name="addValue">
          <properties>
              <property name="identifier" value="raid"/>
              <property name="authDomain" value="raid.org.au"/>
          </properties>
      </request>""".trim());
  }

}
